package com.nervousfish.nervousfish.modules.database;

import com.nervousfish.nervousfish.ConstantKeywords;

import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;

/**
 * Thrown when there is an issue with the database
 */
public final class DatabaseException extends RuntimeException {

    private static final long serialVersionUID = -5464890114687852303L;

    /**
     * Constructs a new Database Exception, thrown when there is an issue with the database
     *
     * @param msg A message describing the event that happened
     */
    public DatabaseException(final String msg) {
        super(msg);
    }

    /**
     * Serialize the created proxy instead of the {@link DatabaseException} instance.
     */
    private Object writeReplace() {
        return new SerializationProxy(this);
    }

    /**
     * Ensure no instance of {@link DatabaseException} is created when present in the stream.
     */
    private void readObject(final ObjectInputStream stream) throws InvalidObjectException {
        throw new InvalidObjectException(ConstantKeywords.PROXY_REQUIRED);
    }

    /**
     * A proxy representing the logical state of {@link DatabaseException}. Copies the data
     * from that class without any consistency checking or defensive copying.
     */
    private static final class SerializationProxy implements Serializable {

        private static final long serialVersionUID = -5464890114687852303L;

        private final String message;
        private final Throwable throwable;

        /**
         * Constructs a new SerializationProxy
         *
         * @param exception The current instance of the proxy
         */
        SerializationProxy(final DatabaseException exception) {
            this.message = exception.getMessage();
            this.throwable = exception.getCause();
        }

        /**
         * Not to be called by the user - resolves a new object of this proxy
         *
         * @return The object resolved by this proxy
         */
        private Object readResolve() {
            return new DatabaseException(this.message).initCause(this.throwable);
        }

    }

}
