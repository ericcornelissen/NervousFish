package com.nervousfish.nervousfish.exceptions;

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

}
