package com.nervousfish.nervousfish.modules.exploring;

import com.nervousfish.nervousfish.service_locator.IServiceLocator;
import com.nervousfish.nervousfish.service_locator.ServiceLocatorBridge;

public final class AndroidBluetoothAdapter extends APairingHandler implements IBluetoothHandler {
    public static AndroidBluetoothAdapter register(ServiceLocatorBridge serviceLocatorBridge) {
        return new AndroidBluetoothAdapter(serviceLocatorBridge);
    }

    private AndroidBluetoothAdapter(ServiceLocatorBridge serviceLocatorBridge) {
        final IServiceLocator serviceLocator = serviceLocatorBridge.getServiceLocator();
    }
}
