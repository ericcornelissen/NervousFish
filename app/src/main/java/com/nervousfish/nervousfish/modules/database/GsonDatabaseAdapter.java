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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
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

    private final static Type TYPE_CONTACT_LIST = new TypeToken<ArrayList<Contact>>() {
    }.getType();
    private final static Type TYPE_PROFILE_LIST = new TypeToken<ArrayList<Profile>>() {
    }.getType();
    private final static String CONTACT_NOT_FOUND = "Contact not found in database";
    private final String contactsPath;
    private final String profilesPath;

    /**
     * Prevents construction from outside the class.
     *
     * @param serviceLocator Can be used to get access to other modules
     */
    @SuppressWarnings("PMD.UnusedFormalParameter")
    // This servicelocator will be used later on probably
    private GsonDatabaseAdapter(final IServiceLocator serviceLocator) {
        final IConstants constants = serviceLocator.getConstants();

        this.contactsPath = constants.getDatabaseContactsPath();
        this.profilesPath = constants.getDatabaseUserdataPath();

        try {
            this.initializeDatabase();
        } catch (final IOException e) {
            // TODO: Handle failure
            e.printStackTrace();
        }
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
    public void deleteContact(final Contact contact) throws IllegalArgumentException, IOException {
        // Get the list of contacts
        final List<Contact> contacts = this.getAllContacts();
        final int lengthBefore = contacts.size();
        contacts.remove(contact);

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
            final Writer writer = new BufferedWriter(
                    new OutputStreamWriter(new FileOutputStream(this.contactsPath), UTF_8));
            writer.write("[]");
            writer.close();
        }
    }

    /**
     * Initialize the userdata in the database. This does nothing
     * if the userdata section of the database already exists.
     */
    private void initializeUserdata() throws IOException {
        final File file = new File(profilesPath);
        if (!file.exists()) {
            final Writer writer = new BufferedWriter(
                    new OutputStreamWriter(new FileOutputStream(profilesPath), UTF_8));
            writer.write("[]");
            writer.close();
        }
    }
}
