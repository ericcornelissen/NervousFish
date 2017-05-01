package com.nervousfish.nervousfish.modules.cryptography;

import com.nervousfish.nervousfish.service_locator.IServiceLocator;
import com.nervousfish.nervousfish.service_locator.IServiceLocatorCreator;
import com.nervousfish.nervousfish.service_locator.ModuleWrapper;

/**
 * An adapter to the default Java class for encrypting messages
 */
public final class EncryptorAdapter implements IEncryptor {
    /**
     * Prevents construction from outside the class.
     *
     * @param serviceLocatorBridge The object responsible for creating the service locator
     */
    private EncryptorAdapter(IServiceLocatorCreator serviceLocatorBridge) {
        final IServiceLocator serviceLocator = serviceLocatorBridge.getServiceLocator();
    }

    /**
     * Creates a new instance of itself and wraps it in a {@link ModuleWrapper} so that only an {@link IServiceLocatorCreator}
     * can access the new module to create the new {@link IServiceLocator}.
     *
     * @param serviceLocatorBridge The service locator bridge that creates the new service locator
     * @return A wrapper around a newly created instance of this class
     */
    public static ModuleWrapper<EncryptorAdapter> newInstance(IServiceLocatorCreator serviceLocatorBridge) {
        return new ModuleWrapper<>(new EncryptorAdapter(serviceLocatorBridge));
    }
}
