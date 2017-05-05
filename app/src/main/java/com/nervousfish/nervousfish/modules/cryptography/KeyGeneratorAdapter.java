package com.nervousfish.nervousfish.modules.cryptography;

import com.nervousfish.nervousfish.service_locator.IServiceLocator;
import com.nervousfish.nervousfish.service_locator.IServiceLocatorCreator;
import com.nervousfish.nervousfish.service_locator.ModuleWrapper;

import java.security.KeyFactory;
import java.security.KeyPair;
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
     * @param serviceLocatorCreator The object responsible for creating the service locator
     */
    @SuppressWarnings("PMD.UnusedFormalParameter")
    private KeyGeneratorAdapter(final IServiceLocatorCreator serviceLocatorCreator) {
        // final IServiceLocator serviceLocator = serviceLocatorCreator.getServiceLocator();
    }

    /**
     * Creates a new instance of itself and wraps it in a {@link ModuleWrapper} so that only an {@link IServiceLocatorCreator}
     * can access the new module to create the new {@link IServiceLocator}.
     *
     * @param serviceLocatorCreator The service locator bridge that creates the new service locator
     * @return A wrapper around a newly created instance of this class
     */
    public static ModuleWrapper<KeyGeneratorAdapter> newInstance(final IServiceLocatorCreator serviceLocatorCreator) {
        return new ModuleWrapper<>(new KeyGeneratorAdapter(serviceLocatorCreator));
    }

    /**
     * Generates a random KeyPair with the RSA algorithm.
     * @return a randomly generated KeyPair
     */
    public static KeyPair generateRandomKeyPair() throws NoSuchAlgorithmException {
        final KeyPairGenerator k = KeyPairGenerator.getInstance("RSA");
        k.initialize(2048);

        return k.generateKeyPair();
    }

    /**
     * Gets from the parameter 'kp' the public key and makes a RSAPublicKeySpec out of it.
     * @param keyPair the KeyPair with the public key
     * @return the RSAPublicKeySpec
     */
    public static RSAPublicKeySpec getPublicKeySpec(final KeyPair keyPair) throws NoSuchAlgorithmException,
            InvalidKeySpecException {
        final KeyFactory fact = KeyFactory.getInstance("RSA");

        return fact.getKeySpec(keyPair.getPublic(),
                RSAPublicKeySpec.class);
    }

    /**
     * Gets from the parameter 'kp' the private key and makes a RSAPrivateKeySpec out of it.
     * @param keyPair the KeyPair with the private key
     * @return the RSAPrivateKeySpec
     */
    public static RSAPrivateKeySpec getPrivateKeySpec(final KeyPair keyPair) throws NoSuchAlgorithmException,
            InvalidKeySpecException {
        final KeyFactory fact = KeyFactory.getInstance("RSA");

        return fact.getKeySpec(keyPair.getPrivate(),
                RSAPrivateKeySpec.class);
    }
}
