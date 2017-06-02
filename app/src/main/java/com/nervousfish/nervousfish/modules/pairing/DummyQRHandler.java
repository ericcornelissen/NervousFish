package com.nervousfish.nervousfish.modules.pairing;

import com.nervousfish.nervousfish.service_locator.IServiceLocator;
import com.nervousfish.nervousfish.service_locator.ModuleWrapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An handler doing nothing.
 */
public final class DummyQRHandler extends APairingHandler implements IQRHandler {

    private static final long serialVersionUID = -1164062335787406761L;
    private static final Logger LOGGER = LoggerFactory.getLogger("DummyQRHandler");

    /**
     * Prevents construction from outside the class.
     *
     * @param serviceLocator Can be used to get access to other modules
     */
    private DummyQRHandler(final IServiceLocator serviceLocator) {
        super(serviceLocator);
        LOGGER.info("Initialized");
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

    /**
     * {@inheritDoc}
     */
    @Override
    public void send(final byte[] buffer) {
        throw new UnsupportedOperationException("Sending data over QR is not implemented yet");
    }

}
