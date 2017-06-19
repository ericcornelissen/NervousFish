package com.nervousfish.nervousfish;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Created to be able to inject Serializable things (in this case an Exception)
 * in classes without having to use classes from the app that may be deleted
 * in the future.
 */
public final class TestException extends RuntimeException {

    private static final long serialVersionUID = -5464890114687852303L;

    /**
     * Constructs a new TestException with a string
     * @param msg A random message
     */
    public TestException(final String msg) {
        super(msg);
    }

    /**
     * Constructs a new TestException with an exception
     * @param throwable A random exception = a random throwable
     */
    public TestException(final Throwable throwable) {
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