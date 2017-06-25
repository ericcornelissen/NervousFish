package com.nervousfish.nervousfish.modules.cryptography;

import net.i2p.crypto.eddsa.EdDSAPrivateKey;
import net.i2p.crypto.eddsa.EdDSAPublicKey;
import net.i2p.crypto.eddsa.spec.EdDSANamedCurveTable;
import net.i2p.crypto.eddsa.spec.EdDSAParameterSpec;
import net.i2p.crypto.eddsa.spec.EdDSAPrivateKeySpec;
import net.i2p.crypto.eddsa.spec.EdDSAPublicKeySpec;

import java.security.SecureRandom;

/**
 * A generator for Ed25519Generator public/private key-pairs.
 * For more information on Ed25519Generator, see: https://ed25519.cr.yp.to/
 */
public final class Ed25519Generator {

    private static final int SEED_LENGTH = 32;
    private static final EdDSAParameterSpec PARAMETER_SPEC = EdDSANamedCurveTable.getByName(EdDSANamedCurveTable.CURVE_ED25519_SHA512);

    private final EdDSAPublicKey publicKey;
    private final EdDSAPrivateKey privateKey;
    private final byte[] seed;

    /**
     * Create a new {@link Ed25519Generator}.
     *
     * @param publicKey  The generated public key.
     * @param privateKey The generated private key.
     */
    private Ed25519Generator(final EdDSAPublicKey publicKey, final EdDSAPrivateKey privateKey, final byte[] seed) {
        assert publicKey != null;
        assert privateKey != null;
        assert seed != null;

        this.publicKey = publicKey;
        this.privateKey = privateKey;
        this.seed = seed;
    }

    /**
     * Get an instance of a Ed25519Generator, which provides a public/private key-pair
     * based on the Ed25519Generator algorithm.
     *
     * @return A new {@link Ed25519Generator} instance.
     */
    public static Ed25519Generator generatePair() {
        // Create a random seed
        final SecureRandom generator = new SecureRandom();
        final byte[] seed = generator.generateSeed(SEED_LENGTH);
        return Ed25519Generator.generatePair(seed);
    }

    /**
     * Get an instance of a Ed25519Generator, which provides a public/private key-pair
     * based on the Ed25519Generator algorithm.
     *
     * @return A new {@link Ed25519Generator} instance.
     */
    public static Ed25519Generator generatePair(final byte[] seed) {
        // Create a private key
        final EdDSAPrivateKeySpec privateKeySpec = new EdDSAPrivateKeySpec(seed, PARAMETER_SPEC);
        final EdDSAPrivateKey privateKey = new EdDSAPrivateKey(privateKeySpec);

        // Create a public key
        final byte[] publicSeed = privateKey.getAbyte();
        final EdDSAPublicKeySpec publicKeySpec = new EdDSAPublicKeySpec(publicSeed, PARAMETER_SPEC);
        final EdDSAPublicKey publicKey = new EdDSAPublicKey(publicKeySpec);

        // Return an object from which the public and private key can be obtained
        return new Ed25519Generator(publicKey, privateKey, seed);
    }

    public EdDSAPublicKey getPublicKey() {
        return this.publicKey;
    }

    public EdDSAPrivateKey getPrivateKey() {
        return this.privateKey;
    }

    byte[] getSeed() {
        return this.seed;
    }

}