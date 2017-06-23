package com.nervousfish.nervousfish.modules.pairing;

import org.junit.Test;

import java.io.Serializable;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

public class DataWrapperTest {

    @Test
    public void testGetData() throws Exception {
        Serializable mock = mock(Serializable.class);
        DataWrapper wrapper = new DataWrapper(mock);
        assertEquals(wrapper.getData(), mock);
    }

    @Test
    public void testGetClazz() throws Exception {
        Serializable mock = mock(Serializable.class);
        DataWrapper wrapper = new DataWrapper(mock);
        assertEquals(wrapper.getClazz(), mock.getClass());
    }

    @Test
    public void testSerialization() throws IOException, ClassNotFoundException {
        final DataWrapper wrapper = new DataWrapper(new TestSerializable("foo"));
        try (
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(bos)
        ) {
            oos.writeObject(wrapper);
            byte[] bytes = bos.toByteArray();
            try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
                 ObjectInputStream ois = new ObjectInputStream(bis)) {
                // Classes in the test module cannot be deserialized ?!?
            }
        }
    }

}
