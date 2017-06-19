package com.nervousfish.nervousfish.exceptions;

import java.io.IOException;

/**
 * Thrown when there's an issue while encrypting.
 */
public class EncryptionException extends RuntimeException {

    private static final long serialVersionUID = -1930461199728515311L;

    /**
     * Constructs a new EncryptionException that's thrown when there is an issue while
     * encrypting / decrypting.
     *
     * @param msg A string describing the event
     */
    public EncryptionException(final String msg) {
        super(msg);
    }

    /**
     * Constructs a new EncryptionException that's thrown when there is an issue while
     * encrypting / decrypting
     *
     * @param throwable The exception that occurred the layer above
     */
    public EncryptionException(final Throwable throwable) {
        super(throwable);
    }

    /**
     * Constructs a new EncryptionException that's thrown when there is an issue while
     * encrypting.
     *
     * @param msg A string describing the event
     * @param e   The original exception.
     */
    public EncryptionException(final String msg, final Exception e) {
        super(msg, e);
    }
}
