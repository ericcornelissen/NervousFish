package com.nervousfish.nervousfish.events;

import android.bluetooth.BluetoothSocket;

import org.junit.Test;

import static org.mockito.Mockito.mock;

public class BluetoothConnectionFailedEventTest {
    @Test
    public void testBluetoothConnectionFailedEvent() {
        new BluetoothConnectionFailedEvent();
    }
}
