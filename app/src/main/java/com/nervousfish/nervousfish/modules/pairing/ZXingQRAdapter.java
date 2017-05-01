package com.nervousfish.nervousfish.modules.pairing;

import com.nervousfish.nervousfish.service_locator.IServiceLocator;
import com.nervousfish.nervousfish.service_locator.IServiceLocatorCreator;
import com.nervousfish.nervousfish.service_locator.ModuleWrapper;

/**
 * An adapter to the ZXing QR code library
 */
public final class ZXingQRAdapter implements IQRHandler {
    /**
     * Creates a new instance of itself and wraps it in a {@link ModuleWrapper} so that only an {@link IServiceLocatorCreator}
     * can access the new module to create the new {@link IServiceLocator}.
     *
     * @param serviceLocatorBridge The service locator bridge that creates the new service locator
     * @return A wrapper around a newly created instance of this class
     */
    public static ModuleWrapper<ZXingQRAdapter> newInstance(IServiceLocatorCreator serviceLocatorBridge) {
        return new ModuleWrapper<>(new ZXingQRAdapter(serviceLocatorBridge));
    }

    /**
     * Prevents construction from outside the class.
     * @param serviceLocatorBridge The object responsible for creating the service locator
     */
    private ZXingQRAdapter(IServiceLocatorCreator serviceLocatorBridge) {
        final IServiceLocator serviceLocator = serviceLocatorBridge.getServiceLocator();
    }
}
