package com.nervousfish.nervousfish.data_objects;

import com.nervousfish.nervousfish.ConstantKeywords;

import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.Validate;

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
@SuppressWarnings({"PMD.UselessParentheses", "PMD.NullAssignment"})
//1. The parentheses on line 125 are not useless.
//2. We want iban to be immutable, but not a required field.
public final class Contact implements Serializable {
    private static final long serialVersionUID = -4715364587956219157L;

    private final String name;
    private final List<IKey> keys = new ArrayList<>();
    private final IBAN iban;
    private final boolean ibanVerified;

    /**
     * Constructor for the {@link Contact} POJO for a single {@link IKey}.
     *
     * @param name The name of the {@link Contact}
     * @param key  The {@link IKey} to initialize the {@link Contact} with
     */
    public Contact(final String name, final IKey key) {
        Validate.notBlank(name);
        Validate.notNull(key);
        this.name = name;
        this.keys.add(SerializationUtils.clone(key));
        this.iban = null;
        this.ibanVerified = false;
    }

    /**
     * Constructor for the {@link Contact} POJO for a single {@link IKey},
     * and an IBAN.
     *
     * @param name The name of the {@link Contact}
     * @param key  The {@link IKey} to initialize the {@link Contact} with
     * @param iban  The IBAN of the {@link Contact}
     * @param ibanVerified The boolean if the IBAN is verified or not.
     */
    public Contact(final String name, final IKey key, final IBAN iban, final boolean ibanVerified) {
        Validate.notBlank(name);
        Validate.notNull(key);
        this.name = name;
        this.keys.add(SerializationUtils.clone(key));
        this.iban = iban;
        this.ibanVerified = ibanVerified;
    }

    /**
     * Constructor for the {@link Contact} POJO for multiple {@link IKey}s.
     *
     * @param name The name of the {@link Contact}
     * @param keys The {@link Collection} of keys to initialize the {@link Contact} with
     */
    public Contact(final String name, final Collection<IKey> keys) {
        Validate.notBlank(name);
        Validate.noNullElements(keys);
        this.name = name;
        for (final IKey key : keys) {
            this.keys.add(SerializationUtils.clone(key));
        }
        this.iban = null;
        this.ibanVerified = false;
    }

    /**
     * Constructor for the {@link Contact} POJO for multiple {@link IKey}s, and
     * the IBAN.
     *
     * @param name The name of the {@link Contact}
     * @param keys The {@link Collection} of keys to initialize the {@link Contact} with
     * @param iban  The IBAN of the {@link Contact}
     * @param ibanVerified The boolean if the IBAN is verified or not.
     */
    public Contact(final String name, final Collection<IKey> keys, final IBAN iban, final boolean ibanVerified) {
        this.name = name;
        for (final IKey key : keys) {
            this.keys.add(SerializationUtils.clone(key));
        }
        this.iban = iban;
        this.ibanVerified = ibanVerified;
    }

    public String getName() {
        return this.name;
    }

    public IBAN getIban() {
        return this.iban;
    }

    public boolean getIbanVerified() {
        return this.ibanVerified;
    }

    /**
     * Returns the first Ed25519 Key of the user.
     *
     * @return The first Ed25519 Key of the user.
     */
    public IKey getFirstEd25519Key() {
        for (final IKey key : this.keys) {
            if (key.getType().equals(ConstantKeywords.ED25519_KEY)) {
                return key;
            }
        }
        throw new IllegalStateException("No Ed25519 key found");
    }

    /**
     * Retuns the string of the IBAN, and a dash if the object is null.
     *
     * @return String of the IBAN, and a dash if the object is null;
     */
    public String getIbanAsString() {
        if (this.iban != null) {
            return this.iban.toString();
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
        private final IBAN iban;
        private final boolean ibanVerified;

        /**
         * Constructs a new SerializationProxy
         *
         * @param contact The current instance of the proxy
         */
        SerializationProxy(final Contact contact) {
            this.name = contact.name;
            this.keys = contact.keys.toArray(new IKey[contact.keys.size()]);
            this.iban = contact.getIban();
            this.ibanVerified = contact.getIbanVerified();
        }

        /**
         * Not to be called by the user - resolves a new object of this proxy
         *
         * @return The object resolved by this proxy
         */
        private Object readResolve() {
            return new Contact(this.name, Arrays.asList(this.keys), this.iban, this.ibanVerified);
        }
    }
}