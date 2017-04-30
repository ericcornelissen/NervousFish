package com.nervousfish.nervousfish.modules.database;

import com.nervousfish.nervousfish.service_locator.IServiceLocator;
import com.nervousfish.nervousfish.service_locator.ServiceLocatorBridge;

public final class GsonDatabaseAdapter implements IDatabase {
    public static GsonDatabaseAdapter register(ServiceLocatorBridge serviceLocatorBridge) {
        return new GsonDatabaseAdapter(serviceLocatorBridge);
    }

    private GsonDatabaseAdapter(ServiceLocatorBridge serviceLocatorBridge) {
        final IServiceLocator serviceLocator = serviceLocatorBridge.getServiceLocator();
    }
}
