package com.nervousfish.nervousfish.modules.cryptography;

import com.nervousfish.nervousfish.data_objects.KeyPair;
import com.nervousfish.nervousfish.data_objects.RSAKey;
import com.nervousfish.nervousfish.service_locator.IServiceLocator;
import com.nervousfish.nervousfish.service_locator.ModuleWrapper;

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

    /**
     * Prevents construction from outside the class.
     *
     * @param serviceLocator Can be used to get access to other modules
     */
    @SuppressWarnings("PMD.UnusedFormalParameter") // This servicelocator will be used later on probably
    private KeyGeneratorAdapter(final IServiceLocator serviceLocator) {
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
     * Generates a random KeyPair with the RSA algorithm.
     *
     * @return a randomly generated KeyPair
     */
    public static KeyPair generateRSAKeyPair() throws NoSuchAlgorithmException, InvalidKeySpecException {
        final KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);

        final java.security.KeyPair keyPair = keyPairGenerator.generateKeyPair();
        final KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        final RSAPublicKeySpec publicKeySpec = keyFactory.getKeySpec(keyPair.getPublic(),
                RSAPublicKeySpec.class);
        final RSAPrivateKeySpec privateKeySpec = keyFactory.getKeySpec(keyPair.getPrivate(),
                RSAPrivateKeySpec.class);

        final RSAKey rsaPublicKey = new RSAKey(publicKeySpec.getModulus().toString(), publicKeySpec.getPublicExponent().toString());
        final RSAKey rsaPrivateKey = new RSAKey(privateKeySpec.getModulus().toString(), privateKeySpec.getPrivateExponent().toString());

        return new KeyPair(rsaPublicKey, rsaPrivateKey);
    }
}
