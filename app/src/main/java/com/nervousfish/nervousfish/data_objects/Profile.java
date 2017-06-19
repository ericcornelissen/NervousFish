package com.nervousfish.nervousfish.data_objects;

import com.nervousfish.nervousfish.ConstantKeywords;

import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A Profile POJO to store a contact representing the user and the user's keypairs.
 */
public class Profile implements Serializable {

    private static final long serialVersionUID = 8191245914949893284L;
    private final String name;
    private final List<KeyPair> keyPairs;


    /**
     * The constructor for the {@link Profile} class.
     *
     * @param name     The contact belonging to the user.
     * @param keyPairs the public/private key-pairs of the user.
     */
    public Profile(final String name, final List<KeyPair> keyPairs) {
        this.keyPairs = keyPairs;
        this.name = name;
    }

    /**
     * Adds a new keyPair to the profile
     *
     * @param keyPair the keyPair to add.
     */
    public void addKeyPair(final KeyPair keyPair) {
        this.keyPairs.add(keyPair);
    }

    /**
     * Returns the contact Object of the user
     *
     * @return - The Contact POJO.
     */
    public Contact getContact() {
        final List<IKey> publicKeys = new ArrayList<>();
        for (final KeyPair pair : this.keyPairs) {
            publicKeys.add(pair.getPublicKey());
        }
        return new Contact(this.name, publicKeys);
    }

    /**
     * Returns the Keypairs of the user.
     *
     * @return The list of keypairs of the user.
     */
    public List<KeyPair> getKeyPairs() {
        return this.keyPairs;
    }

    /**
     * Returns the name of the profile.
     *
     * @return Name of the profile.
     */
    public String getName() {
        return this.name;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object o) {
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }

        final Profile that = (Profile) o;

        return this.name.equals(that.name) && this.keyPairs.equals(that.keyPairs);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return this.name.hashCode() + this.keyPairs.hashCode();
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
        private final KeyPair[] keyPairs;

        /**
         * Constructs a new SerializationProxy
         *
         * @param profile The current instance of the proxy
         */
        SerializationProxy(final Profile profile) {
            this.name = profile.name;
            this.keyPairs = profile.keyPairs.toArray(new KeyPair[profile.keyPairs.size()]);
        }

        /**
         * Not to be called by the user - resolves a new object of this proxy
         *
         * @return The object resolved by this proxy
         */
        private Object readResolve() {
            return new Profile(this.name, Arrays.asList(this.keyPairs));
        }
    }

}
