package com.nervousfish.nervousfish.modules.database;

import com.nervousfish.nervousfish.data_objects.Account;
import com.nervousfish.nervousfish.data_objects.Contact;
import com.nervousfish.nervousfish.modules.IModule;

import java.io.IOException;
import java.util.List;

/**
 * Defines a module for interaction with a database that can be used by
 * a {@link com.nervousfish.nervousfish.service_locator.IServiceLocator}.
 */
public interface IDatabase extends IModule {

    /**
     * Add a new contact in the database.
     *
     * @param contact The {@link Contact} to add to the database.
     */
    void addContact(final Contact contact) throws IOException;

    /**
     * Delete a contact from the database.
     *
     * @param contact The {@link Contact} to remove from the database.
     * @throws IllegalArgumentException When {@code contact} is not in the database.
     */
    void deleteContact(final Contact contact) throws IllegalArgumentException, IOException;

    /**
     * Get a list of all contacts in the database.
     *
     * @return A {@link List} of {@link Contact Contacts}.
     */
    List<Contact> getAllContacts() throws IOException;

    /**
     * Get the Account object with user information.
     *
     * @return A {@link Account account}.
     */
    Account getAccount();

    /**
     * Update an existing contact in the database.
     *
     * @param oldContact The old {@link Contact} to be updated.
     * @param newContact The new {@link Contact} details.
     * @throws IllegalArgumentException When {@code oldContact} is not in the database.
     */
    void updateContact(final Contact oldContact, final Contact newContact) throws IllegalArgumentException, IOException;
}
