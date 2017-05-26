package com.nervousfish.nervousfish.data_objects.tap;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * This class denotes a touch event on the screen.
 */
public abstract class ATapData implements Serializable {
    private static final long serialVersionUID = 6581521113517232361L;
    private final Timestamp timestamp;

    /**
     * Creates a new TapData object.
     */
    public ATapData() {
        this.timestamp = new Timestamp(System.currentTimeMillis());
    }

    /**
     * <b>Should only be used for testing purposes!</b>
     *
     * @param timestamp The timestamp of the tap data
     */
    public ATapData(final Timestamp timestamp) {
        this.timestamp = new Timestamp(timestamp.getTime());
    }

    /**
     * @return The time on which the ATapData was instantiated
     */
    public Timestamp getTimestamp() {
        // Defensive copying
        return new Timestamp(this.timestamp.getTime());
    }
}
