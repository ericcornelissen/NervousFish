package com.nervousfish.nervousfish.exceptions;

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
        final Exception exception = new Exception();
        UnknownIntervalException unknownIntervalException = new UnknownIntervalException("foo");
        assertTrue(unknownIntervalException.getMessage().equals("foo"));
    }

    @Test
    public void testSerialization() throws IOException, ClassNotFoundException {
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
            }
        }
    }
}
