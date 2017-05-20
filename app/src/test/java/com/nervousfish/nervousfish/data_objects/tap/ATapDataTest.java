package com.nervousfish.nervousfish.data_objects.tap;

import org.junit.Test;

import java.io.Serializable;
import java.sql.Timestamp;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

<<<<<<< HEAD:app/src/test/java/com/nervousfish/nervousfish/data_objects/tap/AbstractTapDataTest.java
public class AbstractTapDataTest implements Serializable {

    private static final long serialVersionUID = -8153267197382895289L;

    private static class Tmp extends AbstractTapData {
        private static final long serialVersionUID = -7179812982754955801L;
=======
public class ATapDataTest {

    class tmp extends ATapData {
>>>>>>> develop:app/src/test/java/com/nervousfish/nervousfish/data_objects/tap/ATapDataTest.java
    }

    @Test
    public void testTimestamp() {
<<<<<<< HEAD:app/src/test/java/com/nervousfish/nervousfish/data_objects/tap/AbstractTapDataTest.java
        AbstractTapData tap = new Tmp();
=======
        ATapData tap = new tmp();
>>>>>>> develop:app/src/test/java/com/nervousfish/nervousfish/data_objects/tap/ATapDataTest.java
        Timestamp stamp = tap.getTimestamp();
        assertTrue(System.currentTimeMillis() >= stamp.getTime());
        assertTrue(System.currentTimeMillis() < stamp.getTime() + 100);
    }

}
