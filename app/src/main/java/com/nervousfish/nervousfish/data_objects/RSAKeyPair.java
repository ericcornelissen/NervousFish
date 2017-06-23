package com.nervousfish.nervousfish.data_objects;

import com.nervousfish.nervousfish.ConstantKeywords;

import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;

public final class RSAKeyPair extends AKeyPair<RSAKeyWrapper, RSAKeyWrapper> {
    /**
     * The constructor for the {@link AKeyPair}.
     *
     * @param name       The name for the {@link AKeyPair}.
     * @param publicKey  The publicKey of the {@link AKeyPair}.
     * @param privateKey The privateKey of the {@link AKeyPair}.
     */
    public RSAKeyPair(String name, RSAKeyWrapper publicKey, RSAKeyWrapper privateKey) {
        super(name, publicKey, privateKey);
    }

    /**
     * Returns the public key of the {@link AKeyPair}.
     *
     * @return the public {@link IKey}.
     */
    public RSAKeyWrapper getPublicKey() {
        return this.publicKey;
    }

    /**
     * Returns the private key of the {@link AKeyPair}.
     *
     * @return the private {@link IKey}.
     */
    public RSAKeyWrapper getPrivateKey() {
        return this.privateKey;
    }

    /**
     * Serialize the created proxy instead of this instance.
     */
    private Object writeReplace() {
        return new RSAKeyPair.SerializationProxy(this);
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
        private final RSAKeyWrapper publicKey;
        private final RSAKeyWrapper privateKey;

        /**
         * Constructs a new SerializationProxy
         *
         * @param keyPair The current instance of the proxy
         */
        SerializationProxy(final RSAKeyPair keyPair) {
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
            return new RSAKeyPair(this.name, this.publicKey, this.privateKey);
        }
    }
}
