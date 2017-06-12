package com.nervousfish.nervousfish.exceptions;

import org.junit.Test;

public class NoBluetoothExceptionTest {
    @Test(expected = NoBluetoothException.class)
    public void testStringConstructor() {
        throw new NoBluetoothException("foo");
    }
}