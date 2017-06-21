package com.nervousfish.nervousfish.data_objects;

import java.io.Serializable;
import java.util.List;

/**
 * A database object to write and encrypt to file
 */
public final class Database implements Serializable {
    private static final long serialVersionUID = 6127044556316179233L;
    private final List<Contact> contacts;
    private final Profile profile;


    /**
     * The constructor for the {@link Database} POJO
     *
     * @param contacts The contacts the user has.
     * @param profile  The profile of the user.
     */
    public Database(final List<Contact> contacts, final Profile profile) {
        this.contacts = contacts;
        this.profile = profile;
    }


    /**
     * Getter for the contacts.
     *
     * @return The contacts list.
     */
    public List<Contact> getContacts() {
        return this.contacts;
    }

    /**
     * Sets the list of contacts to the given list of contacts.
     *
     * @param contacts The new list of contacts.
     */
    public void setContacts(final List<Contact> contacts) {
        this.contacts.clear();
        this.contacts.addAll(contacts);
    }

    /**
     * Getter for the user profile
     *
     * @return The User profile.
     */
    public Profile getProfile() {
        return this.profile;
    }
}
