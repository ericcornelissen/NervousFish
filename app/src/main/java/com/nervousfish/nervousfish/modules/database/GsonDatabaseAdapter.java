package com.nervousfish.nervousfish.modules.database;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.nervousfish.nervousfish.data_objects.Contact;
import com.nervousfish.nervousfish.data_objects.Database;
import com.nervousfish.nervousfish.data_objects.DatabasePass;
import com.nervousfish.nervousfish.data_objects.IKey;
import com.nervousfish.nervousfish.data_objects.Profile;
import com.nervousfish.nervousfish.modules.cryptography.EncryptedSaver;
import com.nervousfish.nervousfish.modules.filesystem.IFileSystem;
import com.nervousfish.nervousfish.service_locator.IServiceLocator;
import com.nervousfish.nervousfish.service_locator.ModuleWrapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Type;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.SecretKey;


/**
 * An adapter to the GSON database library. We suppress the TooManyMethods warning of PMD because a
 * DatabaseHandler has a lot of methods by nature and refactoring it to multiple classes or single
 * methods with more logic would make the class only less understandable. We also suppress
 * ClassDataAbstractionCoupling of checkstyle because of the by the pojo's created to write safely,
 * we have to import this much.
 */
@SuppressWarnings({"checkstyle:ClassDataAbstractionCoupling", "PMD.TooManyMethods"})
public final class GsonDatabaseAdapter implements IDatabase {

    private static final long serialVersionUID = -4101015873770268925L;
    private static final String CONTACT_NOT_FOUND = "Contact not found in database";
    private static final String CONTACT_DUPLICATE = "Contact is already in the database";
    private static final String DATABASE_NOT_CREATED = "Database is not created";
    private static final String DATABASE_WRONG_SIZE = "Database has the wrong size";
    private static final String BAD_KEY_SPEC = "Password leads to bad key spec";
    private static final String BAD_PADDING = "Database has bad padding";

    //  Strings for saving database related objects in the map
    private static final String DATABASE = "database";
    private static final String DATABASE_PASS = "database pass";
    private static final String ENCRYPTION_KEY = "encryption key";
    private static final Logger LOGGER = LoggerFactory.getLogger("GsonDatabaseAdapter");
    private static final Type TYPE_DATABASE = new TypeToken<Database>() {
    }.getType();

    private final String databasePath;
    private final String passwordPath;

    private final Map<String, Object> databaseMap;

    private final IFileSystem fileSystem;
    private final GsonDatabaseAdapterHelper helper;


    /**
     * Prevents construction from outside the class.
     *
     * @param serviceLocator Can be used to get access to other modules
     */
    private GsonDatabaseAdapter(final IServiceLocator serviceLocator) {
        this.fileSystem = serviceLocator.getFileSystem();
        this.databaseMap = new HashMap<>();
        this.databasePath = serviceLocator.getConstants().getDatabasePath();
        this.passwordPath = serviceLocator.getConstants().getPasswordPath();
        this.helper = new GsonDatabaseAdapterHelper(serviceLocator);
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

        this.updateDatabase();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteContact(final String contactName) throws IOException {
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

        this.updateDatabase();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateContact(final Contact oldContact, final Contact newContact) throws IllegalArgumentException, IOException {
        final Database database = getDatabase();
        final List<Contact> contacts = database.getContacts();
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
    public List<Contact> getAllContacts() throws IOException {
        return getDatabase().getContacts();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Contact getContactWithName(final String contactName) throws IOException {
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
    public boolean contactExists(final String name) throws IOException {
        return getContactWithName(name) != null;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public Profile getProfile() throws IOException {
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
        try {
            final GsonBuilder gsonBuilder = new GsonBuilder().registerTypeHierarchyAdapter(IKey.class, new GsonKeyAdapter());
            final Gson gsonParser = gsonBuilder.create();

            final String databaseJson = gsonParser.toJson(getDatabase());
            final String encryptedDatabase = EncryptedSaver.encryptOrDecryptWithPassword(databaseJson, getEncryptionKey(), true);
            final Writer writer = this.fileSystem.getWriter(databasePath);
            writer.write(encryptedDatabase);
            writer.close();
        } catch (IllegalBlockSizeException e) {
            LOGGER.error("Wrong size of password", e);
            throw new IOException("Password is wrong size", e);
        } catch (BadPaddingException e) {
            LOGGER.error(BAD_PADDING, e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void loadDatabase(final String password) throws IOException, RuntimeException {
        LOGGER.info("Started loading database");
        final GsonBuilder gsonBuilder = new GsonBuilder().registerTypeHierarchyAdapter(IKey.class, new GsonKeyAdapter());
        final Gson gsonParser = gsonBuilder.create();
        helper.checkPassword(password);

        try {

            final String databaseFileString = helper.readDatabaseToString();
            final SecretKey key = EncryptedSaver.makeKey(password);
            databaseMap.put(ENCRYPTION_KEY, key);
            final String databaseJson = EncryptedSaver.encryptOrDecryptWithPassword(databaseFileString, key, false);
            final Database database = gsonParser.fromJson(databaseJson, TYPE_DATABASE);
            databaseMap.put(DATABASE, database);
        } catch (InvalidKeySpecException e) {
            throw new IOException(BAD_KEY_SPEC, e);
        } catch (IllegalBlockSizeException e) {
            throw new IOException(DATABASE_WRONG_SIZE, e);
        } catch (BadPaddingException e) {
            LOGGER.error(BAD_PADDING, e);
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void createDatabase(final Profile profile, final String password) throws IOException {
        LOGGER.info("Started creating database for profile");
        initializePassword(password);
        initializeDatabase(profile, password);
        LOGGER.info("Database successfully created");

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean checkFirstUse() {
        final File filePass = new File(passwordPath);
        final File fileDb = new File(databasePath);
        return filePass.exists() && fileDb.exists();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteDatabase() {
        LOGGER.info("Deleting database");
        if (checkFirstUse()) {
            final File filePass = new File(passwordPath);
            final File fileDb = new File(databasePath);
            if (!(filePass.delete() && fileDb.delete())) {
                deleteDatabase();
                LOGGER.info("Something went wrong");
            }
        }
    }


    /**
     * Initialize the main database. Does nothing when file is present.
     *
     * @param profile  The profile to initialize the database for.
     * @param password The password to initialize the database with.
     */
    private void initializeDatabase(final Profile profile, final String password) throws IOException {
        LOGGER.info("Database path created: " + databasePath);

        databaseMap.put(DATABASE, new Database(new ArrayList<Contact>(), profile));


        final Writer writer = this.fileSystem.getWriter(databasePath);

        final GsonBuilder gsonBuilder = new GsonBuilder().registerTypeHierarchyAdapter(IKey.class, new GsonKeyAdapter());
        final Gson gsonParser = gsonBuilder.create();

        final String databaseJson = gsonParser.toJson(getDatabase());
        LOGGER.info("Database translated to json: " + databaseJson);

        try {
            final SecretKey key = EncryptedSaver.makeKey(password);
            final String databaseEncrypted = EncryptedSaver.encryptOrDecryptWithPassword(databaseJson, key, true);

            writer.write(databaseEncrypted);
            writer.close();
            LOGGER.info("Created the database: %s", this.databasePath);
        } catch (InvalidKeySpecException e) {
            throw new IOException(BAD_KEY_SPEC, e);
        } catch (IllegalBlockSizeException e) {
            throw new IOException(DATABASE_WRONG_SIZE, e);
        } catch (BadPaddingException e) {
            LOGGER.error(BAD_PADDING, e);
        }
    }

    /**
     * Initialize the password file with the keys to encrypt the database. When there's no file yet
     * it creates a new file and a new keypair. Does nothing when file is present.
     *
     * @param password The password to initialize the database with.
     */
    private void initializePassword(final String password) throws IOException {
        LOGGER.info("Initializing password");
        databaseMap.put(DATABASE_PASS, new DatabasePass(password));


        final DatabasePass databasePass = getDatabasePass();
        final GsonBuilder gsonBuilder = new GsonBuilder().registerTypeHierarchyAdapter(IKey.class, new GsonKeyAdapter());
        final Gson gsonParser = gsonBuilder.create();
        final String databasePassJson = gsonParser.toJson(databasePass);
        LOGGER.info("Database pass: " + databasePassJson);

        final Writer writer = this.fileSystem.getWriter(passwordPath);

        writer.write(databasePassJson);
        writer.close();
        LOGGER.info("Created the password file: %s", this.passwordPath);

    }


    /**
     * Gets the databasePass from the databaseMap
     *
     * @throws IOException throws IOException if the database isn't loaded yet.
     */
    private DatabasePass getDatabasePass() throws IOException {
        final Object object = databaseMap.get(DATABASE_PASS);
        if (object instanceof DatabasePass) {
            return (DatabasePass) object;
        } else {
            throw new IOException(DATABASE_NOT_CREATED);
        }
    }

    /**
     * Gets the database from the databaseMap
     *
     * @throws IOException throws IOException if the database isn't loaded yet.
     */
    private Database getDatabase() throws IOException {
        final Object object = databaseMap.get(DATABASE);
        if (object instanceof Database) {
            return (Database) object;
        } else {
            throw new IOException(DATABASE_NOT_CREATED);
        }
    }

    /**
     * Gets the encryption key from the databaseMap
     *
     * @throws IOException throws IOException if the database isn't loaded yet.
     */
    private SecretKey getEncryptionKey() throws IOException {
        final Object object = databaseMap.get(ENCRYPTION_KEY);
        if (object instanceof SecretKey) {
            return (SecretKey) object;
        } else {
            throw new IOException(DATABASE_NOT_CREATED);
        }
    }


}
