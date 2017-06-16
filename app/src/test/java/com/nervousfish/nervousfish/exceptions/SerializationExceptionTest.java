package com.nervousfish.nervousfish.exceptions;


import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import static org.junit.Assert.assertTrue;

public class SerializationExceptionTest {
    @Test
    public void testStringConstructor() {
        SerializationException serializationException = new SerializationException("foo");
        assertTrue(serializationException.getMessage().equals("foo"));
    }

    @Test
    public void testThrowableConstructor() {
        final Throwable throwable = new Throwable();
        SerializationException serializationException = new SerializationException(throwable);
        assertTrue(serializationException.getCause().equals(throwable));
    }

    @Test
    public void testSerialization() throws IOException, ClassNotFoundException {
        final SerializationException exception = new SerializationException(new Exception());
        try (
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(bos)
                ) {
            oos.writeObject(exception);
            byte[] bytes = bos.toByteArray();
            try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
                 ObjectInputStream ois = new ObjectInputStream(bis)) {
                Object exception1 = ois.readObject();
                assertTrue(exception1.getClass().equals(SerializationException.class));
            }
        }
    }
}
