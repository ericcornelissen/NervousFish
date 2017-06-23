package com.nervousfish.nervousfish.data_objects;

import com.nervousfish.nervousfish.ConstantKeywords;
import com.nervousfish.nervousfish.modules.cryptography.Ed25519;

import net.i2p.crypto.eddsa.EdDSAPrivateKey;
import net.i2p.crypto.eddsa.EdDSAPublicKey;

import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;

public final class Ed25519KeyPair extends AKeyPair<Ed25519PublicKeyWrapper, Ed25519PrivateKeyWrapper> {
    /**
     * The constructor for the {@link AKeyPair}.
     *
     * @param name       The name for the {@link AKeyPair}.
     * @param publicKey  The publicKey of the {@link AKeyPair}.
     * @param privateKey The privateKey of the {@link AKeyPair}.
     */
    public Ed25519KeyPair(final String name, final Ed25519PublicKeyWrapper publicKey, final Ed25519PrivateKeyWrapper privateKey) {
        super(name, publicKey, privateKey);
    }

    /**
     * Returns the public key of the {@link AKeyPair}.
     *
     * @return the public {@link IKey}.
     */
    public Ed25519PublicKeyWrapper getPublicKey() {
        return this.publicKey;
    }

    /**
     * Returns the private key of the {@link AKeyPair}.
     *
     * @return the private {@link IKey}.
     */
    public Ed25519PrivateKeyWrapper getPrivateKey() {
        return this.privateKey;
    }

    /**
     * Serialize the created proxy instead of this instance.
     */
    private Object writeReplace() {
        return new Ed25519KeyPair.SerializationProxy(this);
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
        private final Ed25519PublicKeyWrapper publicKey;
        private final Ed25519PrivateKeyWrapper privateKey;

        /**
         * Constructs a new SerializationProxy
         *
         * @param keyPair The current instance of the proxy
         */
        SerializationProxy(final Ed25519KeyPair keyPair) {
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
            return new Ed25519KeyPair(this.name, this.publicKey, this.privateKey);
        }
    }
}
