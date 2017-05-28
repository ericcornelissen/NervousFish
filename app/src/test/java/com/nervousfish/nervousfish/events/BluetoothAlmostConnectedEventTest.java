package com.nervousfish.nervousfish.events;

import android.bluetooth.BluetoothSocket;

import com.nervousfish.nervousfish.modules.pairing.AndroidConnectedThread;

import org.junit.Test;

import static org.mockito.Mockito.mock;

public class BluetoothAlmostConnectedEventTest {
    @Test
    public void testBluetoothAlmostConnectedEvent() {
        new BluetoothAlmostConnectedEvent(mock(BluetoothSocket.class));
    }
}
