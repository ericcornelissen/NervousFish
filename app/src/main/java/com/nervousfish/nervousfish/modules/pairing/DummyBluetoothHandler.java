package com.nervousfish.nervousfish.modules.pairing;

import android.bluetooth.BluetoothDevice;

import com.nervousfish.nervousfish.service_locator.IServiceLocator;
import com.nervousfish.nervousfish.service_locator.ModuleWrapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * A Bluetooth handler without implementation, needed because Bluetooth is unavailable on the emulator
 */
public final class DummyBluetoothHandler extends APairingHandler implements IBluetoothHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger("DummyBluetoothHandler");

    /**
     * Prevents construction from outside the class.
     *
     * @param serviceLocator Can be used to get access to other modules
     */
    private DummyBluetoothHandler(final IServiceLocator serviceLocator) {
        super(serviceLocator);
        LOGGER.info("Initialized");
    }

    /**
     * Creates a new instance of itself and wraps it in a {@link ModuleWrapper} so that only an
     * {@link IServiceLocator}
     *
     * @param serviceLocator The servicelocator that gives access to the other modules
     * @return A wrapper around a newly created instance of this class
     */
    public static ModuleWrapper<DummyBluetoothHandler> newInstance(final IServiceLocator serviceLocator) {
        return new ModuleWrapper<>(new DummyBluetoothHandler(serviceLocator));
    }

    @Override
    public void start() {
        // Do nothing
    }

    @Override
    public void connect(final BluetoothDevice device) {
        // Do nothing
    }

    @Override
    public void restart() throws IOException {
        // Do nothing
    }

    @Override
    public void stop() {
        // Do nothing
    }

    @Override
    public void send(final byte[] buffer) {
        // Do nothing
    }
}