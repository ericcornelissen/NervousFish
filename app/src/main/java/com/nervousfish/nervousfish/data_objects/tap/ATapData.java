package com.nervousfish.nervousfish.data_objects.tap;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * This class denotes a touch event on the screen.
 */
abstract class ATapData implements Serializable {
    private Timestamp timestamp;

    /**
     * Creates a new TapData object.
     */
    public ATapData() {
        this.timestamp = new Timestamp(System.currentTimeMillis());
    }

    /**
     * @return The time on which the ATapData was instantiated
     */
    Timestamp getTimestamp() {
        return this.timestamp;
    }
}
