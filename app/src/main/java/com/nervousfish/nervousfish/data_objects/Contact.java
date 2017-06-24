package com.nervousfish.nervousfish.data_objects;

import com.nervousfish.nervousfish.ConstantKeywords;

import net.i2p.crypto.eddsa.EdDSAPublicKey;

import org.apache.commons.lang3.Validate;

import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.security.interfaces.RSAKey;
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

    public static class ContactBuilder {
        private final String name;
        private final List<RSAKeyWrapper> rsaKeys = new ArrayList<>();
        private final List<Ed25519PublicKeyWrapper> ed25519Keys = new ArrayList<>();
        private IBAN iban;

        public ContactBuilder(final String name) {
            this.name = name;
        }

        public void addRSAKey(final RSAKeyWrapper rsaKeyWrapper) {
            this.rsaKeys.add(rsaKeyWrapper);
        }

        public void addEd25519Key(final Ed25519PublicKeyWrapper ed25519PublicKeyWrapper) {
            this.ed25519Keys.add(ed25519PublicKeyWrapper);
        }

        public void addRSAKeys(final List<RSAKeyWrapper> rsaKeyWrappers) {
            this.rsaKeys.addAll(rsaKeyWrappers);
        }

        public void addEd25519Keys(final List<Ed25519PublicKeyWrapper> ed25519PublicKeyWrappers) {
            this.ed25519Keys.addAll(ed25519PublicKeyWrappers);
        }

        public void setIban(final IBAN iban) {
            this.iban = iban;
        }

        public Contact build() {
            return new Contact(this.name, this.rsaKeys, this.ed25519Keys, this.iban);
        }
    }

    /**
     * Constructor for the {@link Contact} POJO for a single {@link IKey},
     * and an IBAN.
     *
     * @param name The name of the {@link Contact}
     * @param key  The {@link IKey} to initialize the {@link Contact} with
     * @param iban  The IBAN of the {@link Contact}
     */
    private Contact(final String name, final List<RSAKeyWrapper> rsaKeys, final List<Ed25519PublicKeyWrapper> ed25519Keys, final IBAN iban) {
        this.name = name;
        for (final RSAKeyWrapper rsaKey : rsaKeys) {
            this.rsaKeys.add(rsaKey);
        }
        for (final Ed25519PublicKeyWrapper ed25519key : ed25519Keys) {
            this.ed25519Keys.add(ed25519key);
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
        if (this.iban != null) {
            return this.iban.toString();
        }
        return "-";
    }

    public List<IKey<?>> getKeys() {
        final List<IKey<?>> result = new ArrayList<>(this.rsaKeys);
        result.addAll(this.ed25519Keys);
        return result;
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

    public void addRSAKeys(final List<RSAKeyWrapper> rsaKeys) {
        this.rsaKeys.addAll(rsaKeys);
    }

    public void addEd25519Keys(final List<Ed25519PublicKeyWrapper> ed25519Keys) {
        this.ed25519Keys.addAll(ed25519Keys);
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
        }

        /**
         * Not to be called by the user - resolves a new object of this proxy
         *
         * @return The object resolved by this proxy
         */
        private Object readResolve() {
            return new Contact(this.name, Arrays.asList(this.rsaKeys), Arrays.asList(this.ed25519Keys), this.iban);
        }
    }
}