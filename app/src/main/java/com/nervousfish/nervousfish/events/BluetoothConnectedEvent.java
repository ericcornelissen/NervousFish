package com.nervousfish.nervousfish.events;

import com.nervousfish.nervousfish.modules.pairing.AndroidConnectedThread;

/**
 * Greenrobot's EventBus message event
 * <p>
 * Sent when the device successfully paired with a Bluetooth device
 */
public final class BluetoothConnectedEvent {
    private final AndroidConnectedThread thread;

    /**
     * Constructs a new BluetoothConnectedEvent
     *
     * @param thread The thread handling the Bluetooth connection
     */
    public BluetoothConnectedEvent(final AndroidConnectedThread thread) {
        this.thread = thread;
    }

    public AndroidConnectedThread getThread() {
        return thread;
    }
}
