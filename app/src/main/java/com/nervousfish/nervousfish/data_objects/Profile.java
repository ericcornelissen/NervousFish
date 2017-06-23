package com.nervousfish.nervousfish.data_objects;

import com.nervousfish.nervousfish.ConstantKeywords;

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
 * A Profile POJO to store a contact representing the user and the user's keypairs.
 */
@SuppressWarnings({"PMD.UselessParentheses", "PMD.NullAssignment"})
//1. The parentheses on line 104 are not useless.
//2. We want iban to be immutable, but not a required field.
public final class Profile implements Serializable {

    private static final long serialVersionUID = 8191245914949893284L;
    private final String name;
    private final List<RSAKeyPair> rsaKeypairs = new ArrayList<>();
    private final List<Ed25519KeyPair> ed25519Keypairs = new ArrayList<>();
    private final IBAN iban;


    /**
     * The constructor for the {@link Profile} class.
     *
     * @param name     The contact belonging to the user.
     * @param keyPairs the public/private key-pairs of the user.
     */
    public Profile(final String name, final List<RSAKeyPair> keyPairs) {
        Validate.notBlank(name);
        Validate.noNullElements(keyPairs);
        for (final RSAKeyPair keyPair : keyPairs) {
            this.rsaKeypairs.add(keyPair);
        }
        this.name = name;
        this.iban = null;
    }

    /**
     * The constructor for the {@link Profile} class. This one
     * includes the IBAN.
     *
     * @param name     The contact belonging to the user.
     * @param keyPairs The public/private key-pairs of the user.
     * @param iban     The iban of the user.
     */
    public Profile(final String name, final List<Ed25519KeyPair> keyPairs, final IBAN iban) {
        Validate.notBlank(name);
        Validate.noNullElements(keyPairs);
        for (final Ed25519KeyPair keyPair : keyPairs) {
            this.ed25519Keypairs.add(keyPair);
        }
        this.name = name;
        this.iban = iban;
    }

    /**
     * The constructor for the {@link Profile} class. This one
     * includes the IBAN.
     *
     * @param name     The contact belonging to the user.
     * @param keyPairs The public/private key-pairs of the user.
     * @param iban     The iban of the user.
     */
    public Profile(final String name, final List<RSAKeyPair> rsaKeyPairs, final List<Ed25519KeyPair> ed25519Keypairs, final IBAN iban) {
        Validate.notBlank(name);
        Validate.noNullElements(rsaKeyPairs);
        Validate.noNullElements(ed25519Keypairs);
        for (final RSAKeyPair keyPair : rsaKeyPairs) {
            this.rsaKeypairs.add(keyPair);
        }
        for (final Ed25519KeyPair keyPair : ed25519Keypairs) {
            this.ed25519Keypairs.add(keyPair);
        }
        this.name = name;
        this.iban = iban;
    }

    /**
     * Adds a new keyPair to the profile
     * <p>
     *
     * @param keyPair the keyPair to add.
     */
    public void addKeyPair(final RSAKeyPair keyPair) {
        Validate.notNull(keyPair);
        this.rsaKeypairs.add(keyPair);
    }

    /**
     * Adds a new keyPair to the profile
     * <p>
     *
     * @param keyPair the keyPair to add.
     */
    public void addKeyPair(final Ed25519KeyPair keyPair) {
        Validate.notNull(keyPair);
        this.ed25519Keypairs.add(keyPair);
    }

    /**
     * Returns the contact Object of the user
     *
     * @return - The Contact POJO.
     */

    public Contact getContact() {
        final List<RSAKeyWrapper> rsaKeys = new ArrayList<>();
        final List<Ed25519PublicKeyWrapper> ed25519Keys = new ArrayList<>();
        for (final RSAKeyPair pair : this.rsaKeypairs) {
            rsaKeys.add(pair.getPublicKey());
        }
        for (final Ed25519KeyPair pair : this.ed25519Keypairs) {
            ed25519Keys.add(pair.getPublicKey());
        }
        return new Contact(this.name, rsaKeys, ed25519Keys, this.iban);
    }

    /**
     * Returns the Keypairs of the user.
     *
     * @return The list of keypairs of the user.
     */
    public List<RSAKeyPair> getRSAKeyPairs() {
        return this.rsaKeypairs;
    }

    /**
     * Returns the Keypairs of the user.
     *
     * @return The list of keypairs of the user.
     */
    public List<Ed25519KeyPair> getEd25519KeyPairs() {
        return this.ed25519Keypairs;
    }

    public String getName() {
        return this.name;
    }

    public IBAN getIban() {
        return this.iban;
    }

    /**
     * Retuns the string of the IBAN, and an empty string if the object is null.
     *
     * @return String of the IBAN, and an empty string if the object is null;
     */
    public String getIbanAsString() {
        if (this.getIban() != null) {
            return this.getIban().toString();
        }
        return "";
    }


    /**
     * {@inheritDoc}
     */
    @Override

    public boolean equals(final Object obj) {
        if (obj == null || this.getClass() != obj.getClass()) {
            return false;
        }

        final Profile other = (Profile) obj;

        return this.name.equals(other.name) && this.rsaKeypairs.equals(other.rsaKeypairs) && this.ed25519Keypairs.equals(other.ed25519Keypairs)
                && (this.iban == null ? other.getIban() == null : this.iban.equals(other.getIban()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return this.name.hashCode() + this.rsaKeypairs.hashCode() + this.ed25519Keypairs.hashCode() + Objects.hashCode(this.iban);
    }

    /**
     * Serialize the created proxy instead of this instance.
     */
    private Object writeReplace() {
        return new Profile.SerializationProxy(this);
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
        private static final long serialVersionUID = 8191245914949893284L;
        private final String name;
        private final RSAKeyPair[] rsaKeyPairs;
        private final Ed25519KeyPair[] ed25519KeyPairs;
        private final IBAN iban;

        /**
         * Constructs a new SerializationProxy
         *
         * @param profile The current instance of the proxy
         */
        SerializationProxy(final Profile profile) {
            this.name = profile.name;
            this.rsaKeyPairs = profile.rsaKeypairs.toArray(new RSAKeyPair[profile.rsaKeypairs.size()]);
            this.ed25519KeyPairs = profile.ed25519Keypairs.toArray(new Ed25519KeyPair[profile.ed25519Keypairs.size()]);
            this.iban = profile.getIban();
        }

        /**
         * Not to be called by the user - resolves a new object of this proxy
         *
         * @return The object resolved by this proxy
         */
        private Object readResolve() {
            return new Profile(this.name, Arrays.asList(this.rsaKeyPairs), Arrays.asList(this.ed25519KeyPairs), this.iban);
        }
    }

}
