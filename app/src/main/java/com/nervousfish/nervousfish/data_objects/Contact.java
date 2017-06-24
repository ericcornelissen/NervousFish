package com.nervousfish.nervousfish.data_objects;

import com.nervousfish.nervousfish.ConstantKeywords;

import net.i2p.crypto.eddsa.EdDSAPublicKey;

import org.apache.commons.lang3.Validate;

import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
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
    private final List<RSAKeyWrapper> rsaKeys = new ArrayList<>();
    private final List<Ed25519PublicKeyWrapper> ed25519Keys = new ArrayList<>();
    private final IBAN iban;
    private boolean ibanVerified;

    /**
     * Constructor for the {@link Contact} POJO for a single {@link IKey}.
     *
     * @param name The name of the {@link Contact}
     * @param rsaKey  The {@link IKey} to initialize the {@link Contact} with
     */
    public Contact(final String name, final RSAKeyWrapper rsaKey) {
        Validate.notBlank(name);
        Validate.notNull(rsaKeys);
        this.name = name;
        this.rsaKeys.add(rsaKey);
        this.iban = null;
    }

    /**
     * Constructor for the {@link Contact} POJO for a single {@link IKey}.
     *
     * @param name The name of the {@link Contact}
     * @param ed25519Key  The {@link IKey} to initialize the {@link Contact} with
     */
    public Contact(final String name, final Ed25519PublicKeyWrapper ed25519Key, final IBAN iban) {
        Validate.notBlank(name);
        Validate.notNull(ed25519Keys);
        Validate.notNull(iban);
        this.name = name;
        this.ed25519Keys.add(ed25519Key);
        this.iban = iban;
    }

    /**
     * Constructor for the {@link Contact} POJO for a single {@link IKey},
     * and an IBAN.
     *
     * @param name The name of the {@link Contact}
     * @param rsaKeys  The {@link List<RSAKeyWrapper>} to initialize the {@link Contact} with
     * @param ed25519Keys  The {@link List<Ed25519PublicKeyWrapper>} to initialize the {@link Contact} with
     * @param iban  The IBAN of the {@link Contact}
     */
    public Contact(final String name, final List<RSAKeyWrapper> rsaKeys, final List<Ed25519PublicKeyWrapper> ed25519Keys,
                   final IBAN iban, final boolean ibanVerified) {
        Validate.notBlank(name);
        Validate.noNullElements(rsaKeys);
        Validate.noNullElements(ed25519Keys);
        this.name = name;
        for (final RSAKeyWrapper rsaKey : rsaKeys) {
            this.rsaKeys.add(rsaKey);
        }
        for (final Ed25519PublicKeyWrapper ed25519key : ed25519Keys) {
            this.ed25519Keys.add(ed25519key);
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

    public void setIbanVerified(final boolean ibanVerified) {
        this.ibanVerified = ibanVerified;
    }
    public boolean getIbanVerified() {
        return this.ibanVerified;
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
     * @return A list of all public rsa keys of the contact
     */
    public List<RSAKeyWrapper> getRSAKeys() {
        return this.rsaKeys;
    }

    /**
     * @return A list of all public ed25519 keys of the contact
     */
    public List<Ed25519PublicKeyWrapper> getEd25519Keys() {
        return this.ed25519Keys;
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
        return this.name.equals(otherContact.getName()) && this.rsaKeys.equals(otherContact.getRSAKeys())
                && this.ed25519Keys.equals(otherContact.getEd25519Keys())
                && (this.iban == null ? otherContact.getIban() == null : this.iban.equals(otherContact.getIban()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return this.name.hashCode() + this.rsaKeys.hashCode() + this.ed25519Keys.hashCode() + Objects.hashCode(this.iban);
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
        private final RSAKeyWrapper[] rsaKeys;
        private final Ed25519PublicKeyWrapper[] ed25519Keys;
        private final IBAN iban;
        private final boolean ibanVerified;

        /**
         * Constructs a new SerializationProxy
         *
         * @param contact The current instance of the proxy
         */
        SerializationProxy(final Contact contact) {
            this.name = contact.name;
            this.rsaKeys = contact.rsaKeys.toArray(new RSAKeyWrapper[contact.rsaKeys.size()]);
            this.ed25519Keys = contact.ed25519Keys.toArray(new Ed25519PublicKeyWrapper[contact.ed25519Keys.size()]);
            this.iban = contact.getIban();
            this.ibanVerified = contact.getIbanVerified();
        }

        /**
         * Not to be called by the user - resolves a new object of this proxy
         *
         * @return The object resolved by this proxy
         */
        private Object readResolve() {
            return new Contact(this.name, Arrays.asList(this.rsaKeys), Arrays.asList(this.ed25519Keys),
                    this.iban, this.ibanVerified);
        }
    }
}