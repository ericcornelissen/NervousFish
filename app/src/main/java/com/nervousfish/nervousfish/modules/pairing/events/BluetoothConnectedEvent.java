package com.nervousfish.nervousfish.modules.pairing.events;

import com.nervousfish.nervousfish.modules.pairing.IBluetoothThread;

import org.apache.commons.lang3.Validate;

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
        Validate.notNull(thread);
        this.thread = thread;
    }

    IBluetoothThread getThread() {
        return this.thread;
    }

}
