package com.nervousfish.nervousfish.events;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class BluetoothPublicKeyReceivedEventTest {
    @Test
    public void testBluetoothPublicKeyReceivedEvent() {
        final PublicKeyReceivedEvent event = new PublicKeyReceivedEvent("foo");
        assertTrue(event.getKey().equals("foo"));
    }
}
