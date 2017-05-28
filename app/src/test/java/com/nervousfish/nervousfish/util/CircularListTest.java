package com.nervousfish.nervousfish.util;

import org.junit.Test;

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
}