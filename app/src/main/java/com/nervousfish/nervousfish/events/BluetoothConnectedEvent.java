package com.nervousfish.nervousfish.events;

import com.nervousfish.nervousfish.modules.pairing.AndroidBluetoothConnectedThread;

/**
 * Greenrobot's EventBus message event
 * <p>
 * Sent when the device successfully paired with a Bluetooth device
 */
public final class BluetoothConnectedEvent {
    private final AndroidBluetoothConnectedThread thread;

    /**
     * Constructs a new BluetoothConnectedEvent
     *
     * @param thread The thread handling the Bluetooth connection
     */
    public BluetoothConnectedEvent(final AndroidBluetoothConnectedThread thread) {
        this.thread = thread;
    }

    public AndroidBluetoothConnectedThread getThread() {
        return thread;
    }
}
