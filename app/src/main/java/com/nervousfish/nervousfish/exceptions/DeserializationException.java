package com.nervousfish.nervousfish.exceptions;

import com.nervousfish.nervousfish.ConstantKeywords;

import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;

/**
 * Thrown when there is an issue with deserization
 */
public final class DeserializationException extends RuntimeException {
    private static final long serialVersionUID = -1930759199728515311L;

    /**
     * Constructs a new DeserializationException that's thrown when there is an issue with deserization
     *
     * @param msg A string describing the event
     */
    public DeserializationException(final String msg) {
        super(msg);
    }

    /**
     * Serialize the created proxy instead of this instance.
     */
    private Object writeReplace() {
        return new SerializationProxy(this);
    }

    /**
     * Ensure that no instance of this class is created because it was present in the stream. A correct
     * stream should only contain instances of the proxy.
     */
    private void readObject(final ObjectInputStream stream) throws InvalidObjectException {
        throw new InvalidObjectException(ConstantKeywords.PROXY_REQUIRED);
    }

    /**
     * Represents the logical state of this class and copies the data from that class without
     * any consistency checking or defensive copying.
     */
    private static final class SerializationProxy implements Serializable {
        private static final long serialVersionUID = -1930759199728515311L;

        private final String message;
        private final Throwable throwable;

        /**
         * Constructs a new SerializationProxy
         *
         * @param exception The current instance of the proxy
         */
        SerializationProxy(final DeserializationException exception) {
            message = exception.getMessage();
            throwable = exception.getCause();
        }

        /**
         * Not to be called by the user - resolves a new object of this proxy
         *
         * @return The object resolved by this proxy
         */
        private Object readResolve() {
            return (DeserializationException) new DeserializationException(message).initCause(throwable);
        }
    }
}
