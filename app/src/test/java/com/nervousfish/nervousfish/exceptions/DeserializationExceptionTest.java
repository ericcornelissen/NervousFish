package com.nervousfish.nervousfish.exceptions;

import org.junit.Test;

import static org.junit.Assert.*;

public class DeserializationExceptionTest {
    @Test(expected = DeserializationException.class)
    public void testStringConstructor() {
        throw new DeserializationException("foo");
    }
}