package com.nervousfish.nervousfish.modules.pairing;

import com.nervousfish.nervousfish.service_locator.IServiceLocator;
import com.nervousfish.nervousfish.service_locator.ModuleWrapper;

/**
 * An handler doing nothing.
 */
public final class DummyNFCHandler extends APairingHandler implements INFCHandler {

    /**
     * Prevents construction from outside the class.
     *
     * @param serviceLocator Can be used to get access to other modules
     */
    private DummyNFCHandler(final IServiceLocator serviceLocator) {
        super(serviceLocator);
    }

    @Override
    void write(final byte[] buffer) {

    }

    @Override
    void showWarning() {

    }

    /**
     * Creates a new instance of itself and wraps it in a {@link ModuleWrapper} so that only an
     * {@link IServiceLocator}
     *
     * @param serviceLocator The new service locator
     * @return A wrapper around a newly created instance of this class
     */
    public static ModuleWrapper<DummyNFCHandler> newInstance(final IServiceLocator serviceLocator) {
        return new ModuleWrapper<>(new DummyNFCHandler(serviceLocator));
    }
}
