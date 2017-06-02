package com.nervousfish.nervousfish.data_objects;

import com.nervousfish.nervousfish.ConstantKeywords;

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

    /**
     * The constructor for the {@link Database} POJO
     * @param contacts
     * @param profile
     */
    public Database(final List<Contact> contacts, final Profile profile) {
        this.contacts = contacts;
        this.profile = profile;
    }

    /**
     * Getter for the contacts.
     * @return
     */
    public List<Contact> getContacts() {
        return this.contacts;
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

        /**
         * Constructs a new SerializationProxy
         * @param database The current instance of the proxy
         */
        SerializationProxy(final Database database) {
            this.contacts = database.contacts.toArray(new Contact[database.contacts.size()]);
            this.profile = database.profile;
        }

        /**
         * Not to be called by the user - resolves a new object of this proxy
         * @return The object resolved by this proxy
         */
        private Object readResolve() {
            return new Database(Arrays.asList(this.contacts), this.profile);
        }
    }
}
