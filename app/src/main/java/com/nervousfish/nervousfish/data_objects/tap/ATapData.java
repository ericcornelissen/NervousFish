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
    ATapData() {
        this.timestamp = new Timestamp(System.currentTimeMillis());
    }

    /**
     * <b>Should only be used for testing purposes!</b>
     *
     * @param timestamp The timestamp of the tap data (NonNull)
     * @throws IllegalArgumentException If the provided {@code timestamp} is {@code null}
     */
    ATapData(final Timestamp timestamp) {
        if (timestamp == null) {
            throw new IllegalArgumentException("Null is not allowed");
        }
        this.timestamp = new Timestamp(timestamp.getTime());
    }

    /**
     * @return The time on which the ATapData was instantiated
     */
    public final Timestamp getTimestamp() {
        // Defensive copying
        return new Timestamp(this.timestamp.getTime());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || this.getClass() != obj.getClass()) {
            return false;
        }

        final ATapData other = (ATapData) obj;
        return this.timestamp.equals(other.getTimestamp());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return this.timestamp.hashCode();
    }
}
