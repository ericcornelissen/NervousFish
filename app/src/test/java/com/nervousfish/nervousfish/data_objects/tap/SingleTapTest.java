package com.nervousfish.nervousfish.data_objects.tap;

import com.nervousfish.nervousfish.data_objects.Contact;
import com.nervousfish.nervousfish.data_objects.IKey;

import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

public class SingleTapTest {

    @Test
    public void testInstantiate() {
        SingleTap tap = new SingleTap();
        assertNotNull(tap);
    }

    @Test
    public void testTimestamp() {
        SingleTap tap = new SingleTap();
        Timestamp stamp = tap.getTimestamp();
        assertTrue(System.currentTimeMillis() >= stamp.getTime());
        assertTrue(System.currentTimeMillis() < stamp.getTime() + 100);
    }

    @Test
    public void testSerialization() throws IOException, ClassNotFoundException {
        SingleTap singleTap = new SingleTap();
        try (
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(bos)
        ) {
            oos.writeObject(singleTap);
            byte[] bytes = bos.toByteArray();
            try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
                 ObjectInputStream ois = new ObjectInputStream(bis)) {
                Object object = ois.readObject();
                assertTrue(object.getClass().equals(SingleTap.class));
            }
        }
    }
}
