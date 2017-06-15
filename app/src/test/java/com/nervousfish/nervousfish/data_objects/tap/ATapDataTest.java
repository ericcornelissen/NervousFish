package com.nervousfish.nervousfish.data_objects.tap;

import org.junit.Test;

import java.io.Serializable;
import java.sql.Timestamp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class ATapDataTest implements Serializable {
    // Must be done because the outer class of a serializable inner class must also be serializable
    private static final long serialVersionUID = -2144496846163197196L;

    private static class TestTapData extends ATapData {
        private static final long serialVersionUID = 2815002793347502764L;

        TestTapData() {
            super();
        }

        TestTapData(final Timestamp timestamp) {
            super(timestamp);
        }
    }

    @Test (expected = IllegalArgumentException.class)
    public void testConstructorExceptionNull() {
        ATapData contactA = new TestTapData(null);
    }

    @Test
    public void testTimestamp() {
        ATapData tap = new TestTapData();
        Timestamp stamp = tap.getTimestamp();
        assertTrue(System.currentTimeMillis() >= stamp.getTime());
        assertTrue(System.currentTimeMillis() < stamp.getTime() + 100);
    }

    @Test
    public void testEqualsWorksWithNull() {
        ATapData contact = new TestTapData();
        assertFalse(contact.equals(null));
    }

    @Test
    public void testEqualsWorksWithSameObject() {
        ATapData contact = new TestTapData();
        assertTrue(contact.equals(contact));
    }

    @Test
    public void testEqualsWithDifferentObject() {
        ATapData contactA = new TestTapData();
        assertFalse(contactA.equals(new ATapDataTest()));
    }

    @Test
    public void testEqualsNonNullTimestamps() {
        ATapData contactA = new TestTapData(new Timestamp(5));
        ATapData contactB = new TestTapData(new Timestamp(5));
        assertTrue(contactA.equals(contactB));
    }

    @Test
    public void testHashCodeNotNull() {
        ATapData contact = new TestTapData();
        assertNotNull(contact.hashCode());
    }
}
