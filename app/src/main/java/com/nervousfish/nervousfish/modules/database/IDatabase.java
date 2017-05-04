package com.nervousfish.nervousfish.modules.database;

import com.nervousfish.nervousfish.data_objects.IContact;
import com.nervousfish.nervousfish.modules.IModule;

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
    void addContact(IContact contact);

    /**
     * Delete a contact from the database.
     *
     * @param contact The {@link Contact} to remove from the database.
     */
    void deleteContact(IContact contact);

    /**
     * Get a list of all contacts in the database.
     *
     * @return A {@link List} of {@link Contact Contacts}.
     */
    List<IContact> getAllContacts();

    /**
     * Update an existing contact in the database.
     *
     * @param oldContact The old {@link Contact} to be updated.
     * @param newContact The new {@link Contact} details.
     * @return A new {@link Contact} object with the updated details.
     */
    void updateContact(IContact oldContact, IContact newContact);

}
