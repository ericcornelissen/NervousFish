package com.nervousfish.nervousfish.data_objects.tap;

import org.junit.Test;

import java.sql.Timestamp;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class MultiTapTest {

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

}
