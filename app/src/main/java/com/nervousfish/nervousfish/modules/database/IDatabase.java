package com.nervousfish.nervousfish.modules.database;

import com.nervousfish.nervousfish.data_objects.Contact;
import com.nervousfish.nervousfish.data_objects.Profile;
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
     * @param contactName The {@link String} with the name of the contact to remove from the database.
     * @throws IllegalArgumentException When {@code contact} is not in the database.
     */
    void deleteContact(final String contactName) throws IllegalArgumentException, IOException;

    /**
     * Update an existing contact in the database.
     *
     * @param oldContact The old {@link Contact} to be updated.
     * @param newContact The new {@link Contact} details.
     * @throws IllegalArgumentException When {@code oldContact} is not in the database.
     */
    void updateContact(final Contact oldContact, final Contact newContact) throws IllegalArgumentException, IOException;

    /**
     * Get a list of all contacts in the database.
     *
     * @return A {@link List} of {@link Contact Contacts}.
     */
    List<Contact> getAllContacts() throws IOException;

    /**
     * Get the Profile list with user information.
     *
     * @return A {@link List} of {@link Profile Profiles}.
     */
    List<Profile> getProfiles() throws IOException;

    /**
     * Add a new profile in the database.
     *
     * @param profile The {@link Profile} to add to the database.
     */
    void addProfile(final Profile profile) throws IOException;

    /**
     * Delete an profile from the database.
     *
     * @param profile The {@link Profile} to remove from the database.
     * @throws IllegalArgumentException When {@code profile} is not in the database.
     */
    void deleteProfile(final Profile profile) throws IllegalArgumentException, IOException;

    /**
     * Update an existing profile in the database.
     *
     * @param oldProfile The old {@link Profile} to be updated.
     * @param newProfile The new {@link Profile} details.
     * @throws IllegalArgumentException When {@code oldProfile} is not in the database.
     */
    void updateProfile(final Profile oldProfile, final Profile newProfile) throws IllegalArgumentException, IOException;
}
