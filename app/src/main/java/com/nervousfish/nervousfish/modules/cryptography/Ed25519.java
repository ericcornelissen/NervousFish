package com.nervousfish.nervousfish.modules.cryptography;

import com.google.gson.stream.JsonWriter;
import com.nervousfish.nervousfish.data_objects.IKey;

import net.i2p.crypto.eddsa.EdDSAPrivateKey;
import net.i2p.crypto.eddsa.EdDSAPublicKey;
import net.i2p.crypto.eddsa.spec.EdDSANamedCurveTable;
import net.i2p.crypto.eddsa.spec.EdDSAParameterSpec;
import net.i2p.crypto.eddsa.spec.EdDSAPrivateKeySpec;
import net.i2p.crypto.eddsa.spec.EdDSAPublicKeySpec;

import java.io.IOException;
import java.security.SecureRandom;

/**
 * A generator for Ed25519 public/private key-pairs.
 * For more information on Ed25519, see: https://ed25519.cr.yp.to/
 */
public final class Ed25519 implements IKey {

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
        assert publicKey != null;
        assert privateKey != null;
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

    EdDSAPublicKey getPublicKey() {
        return this.publicKey;
    }

    EdDSAPrivateKey getPrivateKey() {
        return this.privateKey;
    }

    @Override
    public String getKey() {
        return null;
    }

    @Override
    public String getFormattedKey() {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public String getType() {
        return null;
    }

    @Override
    public void toJson(JsonWriter writer) throws IOException {

    }
}