package com.nervousfish.nervousfish.modules.database;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.nervousfish.nervousfish.data_objects.Contact;
import com.nervousfish.nervousfish.data_objects.IKey;
import com.nervousfish.nervousfish.data_objects.Profile;
import com.nervousfish.nervousfish.modules.constants.IConstants;
import com.nervousfish.nervousfish.modules.filesystem.IFileSystem;
import com.nervousfish.nervousfish.service_locator.IServiceLocator;
import com.nervousfish.nervousfish.service_locator.ModuleWrapper;

import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

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
    private static final Logger LOGGER = LoggerFactory.getLogger("GsonDatabaseAdapter");
    private static final Type TYPE_CONTACT_LIST = new TypeToken<ArrayList<Contact>>() {
    }.getType();
    private static final Type TYPE_PROFILE_LIST = new TypeToken<ArrayList<Profile>>() {
    }.getType();

    private final String contactsPath;
    private final String profilesPath;
    private final IFileSystem fileSystem;

    /**
     * Prevents construction from outside the class.
     *
     * @param serviceLocator Can be used to get access to other modules
     */
    private GsonDatabaseAdapter(final IServiceLocator serviceLocator) {
        assert serviceLocator != null;
        final IConstants constants = serviceLocator.getConstants();
        this.fileSystem = serviceLocator.getFileSystem();

        this.contactsPath = constants.getDatabaseContactsPath();
        this.profilesPath = constants.getDatabaseUserdataPath();

        try {
            this.initializeContacts();
            this.initializeDatabase();
        } catch (final IOException e) {
            LOGGER.error("Failed to initialize database", e);
        }
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
        Validate.notNull(serviceLocator);
        return new ModuleWrapper<>(new GsonDatabaseAdapter(serviceLocator));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addContact(final Contact contact) throws IOException {
        Validate.notNull(contact);
        LOGGER.info("Adding new contact with name: {}", contact.getName());
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
    public void deleteContact(final String contactName) throws IOException {
        Validate.notBlank(contactName);
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
    public void updateContact(final Contact oldContact, final Contact newContact) throws IOException {
        Validate.notNull(oldContact);
        Validate.notNull(newContact);
        // Get the list of contacts
        final List<Contact> contacts = this.getAllContacts();
        final int lengthBefore = contacts.size();
        contacts.remove(oldContact);

        // Throw if the contact to update is not found
        if (contacts.size() == lengthBefore) {
            throw new IllegalArgumentException(CONTACT_NOT_FOUND);
        }

        final GsonBuilder gsonBuilder = new GsonBuilder()
                .registerTypeHierarchyAdapter(IKey.class, new GsonKeyAdapter());
        final Gson gsonParser = gsonBuilder.create();
        // Add the new contact and update the database
        contacts.add(newContact);

        final Writer writer = this.fileSystem.getWriter(this.contactsPath);
        gsonParser.toJson(contacts, writer);
        writer.close();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Contact> getAllContacts() throws IOException {
        final GsonBuilder gsonBuilder = new GsonBuilder()
                .registerTypeHierarchyAdapter(IKey.class, new GsonKeyAdapter());
        final Gson gsonParser = gsonBuilder.create();

        final Reader reader = this.fileSystem.getReader(this.contactsPath);

        final List<Contact> contacts = gsonParser.fromJson(reader, TYPE_CONTACT_LIST);
        reader.close();

        return contacts;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Contact getContactWithName(final String contactName) throws IOException {
        Validate.notBlank(contactName);
        final List<Contact> contacts = this.getAllContacts();
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
        Validate.notBlank(name);
        return this.getContactWithName(name) != null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Profile> getProfiles() throws IOException {
        final GsonBuilder gsonBuilder = new GsonBuilder()
                .registerTypeHierarchyAdapter(IKey.class, new GsonKeyAdapter());
        final Gson gsonParser = gsonBuilder.create();

        final Reader reader = this.fileSystem.getReader(this.profilesPath);
        final List<Profile> profile = gsonParser.fromJson(reader, TYPE_PROFILE_LIST);
        reader.close();

        return profile;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addProfile(final Profile profile) throws IOException {
        Validate.notNull(profile);
        // Get the list of profiles and add the new profile
        final List<Profile> profiles = this.getProfiles();
        profiles.add(profile);

        final GsonBuilder gsonBuilder = new GsonBuilder()
                .registerTypeHierarchyAdapter(IKey.class, new GsonKeyAdapter());
        final Gson gsonParser = gsonBuilder.create();

        // Update the database
        final Writer writer = this.fileSystem.getWriter(this.profilesPath);
        gsonParser.toJson(profiles, writer);
        writer.close();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteProfile(final Profile profile) throws IOException {
        Validate.notNull(profile);
        // Get the list of contacts
        final List<Profile> profiles = this.getProfiles();
        final int lengthBefore = profiles.size();
        profiles.remove(profile);

        // Throw if the contact to remove is not found
        if (profiles.size() == lengthBefore) {
            throw new IllegalArgumentException(CONTACT_NOT_FOUND);
        }

        final GsonBuilder gsonBuilder = new GsonBuilder().registerTypeHierarchyAdapter(IKey.class, new GsonKeyAdapter());
        final Gson gsonParser = gsonBuilder.create();

        // Update the database
        final Writer writer = this.fileSystem.getWriter(this.profilesPath);
        gsonParser.toJson(profiles, writer);
        writer.close();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateProfile(final Profile oldProfile, final Profile newProfile) throws IOException {
        Validate.notNull(oldProfile);
        Validate.notNull(newProfile);
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
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getUserPassword() {
        return null;
    }

    /**
     * Update the contact contents of the database.
     *
     * @param contacts The list of contacts to write.
     */
    private void updateContacts(final List<Contact> contacts) throws IOException {
        Validate.noNullElements(contacts);
        final GsonBuilder gsonBuilder = new GsonBuilder()
                .registerTypeHierarchyAdapter(IKey.class, new GsonKeyAdapter());
        final Gson gsonParser = gsonBuilder.create();

        final Writer writer = this.fileSystem.getWriter(this.contactsPath);

        gsonParser.toJson(contacts, writer);
        writer.close();
    }

    /**
     * Initialize the contacts in the database. This does nothing
     * if the contacts section of the database already exists.
     */
    private void initializeContacts() throws IOException {
        final File file = new File(this.contactsPath);
        if (!file.exists()) {
            LOGGER.warn("Contacts part of the database didn't exist");
            try (Writer writer = this.fileSystem.getWriter(this.contactsPath)) {
                writer.write("[]");
                writer.close();
                LOGGER.info("Created the contacts part of the database");
            }
        }
    }

    /**
     * Initialize the specified section in the database. This does nothing
     * if the specified section of the database already exists.
     */
    private void initializeDatabase() throws IOException {
        final File file = new File(this.profilesPath);
        if (!file.exists()) {
            LOGGER.warn("Part of the database didn't exist: {}", this.profilesPath);
            try (Writer writer = this.fileSystem.getWriter(this.profilesPath)) {
                writer.write("[]");
                writer.close();
                LOGGER.info("Created the part of the database: {}", this.profilesPath);
            }
        }
    }

    /**
     * Deserialize the instance using readObject to ensure invariants and security.
     *
     * @param stream The serialized object to be deserialized
     */
    private void readObject(final ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.ensureClassInvariant();
    }

    /**
     * Ensure that the instance meets its class invariant
     */
    private void ensureClassInvariant() {
        Validate.notNull(this.contactsPath);
        Validate.notNull(this.profilesPath);
        Validate.notNull(this.fileSystem);
    }
}
