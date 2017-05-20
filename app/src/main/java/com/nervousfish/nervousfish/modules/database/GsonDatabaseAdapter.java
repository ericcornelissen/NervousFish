package com.nervousfish.nervousfish.modules.database;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.nervousfish.nervousfish.data_objects.Contact;
import com.nervousfish.nervousfish.data_objects.IKey;
import com.nervousfish.nervousfish.data_objects.Profile;
import com.nervousfish.nervousfish.modules.constants.IConstants;
import com.nervousfish.nervousfish.service_locator.IServiceLocator;
import com.nervousfish.nervousfish.service_locator.ModuleWrapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Serializable;
import java.io.Writer;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * An adapter to the GSON database library. We suppress the TooManyMethods warning of PMD because a
 * DatabaseHandler has a lot of methods by nature and refractoring it to multiple classes or single
 * methods with more logic would make the class only less understandable.
 */
@SuppressWarnings("PMD.TooManyMethods")
public final class GsonDatabaseAdapter implements IDatabase {
    private static final long serialVersionUID = -4101015873770268925L;
    private final static String CONTACT_NOT_FOUND = "Contact not found in database";
    private final static String CONTACT_DUPLICATE = "Contact is already in the database";
    private static final Logger LOGGER = LoggerFactory.getLogger("GsonDatabaseAdapter");
    private final static Type TYPE_CONTACT_LIST =
            new TypeToken<ArrayList<Contact>>() {
            }.getType();
    private final static Type TYPE_PROFILE_LIST =
            new TypeToken<ArrayList<Profile>>() {
            }.getType();

    private final String contactsPath;
    private final String profilesPath;
    private final IServiceLocator serviceLocator;

    /**
     * Prevents construction from outside the class.
     *
     * @param serviceLocator Can be used to get access to other modules
     */
    private GsonDatabaseAdapter(final IServiceLocator serviceLocator) {
        this.serviceLocator = serviceLocator;
        final IConstants constants = serviceLocator.getConstants();

        this.contactsPath = constants.getDatabaseContactsPath();
        this.profilesPath = constants.getDatabaseUserdataPath();

        try {
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
        return new ModuleWrapper<>(new GsonDatabaseAdapter(serviceLocator));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addContact(final Contact contact) throws IOException {
        // Get the list of contacts and add the new contact
        if(contactExtists(contact.getName())) {
            throw new IllegalArgumentException(CONTACT_DUPLICATE);
        }

        final List<Contact> contacts = this.getAllContacts();
        contacts.add(contact);

        // Update the database
        final GsonBuilder gsonBuilder = new GsonBuilder()
                .registerTypeHierarchyAdapter(IKey.class, new GsonKeyAdapter());
        final Gson gsonParser = gsonBuilder.create();

        final Writer writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(this.contactsPath), "UTF-8"));

        gsonParser.toJson(contacts, writer);
        writer.close();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteContact(final String contactName) throws IllegalArgumentException, IOException {
        // Get the list of contacts
        final List<Contact> contacts = this.getAllContacts();
        final int lengthBefore = contacts.size();
        for (final Contact contact : contacts) {
            if(contactName.equals(contact.getName())) {
                contacts.remove(contact);
                break;
            }
        }

        // Throw if the contact to remove is not found
        if (contacts.size() == lengthBefore) {
            throw new IllegalArgumentException(CONTACT_NOT_FOUND);
        }

        // Update the database
        final GsonBuilder gsonBuilder = new GsonBuilder()
                .registerTypeHierarchyAdapter(IKey.class, new GsonKeyAdapter());
        final Gson gsonParser = gsonBuilder.create();

        final Writer writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(this.contactsPath), UTF_8));

        gsonParser.toJson(contacts, writer);
        writer.close();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateContact(final Contact oldContact, final Contact newContact) throws IllegalArgumentException, IOException {
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
        final BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(this.contactsPath), UTF_8));
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

        final Reader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(this.contactsPath), UTF_8));

        final List<Contact> contacts = gsonParser.fromJson(reader, TYPE_CONTACT_LIST);
        reader.close();

        return contacts;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Contact getContactWithName(final String contactName) throws IOException {
        final List<Contact> contacts = getAllContacts();
        for (final Contact contact: contacts) {
            if(contact.getName().equals(contactName)) {
                return contact;
            }
        }

        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean contactExtists(final String name) throws IOException {
        return getContactWithName(name) != null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Profile> getProfiles() throws IOException {
        final GsonBuilder gsonBuilder = new GsonBuilder()
                .registerTypeHierarchyAdapter(IKey.class, new GsonKeyAdapter());
        final Gson gsonParser = gsonBuilder.create();

        final Reader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(this.profilesPath), UTF_8));
        final List<Profile> profile = gsonParser.fromJson(reader, TYPE_PROFILE_LIST);
        reader.close();

        return profile;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addProfile(final Profile profile) throws IOException {
        // Get the list of profiles and add the new profile
        final List<Profile> profiles = this.getProfiles();
        profiles.add(profile);

        final GsonBuilder gsonBuilder = new GsonBuilder()
                .registerTypeHierarchyAdapter(IKey.class, new GsonKeyAdapter());
        final Gson gsonParser = gsonBuilder.create();

        // Update the database
        final Writer writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(this.profilesPath), "UTF-8"));
        gsonParser.toJson(profiles, writer);
        writer.close();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteProfile(final Profile profile) throws IllegalArgumentException, IOException {
        // Get the list of contacts
        final List<Profile> profiles = this.getProfiles();
        final int lengthBefore = profiles.size();
        profiles.remove(profile);

        // Throw if the contact to remove is not found
        if (profiles.size() == lengthBefore) {
            throw new IllegalArgumentException(CONTACT_NOT_FOUND);
        }

        final GsonBuilder gsonBuilder = new GsonBuilder()
                .registerTypeHierarchyAdapter(IKey.class, new GsonKeyAdapter());
        final Gson gsonParser = gsonBuilder.create();

        // Update the database
        final Writer writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(this.profilesPath), UTF_8));
        gsonParser.toJson(profiles, writer);
        writer.close();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateProfile(final Profile oldProfile, final Profile newProfile) throws IllegalArgumentException, IOException {
        // Get the list of contacts
        final List<Profile> profiles = this.getProfiles();
        final int lengthBefore = profiles.size();
        profiles.remove(oldProfile);

        // Throw if the contact to update is not found
        if (profiles.size() == lengthBefore) {
            throw new IllegalArgumentException(CONTACT_NOT_FOUND);
        }

        final GsonBuilder gsonBuilder = new GsonBuilder()
                .registerTypeHierarchyAdapter(IKey.class, new GsonKeyAdapter());
        final Gson gsonParser = gsonBuilder.create();

        // Add the new contact and update the database
        profiles.add(newProfile);
        final Writer writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(this.profilesPath), UTF_8));
        gsonParser.toJson(profiles, writer);
        writer.close();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getUserPassword() throws IOException {
        return null;
    }

    /**
     * Initialize the database.
     */
    private void initializeDatabase() throws IOException {
        this.initializeContacts();
        this.initializeUserdata();
    }

    /**
     * Initialize the contacts in the database. This does nothing
     * if the contacts section of the database already exists.
     */
    private void initializeContacts() throws IOException {
        final File file = new File(this.contactsPath);
        if (!file.exists()) {
            LOGGER.warn("Contacts part of the database didn't exist");
            final Writer writer = new BufferedWriter(
                    new OutputStreamWriter(new FileOutputStream(this.contactsPath), UTF_8));
            writer.write("[]");
            writer.close();
            LOGGER.info("Created the contacts part of the database");
        }
    }

    /**
     * Initialize the userdata in the database. This does nothing
     * if the userdata section of the database already exists.
     */
    private void initializeUserdata() throws IOException {
        final File file = new File(profilesPath);
        if (!file.exists()) {
            LOGGER.warn("User data part of the database didn't exist");
            final Writer writer = new BufferedWriter(
                    new OutputStreamWriter(new FileOutputStream(profilesPath), UTF_8));
            writer.write("[]");
            writer.close();
            LOGGER.info("Created the user data part of the database");
        }
    }


    /**
     * Serialize the created proxy instead of this instance.
     */
    private Object writeReplace() {
        return new SerializationProxy(this);
    }

    /**
     * Ensure that no instance of this class is created because it was present in the stream. A correct
     * stream should only contain instances of the proxy.
     */
    private void readObject(final ObjectInputStream stream) throws InvalidObjectException {
        throw new InvalidObjectException("Proxy required.");
    }

    /**
     * Represents the logical state of this class and copies the data from that class without
     * any consistency checking or defensive copying.
     * Used for the Serialization Proxy Pattern.
     * We suppress here the AccessorClassGeneration warning because the only alternative to this pattern -
     * ordinary serialization - is far more dangerous
     */
    @SuppressWarnings("PMD.AccessorClassGeneration")
    private static final class SerializationProxy implements Serializable {
        private static final long serialVersionUID = -4101015873770268925L;
        private final IServiceLocator serviceLocator;

        SerializationProxy(final GsonDatabaseAdapter gsonDatabaseAdapter) {
            this.serviceLocator = gsonDatabaseAdapter.serviceLocator;
        }

        private Object readResolve() {
            return new GsonDatabaseAdapter(this.serviceLocator);
        }
    }
}
