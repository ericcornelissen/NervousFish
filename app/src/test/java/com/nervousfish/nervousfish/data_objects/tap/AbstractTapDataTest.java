package com.nervousfish.nervousfish.data_objects.tap;

import org.junit.Test;

import java.io.Serializable;
import java.sql.Timestamp;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class AbstractTapDataTest implements Serializable {

    private static final long serialVersionUID = -8153267197382895289L;

    private static class Tmp extends AbstractTapData {
        private static final long serialVersionUID = -7179812982754955801L;
    }

    @Test
    public void testTimestamp() {
        AbstractTapData tap = new Tmp();
        Timestamp stamp = tap.getTimestamp();
        assertTrue(System.currentTimeMillis() >= stamp.getTime());
        assertTrue(System.currentTimeMillis() < stamp.getTime() + 100);
    }

}
