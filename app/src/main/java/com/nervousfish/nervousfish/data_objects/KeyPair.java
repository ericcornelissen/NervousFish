package com.nervousfish.nervousfish.data_objects;


import com.nervousfish.nervousfish.ConstantKeywords;

import org.apache.commons.lang3.Validate;

import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;

/**
 * KeyPair POJO which keeps a public and private key.
 */
public final class KeyPair implements Serializable {

    private static final long serialVersionUID = -9078691377598696344L;
    private final String name;
    private final IKey publicKey;
    private final IKey privateKey;

    /**
     * The constructor for the {@link KeyPair}.
     *
     * @param name       The name for the {@link KeyPair}.
     * @param publicKey  The publicKey of the {@link KeyPair}.
     * @param privateKey The privateKey of the {@link KeyPair}.
     */
    public KeyPair(final String name, final IKey publicKey, final IKey privateKey) {
        Validate.notBlank(name);
        Validate.notNull(publicKey);
        Validate.notNull(privateKey);
        this.name = name;
        this.publicKey = publicKey;
        this.privateKey = privateKey;
    }

    /**
     * Returns the name of the {@link KeyPair}.
     *
     * @return the name.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Returns the public key of the {@link KeyPair}.
     *
     * @return the public {@link IKey}.
     */
    public IKey getPublicKey() {
        return this.publicKey;
    }

    /**
     * Returns the private key of the {@link KeyPair}.
     *
     * @return the private {@link IKey}.
     */
    public IKey getPrivateKey() {
        return this.privateKey;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object o) {
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }

        final KeyPair that = (KeyPair) o;
        return this.name.equals(that.name)
                && this.publicKey.equals(that.publicKey)
                && this.privateKey.equals(that.privateKey);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return this.name.hashCode() + this.publicKey.hashCode() + this.privateKey.hashCode();
    }

    /**
     * Serialize the created proxy instead of this instance.
     */
    private Object writeReplace() {
        return new KeyPair.SerializationProxy(this);
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
        private static final long serialVersionUID = -9078691377598696344L;
        private final String name;
        private final IKey publicKey;
        private final IKey privateKey;

        /**
         * Constructs a new SerializationProxy
         *
         * @param keyPair The current instance of the proxy
         */
        SerializationProxy(final KeyPair keyPair) {
            this.name = keyPair.name;
            this.publicKey = keyPair.publicKey;
            this.privateKey = keyPair.privateKey;
        }

        /**
         * Not to be called by the user - resolves a new object of this proxy
         *
         * @return The object resolved by this proxy
         */
        private Object readResolve() {
            return new KeyPair(this.name, this.publicKey, this.privateKey);
        }
    }

}
