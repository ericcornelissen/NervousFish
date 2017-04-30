package com.nervousfish.nervousfish.modules.constants;

import com.nervousfish.nervousfish.service_locator.IServiceLocator;
import com.nervousfish.nervousfish.service_locator.ServiceLocatorBridge;

public final class Constants implements IConstants {
    public static Constants register(ServiceLocatorBridge serviceLocatorBridge) {
        return new Constants(serviceLocatorBridge);
    }

    private Constants(ServiceLocatorBridge serviceLocatorBridge) {
        final IServiceLocator serviceLocator = serviceLocatorBridge.getServiceLocator();
    }
}
