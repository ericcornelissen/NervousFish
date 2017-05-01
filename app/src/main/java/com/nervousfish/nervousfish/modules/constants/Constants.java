package com.nervousfish.nervousfish.modules.constants;

import com.nervousfish.nervousfish.service_locator.IServiceLocator;
import com.nervousfish.nervousfish.service_locator.IServiceLocatorCreator;
import com.nervousfish.nervousfish.service_locator.ModuleWrapper;

/**
 * Contains the main constants
 */
public final class Constants implements IConstants {
    /**
     * Creates a new instance of itself and wraps it in a {@link ModuleWrapper} so that only an {@link IServiceLocatorCreator}
     * can access the new module to create the new {@link IServiceLocator}.
     *
     * @param serviceLocatorBridge The service locator bridge that creates the new service locator
     * @return A wrapper around a newly created instance of this class
     */
    public static ModuleWrapper<Constants> newInstance(IServiceLocatorCreator serviceLocatorBridge) {
        return new ModuleWrapper<>(new Constants(serviceLocatorBridge));
    }

    /**
     * Prevents construction from outside the class.
     * @param serviceLocatorBridge The object responsible for creating the service locator
     */
    private Constants(IServiceLocatorCreator serviceLocatorBridge) {
        final IServiceLocator serviceLocator = serviceLocatorBridge.getServiceLocator();
    }
}
