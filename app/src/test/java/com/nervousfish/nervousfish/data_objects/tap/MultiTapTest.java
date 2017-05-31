package com.nervousfish.nervousfish.data_objects.tap;

import com.nervousfish.nervousfish.data_objects.Contact;
import com.nervousfish.nervousfish.data_objects.IKey;
import com.nervousfish.nervousfish.data_objects.MultiContact;
import com.nervousfish.nervousfish.data_objects.SimpleKey;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class MultiTapTest {

    @Test
    public void testInstantiate() {
        SingleTap tap = new SingleTap();
        List<SingleTap> list = new ArrayList<>();
        list.add(tap);
        MultiTap mTap = new MultiTap(list);
        assertNotNull(mTap);
        assertNotNull(mTap.getTaps());
    }

    @Test
    public void testInstantiateList() {
        SingleTap tap = new SingleTap();
        List<SingleTap> list = new ArrayList<>();
        list.add(tap);
        MultiTap mTap = new MultiTap(list);
        assertNotNull(mTap.getTaps());
    }

    @Test
    public void testCheckList() {
        SingleTap tap = new SingleTap();
        List<SingleTap> list = new ArrayList<>();
        list.add(tap);
        MultiTap mTap = new MultiTap(list);
        assertEquals(mTap.getTaps(), list);
    }

    @Test
    public void testSerialization() throws IOException, ClassNotFoundException {
        final List<SingleTap> taps = new ArrayList<>();
        final SingleTap tap1 = new SingleTap();
        final SingleTap tap2 = new SingleTap();
        taps.add(tap1);
        taps.add(tap2);
        final MultiTap multiTap = new MultiTap(taps);
        try (
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(bos)
        ) {
            oos.writeObject(multiTap);
            byte[] bytes = bos.toByteArray();
            try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
                 ObjectInputStream ois = new ObjectInputStream(bis)) {
                final MultiTap multiTap1 = (MultiTap) ois.readObject();
                final List<SingleTap> taps1 = multiTap1.getTaps();
                assertEquals(taps1.size(), 2);
            }
        }
    }
}
