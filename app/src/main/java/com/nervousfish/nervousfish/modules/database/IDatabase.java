package com.nervousfish.nervousfish.modules.database;

import com.nervousfish.nervousfish.data_objects.Contact;
import com.nervousfish.nervousfish.data_objects.Profile;
import com.nervousfish.nervousfish.modules.IModule;

import java.io.IOException;
import java.util.List;

import nl.tudelft.ewi.ds.bankver.IBAN;

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
    void addContact(Contact contact) throws IOException;

    /**
     * Delete a contact from the database.
     *
     * @param contactName The {@link String} with the name of the contact to remove from the database.
     */
    void deleteContact(String contactName) throws IOException;

    /**
     * Update an existing contact in the database.
     *
     * @param oldContact The old {@link Contact} to be updated.
     * @param newContact The new {@link Contact} details.
     * @throws IllegalArgumentException When {@code oldContact} is not in the database.
     */
    void updateContact(Contact oldContact, Contact newContact) throws IllegalArgumentException, IOException;

    /**
     * Get a list of all contacts in the database.
     *
     * @return A {@link List} of {@link Contact Contacts}.
     */
    List<Contact> getAllContacts() throws IOException;

    /**
     * Get one Contact from the database given the contact's name as input.
     *
     * @param contactName - The name of the Contact as a {@link String}.
     * @return A {@link Contact}.
     */
    Contact getContactWithName(String contactName) throws IOException;

    /**
     * Returns true if the contact exists and fals otherwise.
     *
     * @param name - the name of the contact to check
     * @return A {@link boolean} which is true if the contact exists in the database
     */
    boolean contactExists(String name) throws IOException;

    /**
     * Get the Profile with user information.
     *
     * @return A {@link Profile Profiles}.
     */
    Profile getProfile();

    /**
     * Update an existing profile in the database.
     *
     * @param newProfile The new {@link Profile} details.
     */
    void updateProfile(Profile newProfile) throws IOException;

    /**
     * Updates the database with the new information.
     */
    void updateDatabase() throws IOException;

    /**
     * Loads up the database in an object using the provided password.
     *
     * @param password The password of the database.
     */
    void loadDatabase(String password) throws IOException;

    /**
     * Creates a database for a new profile.
     *
     * @param profile   -   The user profile to create a database for.
     * @param password - The password for the database
     */
    void createDatabase(Profile profile, String password) throws IOException;

    /**
     * Checks if there's already a databasefile created.
     *
     * @return  Whether or not it isn't the first time using the app.
     */
    boolean checkFirstUse();

    /**
     * Deletes the database.
     *
     * @return Whether the database was successfully deleted.
     */
    boolean deleteDatabase();

    /**
     * Gets the Contact of the person corresponding to the given IBAN.
     *
     * @param iban The iban of the contact.
     * @return The contact with the given IBAN.
     */
    Contact getContactWithIban(IBAN iban);

    /**
     * Sets the boolean in the Contact with the given IBAN.
     *
     * @param iban The iban of the contact.
     */
    void setContactVerified(IBAN iban);
}
