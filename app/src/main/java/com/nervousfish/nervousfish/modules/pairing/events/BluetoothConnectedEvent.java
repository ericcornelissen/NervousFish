package com.nervousfish.nervousfish.modules.pairing.events;

import com.nervousfish.nervousfish.modules.pairing.IBluetoothThread;

/**
 * Greenrobot's EventBus message event
 * <p>
 * Sent when the device successfully paired with a Bluetooth device
 */
public final class BluetoothConnectedEvent {
    private final IBluetoothThread thread;

    /**
     * Constructs a new BluetoothConnectedEvent
     *
     * @param thread The thread handling the Bluetooth connection
     * @throws IllegalArgumentException when thread is null
     */
    public BluetoothConnectedEvent(final IBluetoothThread thread) {
        if (thread == null) {
            throw new IllegalArgumentException("Thread cannot be null");
        }
        this.thread = thread;
    }

    public IBluetoothThread getThread() {
        return this.thread;
    }
}
