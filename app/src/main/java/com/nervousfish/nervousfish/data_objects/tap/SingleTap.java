package com.nervousfish.nervousfish.data_objects.tap;

import java.sql.Timestamp;

/**
 * This class contains the bare minimum functionality of a tap event.
 */
public final class SingleTap extends AbstractTapData {
    /**
     * {@inheritDoc}
     */
    public SingleTap() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    public SingleTap(final Timestamp timestamp) {
        super(timestamp);
    }
}
