package com.nervousfish.nervousfish.events;

import android.bluetooth.BluetoothSocket;

import org.junit.Test;

import static org.mockito.Mockito.mock;

public class BluetoothServerSocketAcceptedEventTest {
    @Test
    public void testBluetoothServerSocketAcceptedEvent() {
        new BluetoothServerSocketAcceptedEvent(mock(BluetoothSocket.class));
    }
}
