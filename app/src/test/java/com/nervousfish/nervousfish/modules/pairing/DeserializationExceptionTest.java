package com.nervousfish.nervousfish.modules.pairing;

import com.nervousfish.nervousfish.modules.cryptography.KeyGenerationException;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import static org.junit.Assert.assertTrue;

public class DeserializationExceptionTest {
    @Test
    public void testExceptionConstructorString() {
        DeserializationException keyGenerationException = new DeserializationException("foo");
        assertTrue(keyGenerationException.getMessage().equals("foo"));
    }
    @Test
    public void testExceptionConstructorThrowable() {
        final Exception exception = new Exception();
        DeserializationException keyGenerationException = new DeserializationException(exception);
        assertTrue(keyGenerationException.getCause().equals(exception));
    }

    @Test
    public void testThrowableConstructor() {
        final Throwable throwable = new Throwable();
        DeserializationException keyGenerationException = new DeserializationException(throwable);
        assertTrue(keyGenerationException.getCause().equals(throwable));
    }

    @Test
    public void testSerialization() throws IOException, ClassNotFoundException {
        final DeserializationException exception = new DeserializationException(new Exception());
        try (
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(bos)
                ) {
            oos.writeObject(exception);
            byte[] bytes = bos.toByteArray();
            try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
                 ObjectInputStream ois = new ObjectInputStream(bis)) {
                Object exception1 = ois.readObject();
                assertTrue(exception1.getClass().equals(DeserializationException.class));
            }
        }
    }
}
