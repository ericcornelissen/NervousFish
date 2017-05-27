package com.nervousfish.nervousfish.events;

import android.bluetooth.BluetoothSocket;

import org.junit.Test;

import static org.mockito.Mockito.mock;

public class PublicKeyReceivedEventTest {
    @Test
    public void testPublicKeyReceivedEvent() {
        new PublicKeyReceivedEvent("foo");
    }
}
