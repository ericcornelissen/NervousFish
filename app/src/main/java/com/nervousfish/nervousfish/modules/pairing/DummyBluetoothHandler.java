package com.nervousfish.nervousfish.modules.pairing;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import com.nervousfish.nervousfish.service_locator.IServiceLocator;
import com.nervousfish.nervousfish.service_locator.ModuleWrapper;

/**
 * An handler doing nothing.
 */
public final class DummyBluetoothHandler extends APairingHandler implements IBluetoothHandler {

    /**
     * Prevents construction from outside the class.
     *
     * @param serviceLocator Can be used to get access to other modules
     */
    @SuppressWarnings("PMD.UnusedFormalParameter") // This servicelocator will be used later on probably
    private DummyBluetoothHandler(final IServiceLocator serviceLocator) {
        super(serviceLocator);
    }

    /**
     * Creates a new instance of itself and wraps it in a {@link ModuleWrapper} so that only an
     * {@link IServiceLocator}
     *
     * @param serviceLocator The new service locator
     * @return A wrapper around a newly created instance of this class
     */
    public static ModuleWrapper<DummyBluetoothHandler> newInstance(final IServiceLocator serviceLocator) {
        return new ModuleWrapper<>(new DummyBluetoothHandler(serviceLocator));
    }

    @Override
    public void connect(BluetoothDevice device) {

    }

    @Override
    public void start() {

    }

    @Override
    public void connected(BluetoothSocket socket, BluetoothDevice device) {

    }

    @Override
    public void stop() {

    }

    @Override
    void write(byte[] buffer) {

    }

    @Override
    void showWarning() {

    }
}
