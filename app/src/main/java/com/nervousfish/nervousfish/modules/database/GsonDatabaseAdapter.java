package com.nervousfish.nervousfish.modules.database;

import com.google.gson.Gson;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.nervousfish.nervousfish.data_objects.Account;
import com.nervousfish.nervousfish.data_objects.IKey;
import com.nervousfish.nervousfish.data_objects.RSAKey;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.nervousfish.nervousfish.data_objects.Contact;

import com.nervousfish.nervousfish.modules.constants.IConstants;
import com.nervousfish.nervousfish.service_locator.IServiceLocator;
import com.nervousfish.nervousfish.service_locator.IServiceLocatorCreator;
import com.nervousfish.nervousfish.service_locator.ModuleWrapper;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * An adapter to the GSON database library
 */
public final class GsonDatabaseAdapter implements IDatabase {

    private final static Type TYPE_CONTACT_LIST = new TypeToken<ArrayList<Contact>>(){}.getType();
    private final static Type TYPE_ACCOUNT_INFORMATION = new TypeToken<ArrayList<Account>>(){}.getType();
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

        try {
            this.initializeDatabase();
        } catch (final IOException e) {
            // TODO: Handle failure
            e.printStackTrace();
        }
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
    public void addContact(final Contact contact) throws IOException {
        final String contactsPath = this.constants.getDatabaseContactsPath();

        // Get the list of contacts and add the new contact
        final List<Contact> contacts = this.getAllContacts();
        contacts.add(contact);

        // Update the database
        final BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(contactsPath), "UTF-8"));
        this.gsonParser.toJson(contacts, writer);
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
            throw new IllegalArgumentException("Contact not found in database");
        }

        // Update the database
        final String contactsPath = this.constants.getDatabaseContactsPath();
        final BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(contactsPath), UTF_8));
        this.gsonParser.toJson(contacts, writer);
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
            throw new IllegalArgumentException("Contact not found in database");
        }

        // Add the new contact and update the database
        contacts.add(newContact);
        final String contactsPath = this.constants.getDatabaseContactsPath();
        final BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(contactsPath), UTF_8));
        this.gsonParser.toJson(contacts, writer);
        writer.close();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Contact> getAllContacts() throws IOException {
        final String contactsPath = this.constants.getDatabaseContactsPath();

        final BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(contactsPath), UTF_8));
        final List<Contact> contacts = this.gsonParser.fromJson(reader, TYPE_CONTACT_LIST);
        reader.close();
        return contacts;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Account> getAccounts() throws IOException {
        final String accountPath = this.constants.getDatabaseUserdataPath();

        final BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(accountPath), UTF_8));
        final List<Account> account = this.gsonParser.fromJson(reader, TYPE_ACCOUNT_INFORMATION);
        reader.close();

        return account;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addAccount(final Account account) throws IOException {
        final String accountPath = this.constants.getDatabaseContactsPath();

        // Get the list of accounts and add the new account
        final List<Account> accounts = this.getAccounts();
        accounts.add(account);

        // Update the database
        final BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(accountPath), "UTF-8"));
        this.gsonParser.toJson(accounts, writer);
        writer.close();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteAccount(final Account account) throws IllegalArgumentException, IOException {
        // Get the list of contacts
        final List<Account> accounts = this.getAccounts();
        final int lengthBefore = accounts.size();
        accounts.remove(account);

        // Throw if the contact to remove is not found
        if (accounts.size() == lengthBefore) {
            throw new IllegalArgumentException("Contact not found in database");
        }

        // Update the database
        final String accountsPath = this.constants.getDatabaseUserdataPath();
        final BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(accountsPath), UTF_8));
        this.gsonParser.toJson(accounts, writer);
        writer.close();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateAccount(final Account oldAccount, final Account newAccount) throws IllegalArgumentException, IOException {
        // Get the list of contacts
        final List<Account> accounts = this.getAccounts();
        final int lengthBefore = accounts.size();
        accounts.remove(oldAccount);

        // Throw if the contact to update is not found
        if (accounts.size() == lengthBefore) {
            throw new IllegalArgumentException("Contact not found in database");
        }

        // Add the new contact and update the database
        accounts.add(newAccount);
        final String contactsPath = this.constants.getDatabaseContactsPath();
        final BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(contactsPath), UTF_8));
        this.gsonParser.toJson(accounts, writer);
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
        final String contactsPath = constants.getDatabaseContactsPath();
        final File file = new File(contactsPath);
        if (!file.exists()) {
            final BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(new FileOutputStream(contactsPath), UTF_8));
            writer.write("[]");
            writer.close();
        }
    }

    /**
     * Initialize the userdata in the database. This does nothing
     * if the userdata section of the database already exists.
     */
    private void initializeUserdata() throws IOException {
        final String userdataPath = constants.getDatabaseUserdataPath();
        final File file = new File(userdataPath);
        if (!file.exists()) {
            final BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(new FileOutputStream(userdataPath), UTF_8));
            writer.write("[]");
            writer.close();
        }
    }
}
