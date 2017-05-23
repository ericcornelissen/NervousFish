package com.nervousfish.nervousfish.modules.pairing;

import android.bluetooth.BluetoothDevice;

import com.nervousfish.nervousfish.modules.IModule;

/**
 * Defines a module used for pairing over Bluetooth that can be used by a service locator.
 */

public interface IBluetoothHandler extends IPairingHandler, IModule {

    /**
     * Start the bluetooth service. Specifically start AndroidAcceptThread to begin a
     * session in listening (server) mode.
     */
    void start();

    /**
     * Start the AndroidConnectThread to initiate a connection to a remote device.
     *
     * @param device The BluetoothDevice to connect
     */
    void connect(BluetoothDevice device);

    /**
     * Stop all threads
     */
    void stop();
}
