package com.nervousfish.nervousfish.modules.database;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.nervousfish.nervousfish.data_objects.Contact;
import com.nervousfish.nervousfish.data_objects.Database;
import com.nervousfish.nervousfish.data_objects.IKey;
import com.nervousfish.nervousfish.data_objects.KeyPair;
import com.nervousfish.nervousfish.data_objects.Profile;
import com.nervousfish.nervousfish.modules.constants.IConstants;
import com.nervousfish.nervousfish.modules.cryptography.EncryptedSaver;
import com.nervousfish.nervousfish.modules.cryptography.IKeyGenerator;
import com.nervousfish.nervousfish.modules.filesystem.IFileSystem;
import com.nervousfish.nervousfish.service_locator.IServiceLocator;
import com.nervousfish.nervousfish.service_locator.ModuleWrapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
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
    private static final String DATABASE_NOT_LOADED = "Database is not loaded";
    private static final String DATABASE_PATH = "database path";
    private static final String PASSWORD_PATH = "password path";
    private static final String DATABASE = "database";

    private final String androidFilesDir;


    private static final Logger LOGGER = LoggerFactory.getLogger("GsonDatabaseAdapter");
    private static final Type TYPE_DATABASE = new TypeToken<Database>() {
    }.getType();

    private final Map<String, Object> databaseMap;

    private final IFileSystem fileSystem;
    private final IKeyGenerator keyGenerator;

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
        androidFilesDir = serviceLocator.getAndroidFilesDir();
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
        /*
        final GsonBuilder gsonBuilder = new GsonBuilder()
                .registerTypeHierarchyAdapter(IKey.class, new GsonKeyAdapter());
        final Gson gsonParser = gsonBuilder.create();

        final Reader reader = this.fileSystem.getReader(this.contactsPath);

        final List<Contact> contacts = gsonParser.fromJson(reader, TYPE_CONTACT_LIST);
        reader.close();

        return contacts;
        */
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
        /*
        final GsonBuilder gsonBuilder = new GsonBuilder()
                .registerTypeHierarchyAdapter(IKey.class, new GsonKeyAdapter());
        final Gson gsonParser = gsonBuilder.create();

        final Reader reader = this.fileSystem.getReader(this.profilesPath);
        final List<Profile> profile = gsonParser.fromJson(reader, TYPE_PROFILE_LIST);
        reader.close();

        return profile;
        */
        return getDatabase().getProfile();
    }



    /**
     * {@inheritDoc}
     */
    @Override
    public void updateProfile(final Profile newProfile) throws IOException {
        /*
        // Get the list of contacts
        final List<Profile> profiles = this.getProfiles();
        final int lengthBefore = profiles.size();
        profiles.remove(oldProfile);

        // Throw if the contact to update is not found
        if (profiles.size() == lengthBefore) {
            throw new IllegalArgumentException(CONTACT_NOT_FOUND);
        }

        final GsonBuilder gsonBuilder = new GsonBuilder().registerTypeHierarchyAdapter(IKey.class, new GsonKeyAdapter());
        final Gson gsonParser = gsonBuilder.create();

        // Add the new contact and update the database
        profiles.add(newProfile);
        final Writer writer = this.fileSystem.getWriter(this.profilesPath);
        gsonParser.toJson(profiles, writer);
        writer.close();
        */
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

        String databaseGson = gsonParser.toJson(getDatabase());

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Database loadDatabase(String password) {
        try {
            initializePassword(password);
            initializeDatabase(password);
        } catch (final IOException e) {
            LOGGER.error("Failed to initialize database", e);
        }

        return null;
    }

    /**
     * Update the contact contents of the database.
     *
     * @param contacts The list of contacts to write.
     */
    private void updateContacts(final List<Contact> contacts)  throws IOException {
        /*
        final GsonBuilder gsonBuilder = new GsonBuilder()
                .registerTypeHierarchyAdapter(IKey.class, new GsonKeyAdapter());
        final Gson gsonParser = gsonBuilder.create();

        final Writer writer = this.fileSystem.getWriter(this.contactsPath);

        gsonParser.toJson(contacts, writer);
        writer.close();
        */
        getDatabase().setContacts(contacts);
        updateDatabase();
    }


    /**
     * Initialize the main database. This does nothing
     * if the main database already exists.
     * @param password The password to initialize the database with.
     */
    private void initializeDatabase(String password) throws IOException {
        try {
            getDatabasePath();
            return;
        } catch (IOException e) {
            LOGGER.warn("Database isn't initialized yet");
        }
        final String databasePath = this.androidFilesDir + EncryptedSaver.hashWithoutSalt(DATABASE_PATH+password);
        databaseMap.put(DATABASE_PATH, databasePath);
        final Writer writer = this.fileSystem.getWriter(databasePath);
        writer.write("[]");
        writer.close();
        LOGGER.info("Created the database: %s", this.getDatabasePath());


    }

    /**
     * Initialize the password file with the keys to encrypt the database. This does nothing
     * if the password file already exists.
     * @param password The password to initialize the database with.
     */
    private void initializePassword(String password) throws IOException {
        try {
            getPasswordPath();
            return;
        } catch (IOException e) {
            LOGGER.warn("Password isn't initialized yet");
        }
        final String passwordPath = this.androidFilesDir + EncryptedSaver.hashWithoutSalt(PASSWORD_PATH+password);
        databaseMap.put(PASSWORD_PATH, passwordPath);

        //  Encrypt the lockpair
        KeyPair lockpair = keyGenerator.generateRSAKeyPair(DATABASE);



        final Writer writer = this.fileSystem.getWriter(passwordPath);
        writer.write("[]");
        writer.close();
        LOGGER.info("Created the password file: %s", this.getPasswordPath());


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
        this.getPasswordPath();
        assertNotNull(this.fileSystem);
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
            throw new IOException(DATABASE_NOT_LOADED);
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
            throw new IOException(DATABASE_NOT_LOADED);
        }

    }

    /**
     * Gets the passwordPath from the databaseMap
     * @throws IOException throws IOException if the database isn't loaded yet.
     */
    private String getPasswordPath() throws IOException {
        Object object = databaseMap.get(PASSWORD_PATH);
        if(object instanceof String) {
            String pwp = (String) object;
            return pwp;
        } else {
            throw new IOException(DATABASE_NOT_LOADED);
        }

    }


    public void saveDatabaseEncrypted() throws IOException {
        final GsonBuilder gsonBuilder = new GsonBuilder()
                .registerTypeHierarchyAdapter(IKey.class, new GsonKeyAdapter());
        final Gson gsonParser = gsonBuilder.create();

        final Writer writer = this.fileSystem.getWriter(this.getDatabasePath());
        gsonParser.toJson(this.getDatabase(), writer);
        writer.close();
    }

}
