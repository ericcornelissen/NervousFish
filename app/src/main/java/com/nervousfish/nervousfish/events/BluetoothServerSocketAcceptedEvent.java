package com.nervousfish.nervousfish.events;

import android.bluetooth.BluetoothSocket;

/**
 * Greenrobot's EventBus message event
 *
 * Sent when the request from the bluetooth server to pair is accepted
 */
public final class BluetoothServerSocketAcceptedEvent {
    private final BluetoothSocket socket;

    /**
     * Constructs a new BluetoothServerSocketAcceptedEvent
     * @param socket The socket to the server
     */
    public BluetoothServerSocketAcceptedEvent(final BluetoothSocket socket) {
        this.socket = socket;
    }

    public BluetoothSocket getSocket() {
        return socket;
    }
}
