package com.nervousfish.nervousfish.exceptions;

import com.nervousfish.nervousfish.TestException;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import static org.junit.Assert.assertTrue;

public class UnsupportedKeyTypeExceptionTest {
    @Test
    public void testStringConstructor() {
        UnsupportedKeyTypeException UnsupportedKeyTypeException = new UnsupportedKeyTypeException("foo");
        assertTrue(UnsupportedKeyTypeException.getMessage().equals("foo"));
    }

    @Test
    public void testThrowableConstructor() {
        final Throwable throwable = new Throwable();
        UnsupportedKeyTypeException UnsupportedKeyTypeException = new UnsupportedKeyTypeException(throwable);
        assertTrue(UnsupportedKeyTypeException.getCause().equals(throwable));
    }

    @Test
    public void testSerializationWithException() throws IOException, ClassNotFoundException {
        final UnsupportedKeyTypeException exception = new UnsupportedKeyTypeException(new TestException("foo"));
        try (
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(bos)
        ) {
            oos.writeObject(exception);
            byte[] bytes = bos.toByteArray();
            try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
                 ObjectInputStream ois = new ObjectInputStream(bis)) {
                Object exception1 = ois.readObject();
                assertTrue(exception1.getClass().equals(UnsupportedKeyTypeException.class));
                UnsupportedKeyTypeException exception2 = (UnsupportedKeyTypeException) exception1;
                assertTrue(exception2.getCause().getMessage().equals("foo"));
            }
        }
    }

    @Test
    public void testSerializationWithString() throws IOException, ClassNotFoundException {
        final UnsupportedKeyTypeException exception = new UnsupportedKeyTypeException("foo");
        try (
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(bos)
        ) {
            oos.writeObject(exception);
            byte[] bytes = bos.toByteArray();
            try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
                 ObjectInputStream ois = new ObjectInputStream(bis)) {
                Object exception1 = ois.readObject();
                assertTrue(exception1.getClass().equals(UnsupportedKeyTypeException.class));
                UnsupportedKeyTypeException exception2 = (UnsupportedKeyTypeException) exception1;
                assertTrue(exception2.getMessage().equals("foo"));
            }
        }
    }
}