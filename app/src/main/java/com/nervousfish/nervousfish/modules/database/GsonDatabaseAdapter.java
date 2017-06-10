package com.nervousfish.nervousfish.modules.database;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.nervousfish.nervousfish.data_objects.Contact;
import com.nervousfish.nervousfish.data_objects.Database;
import com.nervousfish.nervousfish.data_objects.DatabasePass;
import com.nervousfish.nervousfish.data_objects.IKey;
import com.nervousfish.nervousfish.data_objects.Profile;
import com.nervousfish.nervousfish.exceptions.DatabaseAlreadyExistsException;
import com.nervousfish.nervousfish.modules.constants.IConstants;
import com.nervousfish.nervousfish.modules.cryptography.EncryptedSaver;
import com.nervousfish.nervousfish.modules.cryptography.IKeyGenerator;
import com.nervousfish.nervousfish.modules.filesystem.IFileSystem;
import com.nervousfish.nervousfish.service_locator.IServiceLocator;
import com.nervousfish.nervousfish.service_locator.ModuleWrapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Writer;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.SecretKey;

import static org.junit.Assert.assertNotNull;

/**
 * An adapter to the GSON database library. We suppress the TooManyMethods warning of PMD because a
 * DatabaseHandler has a lot of methods by nature and refactoring it to multiple classes or single
 * methods with more logic would make the class only less understandable.
 */
@SuppressWarnings("PMD.TooManyMethods")
public final class GsonDatabaseAdapter implements IDatabase {

    private static final long serialVersionUID = -4101015873770268925L;
    private static final String CONTACT_NOT_FOUND = "Contact not found in database";
    private static final String CONTACT_DUPLICATE = "Contact is already in the database";
    private static final String DATABASE_NOT_CREATED = "Database is not created";


    //  Strings for saving database related objects in the map
    private final String databasePath;
    private final String passwordPath;
    private static final String DATABASE = "database";
    private static final String DATABASE_PASS = "database pass";
    private static final String ENCRYPTION_KEY = "encryption key";

    private static final Logger LOGGER = LoggerFactory.getLogger("GsonDatabaseAdapter");
    private static final Type TYPE_DATABASE = new TypeToken<Database>() {
    }.getType();
    private static final Type TYPE_DATABASE_PASS = new TypeToken<DatabasePass>() {
    }.getType();

    private final Map<String, Object> databaseMap;

    private final IFileSystem fileSystem;


    /**
     * Prevents construction from outside the class.
     *
     * @param serviceLocator Can be used to get access to other modules
     */
    private GsonDatabaseAdapter(final IServiceLocator serviceLocator) {
        final IConstants constants = serviceLocator.getConstants();
        this.fileSystem = serviceLocator.getFileSystem();
        this.databaseMap = new HashMap<String, Object>();
        this.databasePath = serviceLocator.getConstants().getDatabasePath();
        this.passwordPath = serviceLocator.getConstants().getPasswordPath();
        LOGGER.info("Initialized");
    }

    /**
     * Creates a new instance of itself and wraps it in a {@link ModuleWrapper} so that only an
     * {@link IServiceLocator}
     *
     * @param serviceLocator The new service locator
     * @return A wrapper around a newly created instance of this class
     */
    public static ModuleWrapper<GsonDatabaseAdapter> newInstance(final IServiceLocator serviceLocator) {
        return new ModuleWrapper<>(new GsonDatabaseAdapter(serviceLocator));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addContact(final Contact contact) throws IOException {
        LOGGER.info("Adding new contact with name: " + contact.getName());
        if (this.contactExists(contact.getName())) {
            throw new IllegalArgumentException(CONTACT_DUPLICATE);
        }
        LOGGER.info("Contact added");

        final List<Contact> contacts = this.getAllContacts();
        contacts.add(contact);

        this.updateContacts(contacts);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteContact(final String contactName)  throws IOException {
        final List<Contact> contacts = this.getAllContacts();
        final int lengthBefore = contacts.size();
        for (final Contact contact : contacts) {
            if (contactName.equals(contact.getName())) {
                contacts.remove(contact);
                break;
            }
        }

        // Throw if the contact to remove is not found
        if (contacts.size() == lengthBefore) {
            throw new IllegalArgumentException(CONTACT_NOT_FOUND);
        }

        this.updateContacts(contacts);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateContact(Contact oldContact, Contact newContact) throws IllegalArgumentException, IOException {
        Database database = getDatabase();
        List<Contact> contacts = database.getContacts();
        final int lengthBefore = contacts.size();
        contacts.remove(oldContact);

        // Throw if the contact to update is not found
        if (contacts.size() == lengthBefore) {
            throw new IllegalArgumentException(CONTACT_NOT_FOUND);
        }

        contacts.add(newContact);
        updateDatabase();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public List<Contact> getAllContacts() throws IOException{

        return getDatabase().getContacts();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Contact getContactWithName(final String contactName)  throws IOException{
        final List<Contact> contacts = getAllContacts();
        for (final Contact contact : contacts) {
            if (contact.getName().equals(contactName)) {
                return contact;
            }
        }

        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean contactExists(final String name)  throws IOException{
        return getContactWithName(name) != null;
    }

    /**
     * Update the contact contents of the database.
     *
     * @param contacts The list of contacts to write.
     */
    private void updateContacts(final List<Contact> contacts)  throws IOException {
        getDatabase().setContacts(contacts);
        updateDatabase();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Profile getProfile() throws IOException{
        return getDatabase().getProfile();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateProfile(final Profile newProfile) throws IOException {
        getDatabase().setProfile(newProfile);
        updateDatabase();

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateDatabase() throws IOException {
        final GsonBuilder gsonBuilder = new GsonBuilder().registerTypeHierarchyAdapter(IKey.class, new GsonKeyAdapter());
        final Gson gsonParser = gsonBuilder.create();

        String databaseJson = gsonParser.toJson(getDatabase());
        String encryptedDatabase = EncryptedSaver.encryptOrDecryptWithPassword(databaseJson, getEncryptionKey(), true);
        final Writer writer = this.fileSystem.getWriter(databasePath);
        writer.write(encryptedDatabase);
        writer.close();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void loadDatabase(String password) throws IOException, RuntimeException {
        LOGGER.info("Started loading database");
        final GsonBuilder gsonBuilder = new GsonBuilder().registerTypeHierarchyAdapter(IKey.class, new GsonKeyAdapter());
        final Gson gsonParser = gsonBuilder.create();

        // Get the keypair from the password file
        final String passwordPath = this.passwordPath;
        final BufferedReader passReader = (BufferedReader) this.fileSystem.getReader(passwordPath);
        String line;
        String passwordFileString = "";
        while ((line = passReader.readLine()) != null) {
            passwordFileString += line;
        }

        LOGGER.info("JSON "+ passwordFileString);
        final DatabasePass databasePass =  gsonParser.fromJson(passwordFileString, TYPE_DATABASE_PASS);
        if(!databasePass.getEncryptedPassword().equals(EncryptedSaver.hashWithoutSalt(password))) {
            throw new IOException("Password is wrong");
        }

        passReader.close();


        // Get the database from the database file
        final BufferedReader databaseReader = (BufferedReader) this.fileSystem.getReader(databasePath);

        String databaseFileString = "";

        while ((line = databaseReader.readLine()) != null) {
            databaseFileString += line;
        }

        final SecretKey key = EncryptedSaver.makeKey(password);
        databaseMap.put(ENCRYPTION_KEY, key);
        final String databaseJson = EncryptedSaver.encryptOrDecryptWithPassword(databaseFileString, key, false);
        final Database database = gsonParser.fromJson(databaseJson, TYPE_DATABASE);
        databaseMap.put(DATABASE, database);
        databaseReader.close();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void createDatabase(Profile profile, String password) throws IOException {
        LOGGER.info("Started creating database for profile");
        if (databaseMap.get(DATABASE) == null) {
            initializePassword(password);
            initializeDatabase(profile, password);
            LOGGER.info("Database successfully created");
        } else {
            throw new DatabaseAlreadyExistsException("There already is a database");
        }
    }




    /**
     * Initialize the main database. Does nothing when file is present.
     * @param profile The profile to initialize the database for.
     * @param password The password to initialize the database with.
     */
    private void initializeDatabase(Profile profile, String password) throws IOException {
        LOGGER.info("Database path created: " + databasePath);
        File file = new File(databasePath);


        if (file.exists()) {
            LOGGER.info("Database file already exists");
            return;
        }
        LOGGER.info("There is no Database file yet");
        databaseMap.put(DATABASE, new Database(new ArrayList<Contact>(), profile));


        final Writer writer = this.fileSystem.getWriter(databasePath);

        final GsonBuilder gsonBuilder = new GsonBuilder().registerTypeHierarchyAdapter(IKey.class, new GsonKeyAdapter());
        final Gson gsonParser = gsonBuilder.create();

        final String databaseJson = gsonParser.toJson(getDatabase());
        LOGGER.info("Database translated to json: " + databaseJson);

        final SecretKey key = EncryptedSaver.makeKey(password);
        final String databaseEncrypted = EncryptedSaver.encryptOrDecryptWithPassword(databaseJson, key, true);

        writer.write(databaseEncrypted);
        writer.close();
        LOGGER.info("Created the database: %s", this.databasePath);


    }

    /**
     * Initialize the password file with the keys to encrypt the database. When there's no file yet
     * it creates a new file and a new keypair. Does nothing when file is present.
     * @param password The password to initialize the database with.
     */
    private void initializePassword(String password) throws IOException {
        LOGGER.info("Initializing password");
        final String passwordPath = this.passwordPath;

        File file = new File(passwordPath);

        if (file.exists()) {
            LOGGER.info("Password pass file already exists");
            return;
        }

        LOGGER.info("Password path doesn't exist yet");
        databaseMap.put(DATABASE_PASS, new DatabasePass(password));


        final DatabasePass databasePass = getDatabasePass();
        final GsonBuilder gsonBuilder = new GsonBuilder().registerTypeHierarchyAdapter(IKey.class, new GsonKeyAdapter());
        final Gson gsonParser = gsonBuilder.create();
        String databasePassJson = gsonParser.toJson(databasePass);
        LOGGER.info("Database pass: " + databasePassJson);

        final Writer writer = this.fileSystem.getWriter(passwordPath);

        writer.write(databasePassJson);
        writer.close();
        LOGGER.info("Created the password file: %s", this.passwordPath);

    }


    /**
     * Gets the databasePass from the databaseMap
     * @throws IOException throws IOException if the database isn't loaded yet.
     */
    private DatabasePass getDatabasePass() throws IOException {
        Object object = databaseMap.get(DATABASE_PASS);
        if(object instanceof DatabasePass) {
            DatabasePass db = (DatabasePass) object;
            return db;
        } else {
            throw new IOException(DATABASE_NOT_CREATED);
        }
    }

    /**
     * Gets the database from the databaseMap
     * @throws IOException throws IOException if the database isn't loaded yet.
     */
    private Database getDatabase() throws IOException {
        Object object = databaseMap.get(DATABASE);
        if(object instanceof Database) {
            Database db = (Database) object;
            return db;
        } else {
            throw new IOException(DATABASE_NOT_CREATED);
        }
    }

    /**
     * Gets the encryption key from the databaseMap
     * @throws IOException throws IOException if the database isn't loaded yet.
     */
    private SecretKey getEncryptionKey() throws IOException {
        Object object = databaseMap.get(ENCRYPTION_KEY);
        if(object instanceof SecretKey) {
            SecretKey sk = (SecretKey) object;
            return sk;
        } else {
            throw new IOException(DATABASE_NOT_CREATED);
        }
    }




}
