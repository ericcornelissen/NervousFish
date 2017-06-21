package com.nervousfish.nervousfish.modules.cryptography;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Thrown when an error occurred while generating a new RSA key
 */
final class KeyGenerationException extends RuntimeException {
    private static final long serialVersionUID = -2973457213678228010L;

    /**
     * Constructs a new KeyGenerationException that's thrown when an error occurred while generating a new RSA key
     *
     * @param msg A message describing the event that happened
     */
    KeyGenerationException(final String msg) {
        super(msg);
    }

    /**
     * Constructs a new KeyGenerationException that's thrown when an error occurred while generating a new RSA key
     *
     * @param e The throwable that occurred that caused this throwable to happen
     */
    KeyGenerationException(final Throwable e) {
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
