package com.nervousfish.nervousfish.data_objects.tap;

import java.sql.Timestamp;

/**
 * This class contains the bare minimum functionality of a tap event.
 */
public final class SingleTap extends ATapData {
    /**
     * Constructs a new tap data object that denotes a single tap event.
     * The time on which the tap event happened is assumed to be the moment that this
     * constructor is called.
     */
    public SingleTap() {
        super();
    }

    /**
     * Constructs a new tap data object that denotes a single tap event.
     * @param timestamp The time on which the tap event happened
     */
    public SingleTap(final Timestamp timestamp) {
        super(timestamp);
    }
}
