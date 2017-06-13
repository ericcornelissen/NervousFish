package com.nervousfish.nervousfish.data_objects;

import com.nervousfish.nervousfish.ConstantKeywords;

import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

/**
 * A Profile POJO to store a contact representing the user and the user's keypairs.
 */
public class Profile implements Serializable {

    private static final long serialVersionUID = -4715364587956219157L;
    private final Contact contact;
    private final List<KeyPair> keyPairs;

    /**
     * The constructor for the {@link Profile} class.
     *
     * @param contact The contact belonging to the user.
     * @param keyPairs the public/private key-pairs of the user.
     */
    public Profile(final Contact contact, final List<KeyPair> keyPairs) {
        this.contact = contact;
        this.keyPairs = keyPairs;
    }

    /**
     * Adds a new keyPair to the profile
     * @param keyPair the keyPair to add.
     */
    public void addKeyPair(final KeyPair keyPair) {
        keyPairs.add(keyPair);
        contact.getKeys().add(keyPair.getPublicKey());
    }

    /**
     * Returns the contact Object of the user
     * @return - The Contact POJO.
     */
    public Contact getContact() {
        return contact;
    }

    /**
     * Returns the Keypairs of the user.
     * @return The list of keypairs of the user.
     */
    public List<KeyPair> getKeyPairs() {
        return keyPairs;
    }

    /**
     * Returns the (first) public key of the profile.
     * @return the (first) public key.
     * TODO: Let the user decide which public key is active.
     */
    public IKey getPublicKey() { return keyPairs.get(0).getPublicKey(); }

    /**
     * Returns the name of the profile.
     * @return Name of the profile.
     */
    public String getName() { return contact.getName(); }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object o) {
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }

        final Profile that = (Profile) o;
       
        return this.contact.equals(that.contact) && this.keyPairs.equals(that.keyPairs);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return this.contact.hashCode() + this.keyPairs.hashCode();
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
        private static final long serialVersionUID = -4715364587956219157L;
        private final Contact contact;
        private final KeyPair[] keyPairs;

        /**
         * Constructs a new SerializationProxy
         * @param profile The current instance of the proxy
         */
        SerializationProxy(final Profile profile) {
            this.contact = profile.contact;
            this.keyPairs = profile.keyPairs.toArray(new KeyPair[profile.keyPairs.size()]);
        }

        /**
         * Not to be called by the user - resolves a new object of this proxy
         * @return The object resolved by this proxy
         */
        private Object readResolve() {
            return new Profile(this.contact, Arrays.asList(this.keyPairs));
        }
    }

}
