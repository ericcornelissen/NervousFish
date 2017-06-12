package com.nervousfish.nervousfish.exceptions;

import org.junit.Test;

import java.io.IOException;

public class SocketCreationExceptionTest {
    @Test(expected = SocketCreationException.class)
    public void testStringConstructor() {
        throw new SocketCreationException("foo");
    }

    @Test(expected = SocketCreationException.class)
    public void testThrowableConstructor() {
        throw new SocketCreationException(new IOException("foo"));
    }
}