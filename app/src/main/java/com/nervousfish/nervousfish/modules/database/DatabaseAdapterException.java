package com.nervousfish.nervousfish.modules.database;

/**
 * Thrown when there's an issue whith the {@link GsonDatabaseAdapter}.
 */
public class DatabaseAdapterException extends RuntimeException {

    /**
     * Constructs a new DatabaseAdapterException that's thrown when there is an issue with the
     * {@link GsonDatabaseAdapter}.
     *
     * @param msg A string describing the event
     */
    public DatabaseAdapterException(final String msg) {
        super(msg);
    }

    /**
     * Constructs a new DatabaseAdapterException that's thrown when there is an issue with the
     * {@link GsonDatabaseAdapter}.
     *
     * @param msg A string describing the event.
     * @param e The original Exception.
     *
     */
    public DatabaseAdapterException(final String msg, final Exception e) {
        super(msg, e);
    }
}
