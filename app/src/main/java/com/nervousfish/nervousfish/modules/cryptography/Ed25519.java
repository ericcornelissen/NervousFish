package com.nervousfish.nervousfish.modules.cryptography;

import net.i2p.crypto.eddsa.EdDSAPrivateKey;
import net.i2p.crypto.eddsa.EdDSAPublicKey;
import net.i2p.crypto.eddsa.spec.EdDSANamedCurveTable;
import net.i2p.crypto.eddsa.spec.EdDSAParameterSpec;
import net.i2p.crypto.eddsa.spec.EdDSAPrivateKeySpec;
import net.i2p.crypto.eddsa.spec.EdDSAPublicKeySpec;

import org.apache.commons.lang3.Validate;

import java.security.SecureRandom;

/**
 * A generator for Ed25519 public/private key-pairs.
 * For more information on Ed25519, see: https://ed25519.cr.yp.to/
 */
final class Ed25519 {

    private static final int SEED_LENGTH = 32;
    private static final EdDSAParameterSpec PARAMETER_SPEC = EdDSANamedCurveTable.getByName(EdDSANamedCurveTable.CURVE_ED25519_SHA512);

    private final EdDSAPublicKey publicKey;
    private final EdDSAPrivateKey privateKey;

    /**
     * Create a new {@link Ed25519}.
     *
     * @param publicKey  The generated public key.
     * @param privateKey The generated private key.
     */
    private Ed25519(final EdDSAPublicKey publicKey, final EdDSAPrivateKey privateKey) {
        Validate.notNull(publicKey);
        Validate.notNull(privateKey);
        this.publicKey = publicKey;
        this.privateKey = privateKey;
    }

    /**
     * Get an instance of a Ed25519Generator, which provides a public/private key-pair
     * based on the Ed25519 algorithm.
     *
     * @return A new {@link Ed25519} instance.
     */
    static Ed25519 generatePair() {
        // Create a random seed
        final SecureRandom generator = new SecureRandom();
        final byte[] seed = generator.generateSeed(SEED_LENGTH);

        // Create a private key
        final EdDSAPrivateKeySpec privateKeySpec = new EdDSAPrivateKeySpec(seed, PARAMETER_SPEC);
        final EdDSAPrivateKey privateKey = new EdDSAPrivateKey(privateKeySpec);

        // Create a public key
        final byte[] publicSeed = privateKey.getAbyte();
        final EdDSAPublicKeySpec publicKeySpec = new EdDSAPublicKeySpec(publicSeed, PARAMETER_SPEC);
        final EdDSAPublicKey publicKey = new EdDSAPublicKey(publicKeySpec);

        // Return an object from which the public and private key can be obtained
        return new Ed25519(publicKey, privateKey);
    }

    String getPublicKey() {
        return this.publicKey.toString();
    }

    String getPrivateKey() {
        return this.privateKey.toString();
    }

}