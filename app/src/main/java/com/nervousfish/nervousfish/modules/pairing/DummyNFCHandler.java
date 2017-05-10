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
    @SuppressWarnings("PMD.UnusedFormalParameter") // This servicelocator will be used later on probably
    private DummyNFCHandler(final IServiceLocator serviceLocator) {
        super();
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
