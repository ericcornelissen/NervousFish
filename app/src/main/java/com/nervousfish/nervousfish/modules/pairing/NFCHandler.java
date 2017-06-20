package com.nervousfish.nervousfish.modules.pairing;

import com.nervousfish.nervousfish.service_locator.IServiceLocator;
import com.nervousfish.nervousfish.service_locator.ModuleWrapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An handler for NFC communication without implementation, needed because NFC is unavailable on the emulator
 */
public final class NFCHandler extends APairingHandler implements INfcHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger("NFCHandler");

    /**
     * Prevents construction from outside the class.
     *
     * @param serviceLocator Can be used to get access to other modules
     */
    private NFCHandler(final IServiceLocator serviceLocator) {
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
    public static ModuleWrapper<NFCHandler> newInstance(final IServiceLocator serviceLocator) {
        return new ModuleWrapper<>(new NFCHandler(serviceLocator));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void send(final byte[] buffer) {
        // The NFC Handler handles the exchange of bytes in the activity
        LOGGER.error("NFC Handler's send method has been called!!!");
        throw new UnsupportedOperationException("Shouldn't be used");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void dataReceived(final byte[] bytes) {
        getDataReceiver().get().dataReceived(bytes);
    }
}
