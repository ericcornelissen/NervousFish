package com.nervousfish.nervousfish.modules.cryptography;

import com.nervousfish.nervousfish.ConstantKeywords;

import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;

/**
 * Thrown when an error occurred while generating a new RSA key
 */
public final class KeyGenerationException extends RuntimeException {
    private static final long serialVersionUID = -2973457213678228010L;

    /**
     * Constructs a new KeyGenerationException that's thrown when an error occurred while generating a new RSA key
     * @param e The throwable that occurred that caused this throwable to happen
     */
    KeyGenerationException(final Throwable e) {
        super(e);
    }

    /**
     * Constructs a new KeyGenerationException that's thrown when an error occurred while generating a new RSA key
     * @param e The exception that occurred that caused this throwable to happen
     */
    KeyGenerationException(final Exception e) {
        super(e);
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
        private static final long serialVersionUID = -2973457213678228010L;
        private final Throwable throwable;

        /**
         * Constructs a new SerializationProxy
         * @param exception The current instance of the proxy
         */
        SerializationProxy(final KeyGenerationException exception) {
            this.throwable = exception.getCause();
        }

        /**
         * Not to be called by the user - resolves a new object of this proxy
         * @return The object resolved by this proxy
         */
        private Object readResolve() {
            return new KeyGenerationException(this.throwable);
        }
    }
}
