package com.nervousfish.nervousfish.modules.cryptography;

import net.i2p.crypto.eddsa.EdDSAPrivateKey;
import net.i2p.crypto.eddsa.EdDSAPublicKey;

import nl.tudelft.ewi.ds.bankver.cryptography.ED25519;

/**
 * A generator for Ed25519Generator public/private key-pairs.
 * For more information on Ed25519Generator, see: https://ed25519.cr.yp.to/
 */
public final class Ed25519Generator {

    private final EdDSAPublicKey publicKey;
    private final EdDSAPrivateKey privateKey;

    /**
     * Create a new {@link Ed25519Generator}.
     *
     * @param publicKey  The generated public key.
     * @param privateKey The generated private key.
     */
    private Ed25519Generator(final EdDSAPublicKey publicKey, final EdDSAPrivateKey privateKey) {
        assert publicKey != null;
        assert privateKey != null;

        this.publicKey = publicKey;
        this.privateKey = privateKey;
    }

    /**
     * Get an instance of a Ed25519Generator, which provides a public/private key-pair
     * based on the Ed25519Generator algorithm.
     *
     * @return A new {@link Ed25519Generator} instance.
     */
    public static Ed25519Generator generatePair() {
        return Ed25519Generator.generatePair(ED25519.generatePrivateKey());
    }

    /**
     * Get an instance of a Ed25519Generator, which provides a public/private privateKey-pair
     * based on the Ed25519Generator algorithm.
     *
     * @param privateKey The private key key
     * @return A new {@link Ed25519Generator} instance.
     */
    static Ed25519Generator generatePair(final EdDSAPrivateKey privateKey) {
        final EdDSAPublicKey publicKey = ED25519.getPublicKey(privateKey);

        // Return an object from which the public and private privateKey can be obtained
        return new Ed25519Generator(publicKey, privateKey);
    }

    /**
     * @return The public key
     */
    public EdDSAPublicKey getPublicKey() {
        return this.publicKey;
    }

    /**
     * @return The private key
     */
    public EdDSAPrivateKey getPrivateKey() {
        return this.privateKey;
    }

}