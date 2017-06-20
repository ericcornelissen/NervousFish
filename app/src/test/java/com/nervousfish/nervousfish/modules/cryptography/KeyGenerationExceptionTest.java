package com.nervousfish.nervousfish.modules.cryptography;

import com.nervousfish.nervousfish.TestException;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import static org.junit.Assert.assertTrue;

public class KeyGenerationExceptionTest {
    @Test
    public void testExceptionConstructor() {
        final Exception exception = new Exception();
        KeyGenerationException keyGenerationException = new KeyGenerationException(exception);
        assertTrue(keyGenerationException.getCause().equals(exception));
    }

    @Test
    public void testThrowableConstructor() {
        final Throwable throwable = new Throwable();
        KeyGenerationException keyGenerationException = new KeyGenerationException(throwable);
        assertTrue(keyGenerationException.getCause().equals(throwable));
    }

    @Test
    public void testSerializationWithException() throws IOException, ClassNotFoundException {
        final KeyGenerationException exception = new KeyGenerationException(new TestException("foo"));
        try (
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(bos)
        ) {
            oos.writeObject(exception);
            byte[] bytes = bos.toByteArray();
            try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
                 ObjectInputStream ois = new ObjectInputStream(bis)) {
                Object exception1 = ois.readObject();
                assertTrue(exception1.getClass().equals(KeyGenerationException.class));
                KeyGenerationException exception2 = (KeyGenerationException) exception1;
                assertTrue(exception2.getCause().getMessage().equals("foo"));
            }
        }
    }

    @Test
    public void testSerializationWithString() throws IOException, ClassNotFoundException {
        final KeyGenerationException exception = new KeyGenerationException("foo");
        try (
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(bos)
        ) {
            oos.writeObject(exception);
            byte[] bytes = bos.toByteArray();
            try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
                 ObjectInputStream ois = new ObjectInputStream(bis)) {
                Object exception1 = ois.readObject();
                assertTrue(exception1.getClass().equals(KeyGenerationException.class));
                KeyGenerationException exception2 = (KeyGenerationException) exception1;
                assertTrue(exception2.getMessage().equals("foo"));
            }
        }
    }
}
