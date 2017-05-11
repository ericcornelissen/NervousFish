package com.nervousfish.nervousfish.modules.pairing;

import com.nervousfish.nervousfish.service_locator.IServiceLocator;
import com.nervousfish.nervousfish.service_locator.ModuleWrapper;

/**
 * An handler doing nothing.
 */
public final class DummyQRHandler extends APairingHandler implements IQRHandler {

    /**
     * Prevents construction from outside the class.
     *
     * @param serviceLocator Can be used to get access to other modules
     */
    private DummyQRHandler(final IServiceLocator serviceLocator) {
        super(serviceLocator);
    }

    /**
     * {@inheritDoc}
     * @param buffer The bytes to write
     */
    @Override
    void write(final byte[] buffer) {
        //needs to be implemented
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void showWarning() {
        //needs to be implemented
    }

    /**
     * Creates a new instance of itself and wraps it in a {@link ModuleWrapper} so that only an
     * {@link IServiceLocator}
     *
     * @param serviceLocator The new service locator
     * @return A wrapper around a newly created instance of this class
     */
    public static ModuleWrapper<DummyQRHandler> newInstance(final IServiceLocator serviceLocator) {
        return new ModuleWrapper<>(new DummyQRHandler(serviceLocator));
    }
}
