package com.nervousfish.nervousfish.modules.cryptography;

import com.nervousfish.nervousfish.data_objects.Ed25519Key;
import com.nervousfish.nervousfish.data_objects.KeyPair;
import com.nervousfish.nervousfish.data_objects.RSAKey;
import com.nervousfish.nervousfish.service_locator.IServiceLocator;
import com.nervousfish.nervousfish.service_locator.ModuleWrapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.KeyFactory;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;

/**
 * An adapter to the default Java class for generating keys
 */
public final class KeyGeneratorAdapter implements IKeyGenerator {
    private static final Logger LOGGER = LoggerFactory.getLogger("KeyGeneratorAdapter");

    private static final String RSA_KEY_ALGORITHM = "RSA";
    private static final int RSA_KEY_SIZE = 2048;

    /**
     * Prevents construction from outside the class.
     *
     * @param serviceLocator Can be used to get access to other modules
     */
    // We suppress UnusedFormalParameter because the chance is big that a service locator will be used in the future
    @SuppressWarnings("PMD.UnusedFormalParameter")
    private KeyGeneratorAdapter(final IServiceLocator serviceLocator) {
        LOGGER.info("Initialized");
    }

    /**
     * Creates a new instance of itself and wraps it in a {@link ModuleWrapper} so that only an
     * {@link IServiceLocator}
     *
     * @param serviceLocator The new service locator
     * @return A wrapper around a newly created instance of this class
     */
    public static ModuleWrapper<KeyGeneratorAdapter> newInstance(final IServiceLocator serviceLocator) {
        return new ModuleWrapper<>(new KeyGeneratorAdapter(serviceLocator));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public KeyPair generateRSAKeyPair(final String name) {
        try {
            final KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(RSA_KEY_ALGORITHM);
            keyPairGenerator.initialize(RSA_KEY_SIZE);

            final java.security.KeyPair keyPair = keyPairGenerator.generateKeyPair();
            final KeyFactory keyFactory = KeyFactory.getInstance(RSA_KEY_ALGORITHM);
            final RSAPublicKeySpec publicKeySpec = keyFactory.getKeySpec(keyPair.getPublic(),
                    RSAPublicKeySpec.class);
            final RSAPrivateKeySpec privateKeySpec = keyFactory.getKeySpec(keyPair.getPrivate(),
                    RSAPrivateKeySpec.class);

            final RSAKey rsaPublicKey = new RSAKey(name, publicKeySpec.getModulus().toString(), publicKeySpec.getPublicExponent().toString());
            final RSAKey rsaPrivateKey = new RSAKey(name, privateKeySpec.getModulus().toString(), privateKeySpec.getPrivateExponent().toString());

            return new KeyPair(name, rsaPublicKey, rsaPrivateKey);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new KeyGenerationException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public KeyPair generateEd25519KeyPair(final String name) throws KeyGenerationException {
        final Ed25519 keyPairGenerator = Ed25519.generatePair();
        final Ed25519Key publicKey = new Ed25519Key(name, keyPairGenerator.getPublicKey());
        final Ed25519Key privateKey = new Ed25519Key(name, keyPairGenerator.getPrivateKey());
        return new KeyPair(name, publicKey, privateKey);
    }

}
