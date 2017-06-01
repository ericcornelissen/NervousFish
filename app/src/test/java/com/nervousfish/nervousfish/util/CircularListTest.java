package com.nervousfish.nervousfish.util;

import com.nervousfish.nervousfish.data_objects.IKey;
import com.nervousfish.nervousfish.data_objects.SimpleKey;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

import static org.junit.Assert.*;

public class CircularListTest {
    @Test(expected = IllegalArgumentException.class)
    public void testNoElements() throws Exception {
        new CircularList(0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNegativeElements() throws Exception {
        new CircularList(-1);
    }

    @Test
    public void testSingleElement() throws Exception {
        CircularList<Integer> list = new CircularList<>(1);
        list.add(5);
        assertTrue(list.size() == 1);
        assertTrue(list.get(0) == 5);
    }

    @Test
    public void testElementOutOfBounds() throws Exception {
        CircularList<Integer> list = new CircularList<>(1);
        list.add(5);
        assertTrue(list.get(100) == 5);
    }

    @Test
    public void testTripleElement() throws Exception {
        CircularList<Integer> list = new CircularList<>(3);
        list.add(5);
        list.add(10);
        list.add(15);
        assertTrue(list.size() == 3);
        assertTrue(list.get(0) == 5);
        assertTrue(list.get(1) == 10);
        assertTrue(list.get(2) == 15);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetNegative() throws Exception {
        CircularList<Integer> list = new CircularList<>(1);
        list.get(-1);
    }

    @Test
    public void testGetElements() throws Exception {
        CircularList<Integer> list = new CircularList<>(3);
        list.add(5);
        list.add(10);
        list.add(15);
        final List<Integer> elements = list.getElements();
        assertEquals(elements.get(0).intValue(), 5);
        assertEquals(elements.get(1).intValue(), 10);
        assertEquals(elements.get(2).intValue(), 15);
    }

    @Test
    public void testEqualsHash() {
        CircularList<Integer> list1 = new CircularList<>(10);
        list1.add(5);
        CircularList<Integer> list2 = new CircularList<>(10);
        list1.add(10);
        assertFalse(list1.equals(list2));
        assertFalse(list1.hashCode() == list2.hashCode());
        CircularList<Integer> list3 = new CircularList<>(10);
        list1.add(12);
        CircularList<Integer> list4 = new CircularList<>(10);
        list1.add(12);
        assertTrue(list3.equals(list4));
        assertTrue(list3.hashCode() == list4.hashCode());
    }

    @Test
    public void testSerialization() throws IOException, ClassNotFoundException {
        final CircularList<Integer> circularList = new CircularList<>(5);
        circularList.add(1);
        circularList.add(42);
        try (
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(bos)
        ) {
            oos.writeObject(circularList);
            byte[] bytes = bos.toByteArray();
            try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
                 ObjectInputStream ois = new ObjectInputStream(bis)) {
                CircularList<Integer> circularList1 = (CircularList<Integer>) ois.readObject();
                assertTrue(circularList1.get(0) == 1);
                assertTrue(circularList1.get(1) == 42);
            }
        }
    }
}