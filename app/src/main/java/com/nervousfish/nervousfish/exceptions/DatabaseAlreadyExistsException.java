package com.nervousfish.nervousfish.exceptions;

/**
 * Thrown when there is an issue with deserialization.
 */
public final class DatabaseAlreadyExistsException extends RuntimeException {

    private static final long serialVersionUID = -1930461199728515311L;

    /**
     * Constructs a new DatabaseAlreadyExistsException that's thrown when there is an issue with
     * creating a new database.
     *
     * @param msg A string describing the event
     */
    public DatabaseAlreadyExistsException(final String msg) {
        super(msg);
    }
}
