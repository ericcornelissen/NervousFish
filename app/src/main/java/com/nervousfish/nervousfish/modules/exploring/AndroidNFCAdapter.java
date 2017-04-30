package com.nervousfish.nervousfish.modules.exploring;

import com.nervousfish.nervousfish.service_locator.IServiceLocator;
import com.nervousfish.nervousfish.service_locator.ServiceLocatorBridge;

public final class AndroidNFCAdapter extends APairingHandler implements INFCHandler {
    public static AndroidNFCAdapter register(ServiceLocatorBridge serviceLocatorBridge) {
        return new AndroidNFCAdapter(serviceLocatorBridge);
    }

    private AndroidNFCAdapter(ServiceLocatorBridge serviceLocatorBridge) {
        final IServiceLocator serviceLocator = serviceLocatorBridge.getServiceLocator();
    }
}
