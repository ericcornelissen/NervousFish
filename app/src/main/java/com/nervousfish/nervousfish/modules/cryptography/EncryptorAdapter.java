package com.nervousfish.nervousfish.modules.cryptography;

import com.nervousfish.nervousfish.service_locator.IServiceLocator;
import com.nervousfish.nervousfish.service_locator.ModuleWrapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * An adapter to the default Java class for encrypting messages
 */
public final class EncryptorAdapter implements IEncryptor {
    private static final long serialVersionUID = 5930930748980177440L;
    private static final Logger LOGGER = LoggerFactory.getLogger("EncryptorAdapter");

    /**
     * Prevents construction from outside the class.
     *
     * @param serviceLocator Can be used to get access to other modules
     */
    // We suppress UnusedFormalParameter because the chance is big that a service locator will be used in the future
    @SuppressWarnings("PMD.UnusedFormalParameter")
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
    @SuppressWarnings("MethodReturnOfConcreteClass")
    public static ModuleWrapper<EncryptorAdapter> newInstance(final IServiceLocator serviceLocator) {
        return new ModuleWrapper<>(new EncryptorAdapter(serviceLocator));
    }

}
