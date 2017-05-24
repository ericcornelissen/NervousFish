package com.nervousfish.nervousfish.data_objects.tap;

import org.junit.Test;

import java.io.Serializable;
import java.sql.Timestamp;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class ATapDataTest implements Serializable {
    // Must be done because the outer class of a serializable inner class must also be serializable
    private static final long serialVersionUID = -2144496846163197196L;

    private static class Tmp extends ATapData {
        private static final long serialVersionUID = 2815002793347502764L;
    }

    @Test
    public void testTimestamp() {
        ATapData tap = new Tmp();
        Timestamp stamp = tap.getTimestamp();
        assertTrue(System.currentTimeMillis() >= stamp.getTime());
        assertTrue(System.currentTimeMillis() < stamp.getTime() + 100);
    }

}
