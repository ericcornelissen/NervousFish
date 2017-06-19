package com.nervousfish.nervousfish.data_objects;

import com.nervousfish.nervousfish.ConstantKeywords;
import com.nervousfish.nervousfish.R;

import org.apache.commons.lang3.SerializationUtils;

import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import nl.tudelft.ewi.ds.bankver.IBAN;

/**
 * A Java object representation (POJO) of a contact.
 */
@SuppressWarnings({"PMD.ImmutableField", "PMD.UselessParentheses"})
//1. We don't want the field iban to be final.
//2. The parentheses on line 125 are not useless.
public final class Contact implements Serializable {

    private static final long serialVersionUID = -4715364587956219157L;
    private final String name;
    private final List<IKey> keys = new ArrayList<>();
    private IBAN iban;

    /**
     * Constructor for the {@link Contact} POJO for a single {@link IKey}.
     *
     * @param name The name of the {@link Contact}
     * @param key  The {@link IKey} to initialize the {@link Contact} with
     */
    public Contact(final String name, final IKey key) {
        this.name = name;
        this.keys.add(SerializationUtils.clone(key));
    }

    /**
     * Constructor for the {@link Contact} POJO for a single {@link IKey},
     * and an IBAN.
     *
     * @param name The name of the {@link Contact}
     * @param key  The {@link IKey} to initialize the {@link Contact} with
     * @param iban  The IBAN of the {@link Contact}
     */
    public Contact(final String name, final IKey key, final IBAN iban) {
        this.name = name;
        this.keys.add(SerializationUtils.clone(key));
        this.iban = iban;
    }

    /**
     * Constructor for the {@link Contact} POJO for multiple {@link IKey}s.
     *
     * @param name The name of the {@link Contact}
     * @param keys The {@link Collection} of keys to initialize the {@link Contact} with
     */
    public Contact(final String name, final Collection<IKey> keys) {
        this.name = name;
        for (final IKey key : keys) {
            this.keys.add(SerializationUtils.clone(key));
        }
    }

    /**
     * Constructor for the {@link Contact} POJO for multiple {@link IKey}s, and
     * the IBAN.
     *
     * @param name The name of the {@link Contact}
     * @param keys The {@link Collection} of keys to initialize the {@link Contact} with
     * @param iban  The IBAN of the {@link Contact}
     */
    public Contact(final String name, final Collection<IKey> keys, final IBAN iban) {
        this.name = name;
        for (final IKey key : keys) {
            this.keys.add(SerializationUtils.clone(key));
        }
        this.iban = iban;
    }

    public String getName() {
        return this.name;
    }

    public IBAN getIban() {
        return this.iban;
    }

    /**
     * Retuns the string of the IBAN, and a dash if the object is null.
     *
     * @return String of the IBAN, and a dash if the object is null;
     */
    public String getIbanAsString() {
        if (this.getIban() != null) {
            return this.getIban().toString();
        }
        return "-";
    }

    /**
     * @return A list of all public keys of the contact
     */
    public List<IKey> getKeys() {
        final ArrayList<IKey> keysCopy = new ArrayList<>();
        for (final IKey key : this.keys) {
            keysCopy.add(SerializationUtils.clone(key));
        }
        return keysCopy;
    }

    /**
     * Adds a collection of keys to the public keys of the contact
     *
     * @param keys The public keys to add to the contact
     */
    public void addKeys(final Collection<IKey> keys) {
        for (final IKey key : keys) {
            this.keys.add(SerializationUtils.clone(key));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object obj) {
        if (obj == null || this.getClass() != obj.getClass()) {
            return false;
        }

        final Contact otherContact = (Contact) obj;
        return this.name.equals(otherContact.getName()) && this.keys.equals(otherContact.getKeys())
                && (this.iban == null ? otherContact.getIban() == null : this.iban.equals(otherContact.getIban()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return this.name.hashCode() + this.keys.hashCode() + Objects.hashCode(this.iban);
    }

    /**
     * Serialize the created proxy instead of this instance.
     */
    private Object writeReplace() {
        return new Contact.SerializationProxy(this);
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
        private final String name;
        private final IKey[] keys;
        private IBAN iban;

        /**
         * Constructs a new SerializationProxy
         *
         * @param contact The current instance of the proxy
         */
        SerializationProxy(final Contact contact) {
            this.name = contact.name;
            this.keys = contact.keys.toArray(new IKey[contact.keys.size()]);
            this.iban = contact.getIban();
        }

        /**
         * Not to be called by the user - resolves a new object of this proxy
         *
         * @return The object resolved by this proxy
         */
        private Object readResolve() {
            return new Contact(this.name, Arrays.asList(this.keys), iban);
        }
    }
}