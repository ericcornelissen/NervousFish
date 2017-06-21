package com.nervousfish.nervousfish.exceptions;


import com.nervousfish.nervousfish.TestException;
import com.nervousfish.nervousfish.modules.database.DatabaseException;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import static org.junit.Assert.assertTrue;

public class NoBluetoothExceptionTest {
    @Test
    public void testStringConstructor() {
        NoBluetoothException noBluetoothException = new NoBluetoothException("foo");
        assertTrue(noBluetoothException.getMessage().equals("foo"));
    }

    @Test
    public void testThrowableConstructor() {
        final Throwable throwable = new Throwable();
        NoBluetoothException noBluetoothException = new NoBluetoothException(throwable);
        assertTrue(noBluetoothException.getCause().equals(throwable));
    }

    @Test
    public void testSerializationWithException() throws IOException, ClassNotFoundException {
        final NoBluetoothException exception = new NoBluetoothException(new TestException("foo"));
        try (
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(bos)
        ) {
            oos.writeObject(exception);
            byte[] bytes = bos.toByteArray();
            try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
                 ObjectInputStream ois = new ObjectInputStream(bis)) {
                Object exception1 = ois.readObject();
                assertTrue(exception1.getClass().equals(NoBluetoothException.class));
                NoBluetoothException exception2 = (NoBluetoothException) exception1;
                assertTrue(exception2.getCause().getMessage().equals("foo"));
            }
        }
    }

    @Test
    public void testSerializationWithString() throws IOException, ClassNotFoundException {
        final NoBluetoothException exception = new NoBluetoothException("foo");
        try (
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(bos)
        ) {
            oos.writeObject(exception);
            byte[] bytes = bos.toByteArray();
            try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
                 ObjectInputStream ois = new ObjectInputStream(bis)) {
                Object exception1 = ois.readObject();
                assertTrue(exception1.getClass().equals(NoBluetoothException.class));
                NoBluetoothException exception2 = (NoBluetoothException) exception1;
                assertTrue(exception2.getMessage().equals("foo"));
            }
        }
    }
}
