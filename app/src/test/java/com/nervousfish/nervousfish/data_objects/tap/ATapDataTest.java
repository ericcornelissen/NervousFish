package com.nervousfish.nervousfish.data_objects.tap;

import org.junit.Test;

import java.sql.Timestamp;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class ATapDataTest {

    class tmp extends ATapData {
    }

    @Test
    public void testTimestamp() {
        ATapData tap = new tmp();
        Timestamp stamp = tap.getTimestamp();
        assertTrue(System.currentTimeMillis() >= stamp.getTime());
        assertTrue(System.currentTimeMillis() < stamp.getTime() + 100);
    }

}
