package com.nervousfish.nervousfish.data_objects;

import com.nervousfish.nervousfish.ConstantKeywords;
import com.nervousfish.nervousfish.modules.cryptography.EncryptedSaver;

import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

/**
 * A database object to write and encrypt to file
 */
public final class Database implements Serializable {
    private final List<Contact> contacts;
    private final Profile profile;
    private final byte[] salt;
    private final String encryptedPassword;
    private final String databasePath;

    /**
     * The constructor for the {@link Database} POJO
     * @param contacts
     * @param profile
     */
    public Database(final List<Contact> contacts, final Profile profile, final String password) {
        this.contacts = contacts;
        this.profile = profile;
        this.salt = EncryptedSaver.generateSalt();
        this.encryptedPassword = EncryptedSaver.hashUsingSalt(salt, password);
    }

    /**
     * Checks a given string if it's the correct password
     * @param toCheck - The string to check.
     * @return  Whether or not the password is correct.
     */
    public boolean checkPassword(String toCheck) {
        final String toCheckEncrypted = EncryptedSaver.hashUsingSalt(salt, toCheck);
        return encryptedPassword.equals(toCheckEncrypted);
    }

    public String getDatabasePath() {

    }

    /**
     * Getter for the EncryptedPassword
     * @return  The EncryptedPassword string.
     */
    public String getEncryptedPassword() {
        return encryptedPassword;
    }
    /**
     * Getter for the contacts.
     * @return The contacts list.
     */
    public List<Contact> getContacts() {
        return this.contacts;
    }

    /**
     * Sets the list of contacts to the given list of contacts.
     * @param contacts  The new list of contacts.
     */
    public void setContacts(List<Contact> contacts) {
        this.contacts.clear();
        this.contacts.addAll(contacts);
    }
    /**
     * Getter for the user profile
     * @return  The User profile.
     */
    public Profile getProfile() {
        return this.profile;
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
        throw new InvalidObjectException(ConstantKeywords.PROXY_REQUIRED);
    }

    /**
     * Represents the logical state of this class and copies the data from that class without
     * any consistency checking or defensive copying.
     */
    private static final class SerializationProxy implements Serializable {
        private static final long serialVersionUID = -4715364587956219157L;
        private final Contact[] contacts;
        private final Profile profile;
        private final String encryptedPassword;

        /**
         * Constructs a new SerializationProxy
         * @param database The current instance of the proxy
         */
        SerializationProxy(final Database database) {
            this.contacts = database.contacts.toArray(new Contact[database.contacts.size()]);
            this.profile = database.profile;
            this.encryptedPassword = database.encryptedPassword;
        }

        /**
         * Not to be called by the user - resolves a new object of this proxy
         * @return The object resolved by this proxy
         */
        private Object readResolve() {
            return new Database(Arrays.asList(this.contacts), this.profile, this.encryptedPassword);
        }
    }
}
