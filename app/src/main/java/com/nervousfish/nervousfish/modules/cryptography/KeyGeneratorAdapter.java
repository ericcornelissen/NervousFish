package com.nervousfish.nervousfish.modules.cryptography;

import com.nervousfish.nervousfish.data_objects.KeyPair;
import com.nervousfish.nervousfish.data_objects.RSAKey;
import com.nervousfish.nervousfish.events.SLReadyEvent;
import com.nervousfish.nervousfish.service_locator.IServiceLocator;
import com.nervousfish.nervousfish.service_locator.IServiceLocatorCreator;
import com.nervousfish.nervousfish.service_locator.ModuleWrapper;

import java.security.KeyFactory;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import org.greenrobot.eventbus.Subscribe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An adapter to the default Java class for generating keys
 */
public final class KeyGeneratorAdapter implements IKeyGenerator {
    private final Logger logger = LoggerFactory.getLogger("Constants");
    @SuppressWarnings("PMD.SingularField")
    private final IServiceLocatorCreator serviceLocatorCreator;

    /**
     * Prevents construction from outside the class.
     *
     * @param serviceLocatorCreator The object responsible for creating the service locator
     */
    private KeyGeneratorAdapter(final IServiceLocatorCreator serviceLocatorCreator) {
        this.serviceLocatorCreator = serviceLocatorCreator;
        this.serviceLocatorCreator.registerToEventBus(this);
        logger.info("Initialized");
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

    /**
     * {@inheritDoc}
     */
    @Subscribe
    @Override
    public void onSLReadyEvent(final SLReadyEvent event) {
        // Here you can get modules from the service locator
    }
}
