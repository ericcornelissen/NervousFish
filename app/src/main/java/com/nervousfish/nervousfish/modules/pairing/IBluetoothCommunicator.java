package com.nervousfish.nervousfish.modules.pairing;

import android.bluetooth.BluetoothDevice;

/**
 * Defines an object that can communicate over Bluetooth.
 */
public interface IBluetoothCommunicator {
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
