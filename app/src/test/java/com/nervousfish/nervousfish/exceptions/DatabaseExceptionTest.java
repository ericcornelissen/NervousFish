package com.nervousfish.nervousfish.exceptions;

import com.nervousfish.nervousfish.modules.database.DatabaseException;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import static org.junit.Assert.assertTrue;

public class DatabaseExceptionTest {
    @Test
    public void testExceptionConstructor() {
        final Exception exception = new Exception();
        DatabaseException databaseException = new DatabaseException(exception);
        assertTrue(databaseException.getCause().equals(exception));
    }

    @Test
    public void testThrowableConstructor() {
        final Throwable throwable = new Throwable();
        DatabaseException databaseException = new DatabaseException(throwable);
        assertTrue(databaseException.getCause().equals(throwable));
    }

    @Test
    public void testSerialization() throws IOException, ClassNotFoundException {
        final DatabaseException exception = new DatabaseException(new Exception());
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
            }
        }
    }
}
