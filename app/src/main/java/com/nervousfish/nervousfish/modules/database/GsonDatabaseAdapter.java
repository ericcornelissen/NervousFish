package com.nervousfish.nervousfish.modules.database;

import com.nervousfish.nervousfish.service_locator.IServiceLocator;
import com.nervousfish.nervousfish.service_locator.IServiceLocatorBridge;
import com.nervousfish.nervousfish.service_locator.ModuleWrapper;

/**
 * An adapter to the Gson database library
 */
public final class GsonDatabaseAdapter implements IDatabase {
    /**
     * Creates a new instance of itself and wraps it in a {@link ModuleWrapper} so that only an {@link IServiceLocatorBridge}
     * can access the new module to create the new {@link IServiceLocator}.
     *
     * @param serviceLocatorBridge The service locator bridge that creates the new service locator
     * @return A wrapper around a newly created instance of this class
     */
    public static ModuleWrapper<GsonDatabaseAdapter> newInstance(IServiceLocatorBridge serviceLocatorBridge) {
        return new ModuleWrapper<>(new GsonDatabaseAdapter(serviceLocatorBridge));
    }

    /**
     * Prevents construction from outside the class.
     * @param serviceLocatorBridge The object responsible for creating the service locator
     */
    private GsonDatabaseAdapter(IServiceLocatorBridge serviceLocatorBridge) {
        final IServiceLocator serviceLocator = serviceLocatorBridge.getServiceLocator();
    }
}
