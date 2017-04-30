package com.nervousfish.nervousfish.modules.filesystem;

import com.nervousfish.nervousfish.service_locator.IServiceLocator;
import com.nervousfish.nervousfish.service_locator.ServiceLocatorBridge;

public final class AndroidFileSystemAdapter implements IFileSystem {
    public static AndroidFileSystemAdapter register(ServiceLocatorBridge serviceLocatorBridge) {
        return new AndroidFileSystemAdapter(serviceLocatorBridge);
    }

    private AndroidFileSystemAdapter(ServiceLocatorBridge serviceLocatorBridge) {
        final IServiceLocator serviceLocator = serviceLocatorBridge.getServiceLocator();
    }
}
