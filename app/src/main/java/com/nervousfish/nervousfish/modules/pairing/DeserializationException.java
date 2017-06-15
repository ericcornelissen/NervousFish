package com.nervousfish.nervousfish.modules.pairing;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Thrown when there is an issue with deserialization.
 */
public final class DeserializationException extends RuntimeException {

    private static final long serialVersionUID = -1930461199728515311L;

    /**
     * Constructs a new DeserializationException that's thrown when there is an issue with
     * deserialization.
     *
     * @param msg A string describing the event
     */
    public DeserializationException(final String msg) {
        super(msg);
    }

    /**
     * Constructs a new DeserializationException that's thrown when there is an issue with
     * deserialization.
     * @param e The exception that occurred that caused this throwable to happen
     */
    public DeserializationException(final Exception e) {
        super(e);
    }

    /**
     * Deserialize the instance using readObject to ensure invariants and security.
     *
     * @param stream The serialized object to be deserialized
     */
    private void readObject(final ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.ensureClassInvariant();
    }

    /**
     * Used to improve performance / efficiency
     *
     * @param stream The stream to which this object should be serialized to
     */
    private void writeObject(final ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
    }

    /**
     * Ensure that the instance meets its class invariant
     */
    private void ensureClassInvariant() {
        // No checks to perform
    }
}
