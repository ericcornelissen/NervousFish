package com.nervousfish.nervousfish.data_objects;

/**
 * KeyPair POJO which keeps a public and private key.
 */

public final class KeyPair {

    private final IKey publicKey;
    private final IKey privateKey;

    /**
     * Constructor for the KeyPair.
     *
     * @param publicKey The publicKey of the KeyPair.
     * @param privateKey The privateKey of the KeyPair.
     */
    public KeyPair(final IKey publicKey, final IKey privateKey) {
        this.publicKey = publicKey;
        this.privateKey = privateKey;
    }

    /**
     * Returns the public key of this KeyPair.
     * @return the public key as IKey
     */
    public IKey getPublicKey() {
        return this.publicKey;
    }

    /**
     * Returns the private key of this KeyPair.
     * @return the private key as IKey
     */
    public IKey getPrivateKey() {
        return this.privateKey;
    }
}