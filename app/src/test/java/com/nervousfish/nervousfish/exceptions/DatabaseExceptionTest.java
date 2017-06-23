package com.nervousfish.nervousfish.exceptions;

import com.nervousfish.nervousfish.TestException;
import com.nervousfish.nervousfish.exceptions.DatabaseException;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import static org.junit.Assert.assertTrue;

public class DatabaseExceptionTest {
    @Test(expected = DatabaseException.class)
    public void testStringConstructor() {
        throw new DatabaseException("foo");
    }

    @Test(expected = DatabaseException.class)
    public void testThrowableConstructor() {
        throw new DatabaseException(new IOException("foo"));
    }

    @Test
    public void testSerializationWithException() throws IOException, ClassNotFoundException {
        final DatabaseException exception = new DatabaseException(new TestException("foo"));
        try (
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(bos)
        ) {
            oos.writeObject(exception);
            byte[] bytes = bos.toByteArray();
            try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
                 ObjectInputStream ois = new ObjectInputStream(bis)) {
                Object exception1 = ois.readObject();
                assertTrue(exception1.getClass().equals(DatabaseException.class));
                DatabaseException exception2 = (DatabaseException) exception1;
                assertTrue(exception2.getCause().getMessage().equals("foo"));
            }
        }
    }

    @Test
    public void testSerializationWithString() throws IOException, ClassNotFoundException {
        final DatabaseException exception = new DatabaseException("foo");
        try (
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(bos)
        ) {
            oos.writeObject(exception);
            byte[] bytes = bos.toByteArray();
            try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
                 ObjectInputStream ois = new ObjectInputStream(bis)) {
                Object exception1 = ois.readObject();
                assertTrue(exception1.getClass().equals(DatabaseException.class));
                DatabaseException exception2 = (DatabaseException) exception1;
                assertTrue(exception2.getMessage().equals("foo"));
            }
        }
    }
}

