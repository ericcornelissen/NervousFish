package com.nervousfish.nervousfish.modules.cryptography;

import com.nervousfish.nervousfish.service_locator.IServiceLocator;
import com.nervousfish.nervousfish.service_locator.IServiceLocatorCreator;
import com.nervousfish.nervousfish.service_locator.ModuleWrapper;

/**
 * An adapter to the default Java class for generating keys
 */
public final class KeyGeneratorAdapter implements IKeyGenerator {
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
     * Prevents construction from outside the class.
     * @param serviceLocatorCreator The object responsible for creating the service locator
     */
    @SuppressWarnings("PMD.UnusedFormalParameter")
    private KeyGeneratorAdapter(final IServiceLocatorCreator serviceLocatorCreator) {
        // final IServiceLocator serviceLocator = serviceLocatorCreator.getServiceLocator();
    }
}
