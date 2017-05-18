package com.nervousfish.nervousfish.events;

/**
 * Greenrobot's EventBus message event
 *
 * Sent when the a new Bluetooth Device is detected
 */
public class BluetoothDeviceDetectedEvent {
    private final String name;

    /**
     * Constructs a new BluetoothDeviceDetectedEvent
     * @param name The name of the detected Bluetooth device
     */
    public BluetoothDeviceDetectedEvent(final String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
