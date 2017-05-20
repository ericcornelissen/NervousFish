package com.nervousfish.nervousfish.modules.cryptography;

import com.nervousfish.nervousfish.data_objects.KeyPair;
import com.nervousfish.nervousfish.data_objects.RSAKey;
import com.nervousfish.nervousfish.service_locator.IServiceLocator;
import com.nervousfish.nervousfish.service_locator.ModuleWrapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
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
    private static final long serialVersionUID = -5933759426888012276L;
    private static final Logger LOGGER = LoggerFactory.getLogger("KeyGeneratorAdapter");
    private static final String RSA_KEY_ALGORITHM = "RSA";
    private static final int RSA_KEY_SIZE = 2048;

    /**
     * Prevents construction from outside the class.
     *
     * @param serviceLocator Can be used to get access to other modules
     */
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
     * Generates a random KeyPair with the RSA algorithm.
     *
     * @param name The name of the key
     * @return a randomly generated KeyPair
     */
    public KeyPair generateRSAKeyPair(final String name) throws NoSuchAlgorithmException, InvalidKeySpecException {
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
    }

    /**
     * Deserialize the instance using readObject to ensure invariants and security.
     * @param stream The serialized object to be deserialized
     */
    private void readObject(final ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        ensureClassInvariant();
    }

    /**
     * Used to improve performance / efficiency
     * @param stream The stream to which this object should be serialized to
     */
    private void writeObject(final ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
    }

    /**
     * Ensure that the instance meets its class invariant
     * @throws InvalidObjectException Thrown when the state of the class is unstbale
     */
    private void ensureClassInvariant() throws InvalidObjectException {

    }
}
