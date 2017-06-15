package com.nervousfish.nervousfish.modules.database;

import org.junit.Test;

import java.io.IOException;

public class DatabaseExceptionTest {
    @Test(expected = DatabaseException.class)
    public void testStringConstructor() {
        throw new DatabaseException("foo");
    }

    @Test(expected = DatabaseException.class)
    public void testThrowableConstructor() {
        throw new DatabaseException(new IOException("foo"));
    }
}