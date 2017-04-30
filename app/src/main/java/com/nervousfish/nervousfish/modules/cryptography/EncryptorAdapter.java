package com.nervousfish.nervousfish.modules.cryptography;

import com.nervousfish.nervousfish.service_locator.IServiceLocator;
import com.nervousfish.nervousfish.service_locator.ServiceLocatorBridge;

public final class EncryptorAdapter implements IEncryptor {
    public static EncryptorAdapter register(ServiceLocatorBridge serviceLocatorBridge) {
        return new EncryptorAdapter(serviceLocatorBridge);
    }

    private EncryptorAdapter(ServiceLocatorBridge serviceLocatorBridge) {
        final IServiceLocator serviceLocator = serviceLocatorBridge.getServiceLocator();
    }
}
