package com.nervousfish.nervousfish.modules.exploring;

import com.nervousfish.nervousfish.service_locator.IServiceLocator;
import com.nervousfish.nervousfish.service_locator.ServiceLocatorBridge;

public final class AndroidQRAdapter extends APairingHandler implements IQRHandler {
    public static AndroidQRAdapter register(ServiceLocatorBridge serviceLocatorBridge) {
        return new AndroidQRAdapter(serviceLocatorBridge);
    }

    private AndroidQRAdapter(ServiceLocatorBridge serviceLocatorBridge) {
        final IServiceLocator serviceLocator = serviceLocatorBridge.getServiceLocator();
    }
}
