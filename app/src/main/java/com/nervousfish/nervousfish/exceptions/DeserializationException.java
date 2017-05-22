package com.nervousfish.nervousfish.exceptions;

/**
 * Thrown when there is a issue with deserization
 */
public class DeserializationException extends RuntimeException {
    /**
     * {@inheritDoc}
     */
    public DeserializationException(final String msg) {
        super(msg);
    }
}
