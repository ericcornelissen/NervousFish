package com.nervousfish.nervousfish.modules.database;

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
     * Constructs a new Datbase Exception, thrown when there is an issue with the database
     *
     * @param throwable The root of th exception
     */
    public DatabaseException(final Throwable throwable) {
        super(throwable);
    }

}
