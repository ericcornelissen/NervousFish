package com.nervousfish.nervousfish.modules.pairing;

import com.nervousfish.nervousfish.service_locator.IServiceLocator;
import com.nervousfish.nervousfish.service_locator.ModuleWrapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An handler for NFC communication without implementation, needed because NFC is unavailable on the emulator
 */
public final class DummyNFCHandler extends APairingHandler implements INfcHandler {

    private static final long serialVersionUID = -6465987636766819498L;
    private static final Logger LOGGER = LoggerFactory.getLogger("DummyNFCHandler");

    /**
     * Prevents construction from outside the class.
     *
     * @param serviceLocator Can be used to get access to other modules
     */
    private DummyNFCHandler(final IServiceLocator serviceLocator) {
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
    public static ModuleWrapper<DummyNFCHandler> newInstance(final IServiceLocator serviceLocator) {
        return new ModuleWrapper<>(new DummyNFCHandler(serviceLocator));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void send(final byte[] buffer) {
        throw new UnsupportedOperationException("Sending data with NFC should not be implemented here");
    }

    @Override
    public void dataReceived(byte[] bytes) {
        throw new UnsupportedOperationException("Receiving data with NFC should not be implemented here");
    }
}
