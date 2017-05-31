package com.nervousfish.nervousfish.data_objects.tap;

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
import static org.junit.Assert.assertTrue;

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
    public void testHashCodeNotNull() {
        List<SingleTap> taps = new ArrayList<>();
        taps.add(new SingleTap());
        MultiTap multiTap = new MultiTap(taps);
        assertNotNull(multiTap.hashCode());
    }

    @Test
    public void testSerialization() throws IOException, ClassNotFoundException {
        List<SingleTap> taps = new ArrayList<>();
        taps.add(new SingleTap());
        final MultiTap multiTap = new MultiTap(taps);
        try (
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(bos)
        ) {
            oos.writeObject(multiTap);
            byte[] bytes = bos.toByteArray();
            try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
                 ObjectInputStream ois = new ObjectInputStream(bis)) {
                MultiTap multiTap1 = (MultiTap) ois.readObject();
                System.out.println(multiTap.getTaps());
                System.out.println(multiTap1.getTaps());
                assertTrue(multiTap1.getTaps().equals(taps));
            }
        }
    }

}
