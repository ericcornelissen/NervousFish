package com.nervousfish.nervousfish.modules.database;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.nervousfish.nervousfish.data_objects.Contact;
import com.nervousfish.nervousfish.data_objects.Database;
import com.nervousfish.nervousfish.data_objects.DatabasePass;
import com.nervousfish.nervousfish.data_objects.IKey;
import com.nervousfish.nervousfish.data_objects.KeyPair;
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
    private static final String DATABASE_PATH = "database path";
    private static final String PASSWORD_PATH = "databaseKey.txt";
    private static final String DATABASE = "database";
    private static final String DATABASE_PASS = "database pass";
    private static final String PUBLIC_KEY = "public key";

    private final String passwordPath;
    private final String androidFilesDir;

    private static final Logger LOGGER = LoggerFactory.getLogger("GsonDatabaseAdapter");
    private static final Type TYPE_DATABASE = new TypeToken<Database>() {
    }.getType();
    private static final Type TYPE_DATABASE_PASS = new TypeToken<DatabasePass>() {
    }.getType();

    private final Map<String, Object> databaseMap;

    private final IFileSystem fileSystem;
    private final IKeyGenerator keyGenerator;

    private final byte[] ivSpec;
    private final int randomSeed = 1234569;

    /**
     * Prevents construction from outside the class.
     *
     * @param serviceLocator Can be used to get access to other modules
     */
    private GsonDatabaseAdapter(final IServiceLocator serviceLocator) {
        final IConstants constants = serviceLocator.getConstants();
        this.fileSystem = serviceLocator.getFileSystem();
        this.keyGenerator = serviceLocator.getKeyGenerator();
        this.databaseMap = new HashMap<String, Object>();
        this.androidFilesDir = serviceLocator.getAndroidFilesDir();
        this.ivSpec = EncryptedSaver.generateSalt(randomSeed);
        this.passwordPath = androidFilesDir + PASSWORD_PATH;
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
    public void addContact(final Contact contact)  throws IOException {
        if (this.contactExists(contact.getName())) {
            throw new IllegalArgumentException(CONTACT_DUPLICATE);
        }

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
        String encryptedDatabase = EncryptedSaver.encryptUsingRSA(databaseJson, getPublicKey());
        final Writer writer = this.fileSystem.getWriter(getDatabasePath());
        writer.write(encryptedDatabase);
        writer.close();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void loadDatabase(String password) throws IOException, RuntimeException {

        final GsonBuilder gsonBuilder = new GsonBuilder();
        final Gson gsonParser = gsonBuilder.create();

        // Get the keypair from the password file
        final String passwordPath = this.passwordPath;
        final BufferedReader passReader = (BufferedReader) this.fileSystem.getReader(passwordPath);
        String line;
        String passwordFileString = "";
        while ((line = passReader.readLine()) != null) {
            passwordFileString += line;
        }
        final String databasePassJson = new String(EncryptedSaver.encryptOrDecryptWithPassword(passwordFileString.getBytes(),
                password, ivSpec, false));
        final DatabasePass databasePass =  gsonParser.fromJson(databasePassJson, TYPE_DATABASE_PASS);
        if(!databasePass.getEncryptedPassword().equals(EncryptedSaver.hashWithoutSalt(password))) {
            throw new IOException("Password is wrong");
        }
        final KeyPair keyPair = databasePass.getKeyPair();
        databaseMap.put(PUBLIC_KEY, keyPair.getPublicKey());

        passReader.close();


        // Get the database from the database file
        final String databasePath = getDatabasePath();
        final BufferedReader databaseReader = (BufferedReader) this.fileSystem.getReader(databasePath);

        String databaseFileString = "";

        while ((line = databaseReader.readLine()) != null) {
            databaseFileString += line;
        }
        final String databaseJson = new String(EncryptedSaver.decryptUsingRSA(databaseFileString, keyPair.getPrivateKey()));
        final Database database = gsonParser.fromJson(databaseJson, TYPE_DATABASE);
        databaseMap.put(DATABASE, database);
        databaseReader.close();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void createDatabase(Profile profile, String password) throws IOException {
        if (databaseMap.get(DATABASE) == null) {
            databaseMap.put(DATABASE, new Database(new ArrayList<Contact>(), profile));

            KeyPair lockpair = keyGenerator.generateRSAKeyPair(DATABASE);
            databaseMap.put(DATABASE_PASS, new DatabasePass(lockpair, password));

            initializePassword(password);
            initializeDatabase(password);
        } else {
            throw new DatabaseAlreadyExistsException("There already is a database");
        }
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
     * Initialize the main database. Does nothing when file is present.
     * @param password The password to initialize the database with.
     */
    private void initializeDatabase(String password) throws IOException {
        final String databasePath = this.androidFilesDir + EncryptedSaver.hashWithoutSalt(DATABASE_PATH+password);

        File file = new File(databasePath);
        databaseMap.put(DATABASE_PATH, databasePath);

        if (file.exists()) {
            return;
        }

        final Writer writer = this.fileSystem.getWriter(databasePath);

        final GsonBuilder gsonBuilder = new GsonBuilder().registerTypeHierarchyAdapter(IKey.class, new GsonKeyAdapter());
        final Gson gsonParser = gsonBuilder.create();

        final String databaseJson = gsonParser.toJson(getDatabase());
        writer.write(databaseJson);
        writer.close();
        LOGGER.info("Created the database: %s", this.getDatabasePath());


    }

    /**
     * Initialize the password file with the keys to encrypt the database. When there's no file yet
     * it creates a new file and a new keypair. Does nothing when file is present.
     * @param password The password to initialize the database with.
     */
    private void initializePassword(String password) throws IOException {
        final String passwordPath = PASSWORD_PATH;

        File file = new File(passwordPath);

        if (file.exists()) {
            return;
        }

        DatabasePass databasePass = getDatabasePass();
        final GsonBuilder gsonBuilder = new GsonBuilder().registerTypeHierarchyAdapter(IKey.class, new GsonKeyAdapter());
        final Gson gsonParser = gsonBuilder.create();
        String lockpairJson = gsonParser.toJson(databasePass);


        final Writer writer = this.fileSystem.getWriter(passwordPath);
        writer.write(new String(EncryptedSaver.encryptOrDecryptWithPassword(lockpairJson.getBytes(),
                password, ivSpec, true)));
        writer.close();
        LOGGER.info("Created the password file: %s", this.passwordPath);


    }


    /**
     * Deserialize the instance using readObject to ensure invariants and security.
     * @param stream The serialized object to be deserialized
     */
    private void readObject(final ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        ensureClassInvariant();
    }

    /**
     * Ensure that the instance meets its class invariant
     * @throws InvalidObjectException Thrown when the state of the class is unstable
     */
    private void ensureClassInvariant() throws InvalidObjectException, IOException {
        this.getDatabasePath();
        assertNotNull(this.fileSystem);
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
     * Gets the databasepath from the databaseMap
     * @throws IOException throws IOException if the database isn't loaded yet.
     */
    private String getDatabasePath() throws IOException {
        Object object = databaseMap.get(DATABASE_PATH);
        if(object instanceof String) {
            String dbp = (String) object;
            return dbp;
        } else {
            throw new IOException(DATABASE_NOT_CREATED);
        }

    }

    /**
     * Gets the publicKey from the databaseMap
     * @throws IOException throws IOException if the database isn't loaded yet.
     */
    private IKey getPublicKey() throws IOException {
        Object object = databaseMap.get(PUBLIC_KEY);
        if(object instanceof IKey) {
            IKey pk = (IKey) object;
            return pk;
        } else {
            throw new IOException(DATABASE_NOT_CREATED);
        }

    }

}
