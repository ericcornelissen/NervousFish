package com.nervousfish.nervousfish.exceptions;

import com.nervousfish.nervousfish.TestException;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import static org.junit.Assert.assertTrue;

public class UnknownIntervalExceptionTest {
    @Test
    public void testStringConstructor() {
        UnknownIntervalException UnknownIntervalException = new UnknownIntervalException("foo");
        assertTrue(UnknownIntervalException.getMessage().equals("foo"));
    }

    @Test
    public void testThrowableConstructor() {
        final Throwable throwable = new Throwable();
        UnknownIntervalException UnknownIntervalException = new UnknownIntervalException(throwable);
        assertTrue(UnknownIntervalException.getCause().equals(throwable));
    }

    @Test
    public void testSerializationWithException() throws IOException, ClassNotFoundException {
        final UnknownIntervalException exception = new UnknownIntervalException(new TestException("foo"));
        try (
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(bos)
        ) {
            oos.writeObject(exception);
            byte[] bytes = bos.toByteArray();
            try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
                 ObjectInputStream ois = new ObjectInputStream(bis)) {
                Object exception1 = ois.readObject();
                assertTrue(exception1.getClass().equals(UnknownIntervalException.class));
                UnknownIntervalException exception2 = (UnknownIntervalException) exception1;
                assertTrue(exception2.getCause().getMessage().equals("foo"));
            }
        }
    }

    @Test
    public void testSerializationWithString() throws IOException, ClassNotFoundException {
        final UnknownIntervalException exception = new UnknownIntervalException("foo");
        try (
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(bos)
        ) {
            oos.writeObject(exception);
            byte[] bytes = bos.toByteArray();
            try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
                 ObjectInputStream ois = new ObjectInputStream(bis)) {
                Object exception1 = ois.readObject();
                assertTrue(exception1.getClass().equals(UnknownIntervalException.class));
                UnknownIntervalException exception2 = (UnknownIntervalException) exception1;
                assertTrue(exception2.getMessage().equals("foo"));
            }
        }
    }
}
