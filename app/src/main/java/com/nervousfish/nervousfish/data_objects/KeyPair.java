package com.nervousfish.nervousfish.data_objects;

/**
 * KeyPair POJO which keeps a public and private key.
 */

public class KeyPair {

    public IKey publicKey;
    public IKey privateKey;

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
}
