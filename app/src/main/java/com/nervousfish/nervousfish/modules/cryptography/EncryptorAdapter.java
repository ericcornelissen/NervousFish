package com.nervousfish.nervousfish.modules.cryptography;

import com.nervousfish.nervousfish.service_locator.IServiceLocator;
import com.nervousfish.nervousfish.service_locator.ModuleWrapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An adapter to the default Java class for encrypting messages
 */
public final class EncryptorAdapter implements IEncryptor {
    private static final Logger LOGGER = LoggerFactory.getLogger("EncryptorAdapter");

    /**
     * Prevents construction from outside the class.
     *
     * @param serviceLocator Can be used to get access to other modules
     */
    @SuppressWarnings("PMD.UnusedFormalParameter")
    // This servicelocator will be used later on probably
    private EncryptorAdapter(final IServiceLocator serviceLocator) {
        LOGGER.info("Initialized");
    }

    /**
     * Creates a new instance of itself and wraps it in a {@link ModuleWrapper} so that only an
     * {@link IServiceLocator}
     *
     * @param serviceLocator The new service locator
     * @return A wrapper around a newly created instance of this class
     */
    public static ModuleWrapper<EncryptorAdapter> newInstance(final IServiceLocator serviceLocator) {
        return new ModuleWrapper<>(new EncryptorAdapter(serviceLocator));
    }
}
