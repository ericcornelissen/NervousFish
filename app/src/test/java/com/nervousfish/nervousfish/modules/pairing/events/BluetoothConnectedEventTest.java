package com.nervousfish.nervousfish.modules.pairing.events;

import com.nervousfish.nervousfish.modules.pairing.AndroidBluetoothConnectedThread;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

public class BluetoothConnectedEventTest {
    @Test
    public void testBluetoothConnectedEvent() {
        new BluetoothConnectedEvent(null);
    }

    @Test
    public void testBluetoothConnectedEventGetThreadNull() {
         // We cannot test with a real class intead of null because we cannot mock final classes
        BluetoothConnectedEvent event = new BluetoothConnectedEvent(null);
        assertEquals(event.getThread(), null);
    }
}
