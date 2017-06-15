package com.nervousfish.nervousfish.modules.pairing.events;

import com.nervousfish.nervousfish.modules.pairing.IBluetoothThread;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

public class BluetoothConnectedEventTest {
    @Test
    public void testBluetoothConnectedEvent() {
        new BluetoothConnectedEvent(mock(IBluetoothThread.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBluetoothConnectedEventGetThreadNull() {
        new BluetoothConnectedEvent(null);
    }

    @Test
    public void testBluetoothConnectedEventGetThread() {
        IBluetoothThread thread = mock(IBluetoothThread.class);
        BluetoothConnectedEvent event = new BluetoothConnectedEvent(thread);
        assertEquals(event.getThread(), thread);
    }
}
