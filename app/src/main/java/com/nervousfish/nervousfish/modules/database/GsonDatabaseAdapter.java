package com.nervousfish.nervousfish.modules.database;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.nervousfish.nervousfish.data_objects.Contact;
import com.nervousfish.nervousfish.data_objects.IKey;
import com.nervousfish.nervousfish.modules.constants.IConstants;
import com.nervousfish.nervousfish.service_locator.IServiceLocator;
import com.nervousfish.nervousfish.service_locator.IServiceLocatorCreator;
import com.nervousfish.nervousfish.service_locator.ModuleWrapper;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * An adapter to the GSON database library
 */
public final class GsonDatabaseAdapter implements IDatabase {

    private final static Type TYPE_CONTACT_LIST = new TypeToken<ArrayList<Contact>>(){}.getType();
    private final IConstants constants;
    private final GsonBuilder gsonBuilder = new GsonBuilder()
            .registerTypeHierarchyAdapter(IKey.class, new KeyAdapter());
    private final Gson gsonParser = this.gsonBuilder.create();

    /**
     * Prevents construction from outside the class.
     *
     * @param serviceLocatorCreator The object responsible for creating the service locator
     */
    private GsonDatabaseAdapter(final IServiceLocatorCreator serviceLocatorCreator) {
        final IServiceLocator serviceLocator = serviceLocatorCreator.getServiceLocator();
        this.constants = serviceLocator.getConstants();

        this.initializeDatabase();
    }

    /**
     * Creates a new instance of itself and wraps it in a {@link ModuleWrapper} so that only an
     * {@link IServiceLocatorCreator} can access the new module to create the new
     * {@link IServiceLocator}.
     *
     * @param serviceLocatorCreator The service locator bridge that creates the new service locator
     * @return A wrapper around a newly created instance of this class
     */
    public static ModuleWrapper<GsonDatabaseAdapter> newInstance(final IServiceLocatorCreator serviceLocatorCreator) {
        return new ModuleWrapper<>(new GsonDatabaseAdapter(serviceLocatorCreator));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void addContact(final Contact contact) {
        final String contactsPath = this.constants.getDatabaseContactsPath();

        // Get the list of contacts and add the new contact
        final List<Contact> contacts = this.getAllContacts();
        contacts.add(contact);

        // Update the database
        try {
            final FileWriter fileWriter = new FileWriter(contactsPath);
            this.gsonParser.toJson(contacts, fileWriter);
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void deleteContact(final Contact contact) throws IllegalArgumentException {
        final String contactsPath = this.constants.getDatabaseContactsPath();

        // Get the list of contacts
        final List<Contact> contacts = this.getAllContacts();
        final int lengthBefore = contacts.size();
        contacts.remove(contact);

        // Throw if the contact to remove is not found
        if (contacts.size() == lengthBefore) {
            throw new IllegalArgumentException("Contact not found in database");
        }

        // Update the database
        try {
            final FileWriter fileWriter = new FileWriter(contactsPath);
            this.gsonParser.toJson(contacts, fileWriter);
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final List<Contact> getAllContacts() {
        final String contactsPath = this.constants.getDatabaseContactsPath();

        try {
            final Reader reader = new FileReader(contactsPath);
            List<Contact> contacts = this.gsonParser.fromJson(reader, TYPE_CONTACT_LIST);
            reader.close();
            return contacts;
        } catch (IOException e) {
            // Contacts file not found, create a new file
            this.initializeContacts();
        }

        return new ArrayList<Contact>();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void updateContact(final Contact oldContact, final Contact newContact) throws IllegalArgumentException {
        final String contactsPath = this.constants.getDatabaseContactsPath();

        // Get the list of contacts
        final List<Contact> contacts = this.getAllContacts();
        final int lengthBefore = contacts.size();
        contacts.remove(oldContact);

        // Throw if the contact to update is not found
        if (contacts.size() == lengthBefore) {
            throw new IllegalArgumentException("Contact not found in database");
        }

        // Add the new contact and update the database
        contacts.add(newContact);
        try {
            final FileWriter fileWriter = new FileWriter(contactsPath);
            this.gsonParser.toJson(contacts, fileWriter);
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Initialize the database.
     */
    private void initializeDatabase() {
        this.initializeContacts();
        this.initializeUserdata();
    }

    /**
     * Initialize the contacts in the database. This does nothing
     * if the contacts section of the database already exists.
     */
    private void initializeContacts() {
        final String contactsPath = constants.getDatabaseContactsPath();
        final File file = new File(contactsPath);
        if (!file.exists()) {
            try {
                final BufferedWriter bufferedWriter =
                        new BufferedWriter(new FileWriter(contactsPath));
                bufferedWriter.write("[]");
                bufferedWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Initialize the userdata in the database. This does nothing
     * if the userdata section of the database already exists.
     */
    private void initializeUserdata() {
        final String userdataPath = constants.getDatabaseUserdataPath();
        final File file = new File(userdataPath);
        if (!file.exists()) {
            try {
                final BufferedWriter bufferedWriter =
                        new BufferedWriter(new FileWriter(userdataPath));
                bufferedWriter.write("[]");
                bufferedWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
