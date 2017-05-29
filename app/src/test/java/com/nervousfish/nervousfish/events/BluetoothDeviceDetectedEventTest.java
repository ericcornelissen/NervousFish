package com.nervousfish.nervousfish.events;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class BluetoothDeviceDetectedEventTest {
    @Test
    public void testBluetoothDeviceDetectedEvent() {
        final BluetoothDeviceDetectedEvent event = new BluetoothDeviceDetectedEvent("foo");
        assertTrue(event.getName().equals("foo"));
    }
}
