package com.nervousfish.nervousfish.exceptions;

import com.nervousfish.nervousfish.ConstantKeywords;

import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;

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
     * Serialize the created proxy instead of the {@link DeserializationException} instance.
     */
    private Object writeReplace() {
        return new SerializationProxy(this);
    }

    /**
     * Ensure no instance of {@link DeserializationException} is created when present in the stream.
     */
    private void readObject(final ObjectInputStream stream) throws InvalidObjectException {
        throw new InvalidObjectException(ConstantKeywords.PROXY_REQUIRED);
    }

    /**
     * A proxy representing the logical state of {@link DeserializationException}. Copies the data
     * from that class without any consistency checking or defensive copying.
     */
    private static final class SerializationProxy implements Serializable {

        private static final long serialVersionUID = -1930759199728943711L;

        private final String message;
        private final Throwable throwable;

        /**
         * Constructs a new SerializationProxy
         *
         * @param exception The current instance of the proxy
         */
        SerializationProxy(final DeserializationException exception) {
            this.message = exception.getMessage();
            this.throwable = exception.getCause();
        }

        /**
         * Not to be called by the user - resolves a new object of this proxy
         *
         * @return The object resolved by this proxy
         */
        private Object readResolve() {
            return new DeserializationException(this.message).initCause(this.throwable);
        }

    }
}
