package com.nervousfish.nervousfish.modules.pairing;

import android.bluetooth.BluetoothDevice;

import java.io.IOException;

/**
 * Defines an object that can communicate over Bluetooth.
 */
interface IBluetoothCommunicator {

    /**
     * Start the bluetooth service. Specifically start AndroidAcceptThread to begin a
     * session in listening (server) mode.
     */
    void start() throws IOException;

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
