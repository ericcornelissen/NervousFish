package com.nervousfish.nervousfish.exceptions;

/**
 * Thrown when there is a issue with the database
 */
public class DatabaseException extends RuntimeException {
    /**
     * {@inheritDoc}
     */
    public DatabaseException(final String msg) {
        super(msg);
    }
}
