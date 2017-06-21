package com.nervousfish.nervousfish.exceptions;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Simple {@link RuntimeException} that is used when an interval is neither short nor long
 */
public class UnknownIntervalException extends RuntimeException {

    private static final long serialVersionUID = -7030163248191198652L;

    /**
     * Constructor for a new UnknownIntervalException with a message.
     *
     * @param msg A string describing the exception
     */
    public UnknownIntervalException(final String msg) {
        super(msg);
    }

    /**
     * Constructs a new UnknownIntervalException with an exception (a throwable)
     *
     * @param throwable The exception that occurred the layer above
     */
    public UnknownIntervalException(final Throwable throwable) {
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
