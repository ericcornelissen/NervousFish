package com.nervousfish.nervousfish.data_objects;

import com.nervousfish.nervousfish.ConstantKeywords;

import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * A Java object representation (POJO) of a list of contacts.
 */
public final class MultiContact implements Serializable {

    private static final long serialVersionUID = -4715364587956219157L;
    private final List<Contact> contacts = new ArrayList<>();

    /**
     * Constructor for the {@link MultiContact} POJO for a single {@link IKey}.
     *
     * @param contacts A list of {@link Contact}
     */
    public MultiContact(final Collection<Contact> contacts) {
        this.contacts.addAll(contacts);
    }

    public List<Contact> getContacts() {
        return this.contacts;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object o) {
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }

        final MultiContact that = (MultiContact) o;
        return this.contacts.equals(that.contacts);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return this.contacts.hashCode();
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

        /**
         * Constructs a new SerializationProxy
         *
         * @param contact The current instance of the proxy
         */
        SerializationProxy(final MultiContact contact) {
            this.contacts = contact.contacts.toArray(new Contact[contact.contacts.size()]);
        }

        /**
         * Not to be called by the user - resolves a new object of this proxy
         *
         * @return The object resolved by this proxy
         */
        private Object readResolve() {
            return new MultiContact(Arrays.asList(this.contacts));
        }
    }
}
