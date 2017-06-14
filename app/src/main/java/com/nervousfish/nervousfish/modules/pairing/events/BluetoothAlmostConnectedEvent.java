package com.nervousfish.nervousfish.modules.pairing.events;

import android.bluetooth.BluetoothSocket;

/**
 * Greenrobot's EventBus message event
 * <p>
 * Sent when the device can be connected as soon as the application wants it.
 * {@link BluetoothConnectedEvent} should be posted after that is done.
 */
public final class BluetoothAlmostConnectedEvent {
    private final BluetoothSocket socket;

    /**
     * Constructs a new BluetoothAlmostConnectedEvent
     *
     * @param socket The socket to the server
     */
    public BluetoothAlmostConnectedEvent(final BluetoothSocket socket) {
        this.socket = socket;
    }

    public BluetoothSocket getSocket() {
        return this.socket;
    }
}
