package com.nervousfish.nervousfish.exceptions;

/**
 * Simple {@link RuntimeException} that is used when a socket could not be created from the streams
 */
public class SocketCreationException extends RuntimeException {

    private static final long serialVersionUID = -7030163248191198652L;

    /**
     * Constructor for a new SocketCreationException with a message.
     *
     * @param msg A string describing the exception
     */
    public SocketCreationException(final String msg) {
        super(msg);
    }

    /**
     * Constructor for a new SocketCreationException that wraps another Throwable
     *
     * @param throwable The exception that caused this exception to be thrown
     */
    public SocketCreationException(final Throwable throwable) {
        super(throwable);
    }
}
