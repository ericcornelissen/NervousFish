package com.nervousfish.nervousfish.modules.database;

import com.nervousfish.nervousfish.data_objects.Contact;
import com.nervousfish.nervousfish.modules.IModule;

import java.util.List;

/**
 * Defines a module defining the actions that can be executed on the database that can be used by a service locator.
 */
public interface IDatabase extends IModule {

    /**
     * Add a new contact in the database.
     *
     * @param name The new name for the contact.
     * @param key The new key for the contact.
     * @return The new {@link Contact} object.
     */
    Contact addContact(final String name, final String key);

    /**
     * Delete a contact from the database.
     *
     * @param id The id of the contact.
     */
    void deleteContact(final int id);

    /**
     * Get a specific contact from the database.
     *
     * @param id The id of the contact.
     * @return The requested {@link Contact}.
     */
    Contact getContact(final int id);

    /**
     * Get a list of all contacts in the database.
     *
     * @return A {@link List} of {@link Contact Contacts}.
     */
    List<Contact> getAllContacts();

    /**
     * Update an existing contact in the database.
     *
     * @param id The id of the contact.
     * @param name The new name for the contact.
     * @param key The new key for the contact.
     * @return A new {@link Contact} object with the updated details.
     */
    Contact updateContact(final int id, final String name, final String key);

}
