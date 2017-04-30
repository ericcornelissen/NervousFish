package com.nervousfish.nervousfish.modules.cryptography;

import com.nervousfish.nervousfish.service_locator.IServiceLocator;
import com.nervousfish.nervousfish.service_locator.ServiceLocatorBridge;

public final class KeyGeneratorAdapter implements IKeyGenerator {
    public static KeyGeneratorAdapter register(ServiceLocatorBridge serviceLocatorBridge) {
        return new KeyGeneratorAdapter(serviceLocatorBridge);
    }

    private KeyGeneratorAdapter(ServiceLocatorBridge serviceLocatorBridge) {
        final IServiceLocator serviceLocator = serviceLocatorBridge.getServiceLocator();
    }
}
