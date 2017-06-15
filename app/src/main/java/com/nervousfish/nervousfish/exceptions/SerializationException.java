package com.nervousfish.nervousfish.exceptions;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Thrown when there is an issue with serialization.
 */
public final class SerializationException extends RuntimeException {

    private static final long serialVersionUID = 1698338992687134510L;

    /**
     * Constructs a new SerializationException that's thrown when there is an issue with
     * deserialization.
     *
     * @param msg A string describing the event
     */
    public SerializationException(final String msg) {
        super(msg);
    }

    /**
     * Constructs a new SerializationException that's thrown when there is an issue with
     * deserialization.
     *
     * @param throwable The exception that occurred the layer above
     */
    public SerializationException(final Throwable throwable) {
        super(throwable);
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
