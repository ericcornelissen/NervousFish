package com.nervousfish.nervousfish.modules.pairing;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import com.nervousfish.nervousfish.modules.IModule;

/**
 * Defines a module used for pairing over Bluetooth that can be used by a service locator.
 */

public interface IBluetoothHandler extends IPairingHandler, IModule {

    /**
     * Start the bluetooth service. Specifically start AcceptThread to begin a
     * session in listening (server) mode.
     */
    void start();

    /**
     * Start the ConnectThread to initiate a connection to a remote device.
     *
     * @param device The BluetoothDevice to connect
     */
    void connect(final BluetoothDevice device);

    /**
     * Start the ConnectedThread to begin managing a Bluetooth connection
     *
     * @param socket The BluetoothSocket on which the connection was made
     * @param device The BluetoothDevice that has been connected
     */
    void connected(final BluetoothSocket socket, final BluetoothDevice
            device);

    /**
     * Stop all threads
     */
    void stop();
}
