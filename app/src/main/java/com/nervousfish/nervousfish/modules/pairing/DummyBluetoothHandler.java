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

    /**
     * {@inheritDoc} see IBluetoothHandler for a real example
     * @param device The BluetoothDevice to connect
     */
    @Override
    public void connect(final BluetoothDevice device) {
        //needs to be implemented
    }

    /**
     * {@inheritDoc} see IBluetoothHandler for a real example
     */
    @Override
    public void start() {
        //needs to be implemented
    }

    /**
     * {@inheritDoc} see IBluetoothHandler for a real example
     * @param socket The BluetoothSocket on which the connection was made
     * @param device The BluetoothDevice that has been connected
     */
    @Override
    public void connected(final BluetoothSocket socket, final BluetoothDevice device) {
        //needs to be implemented
    }

    /**
     * {@inheritDoc} see IBluetoothHandler for a real example
     */
    @Override
    public void stop() {
        //needs to be implemented
    }

    /**
     * {@inheritDoc} see IBluetoothHandler for a real example
     * @param buffer The bytes to write
     */
    @Override
    void write(final byte[] buffer) {
        //needs to be implemented
    }

    /**
     * {@inheritDoc} see IBluetoothHandler for a real example
     */
    @Override
    void showWarning() {
        //needs to be implemented
    }
}
