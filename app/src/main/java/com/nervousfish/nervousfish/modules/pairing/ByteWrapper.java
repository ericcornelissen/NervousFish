package com.nervousfish.nervousfish.modules.pairing;

import com.nervousfish.nervousfish.ConstantKeywords;

import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;

/**
 * This class contains a byte array and is used as a wrapped object from
 */
public final class ByteWrapper implements Serializable {
    private static final long serialVersionUID = -1704556072876435760L;
    private final byte[] bytes;

    /**
     * Creates a new ByteWrapper
     *
     * @param bytes The byte array the wrapper wraps
     */
    ByteWrapper(final byte[] bytes) {
        this.bytes = bytes.clone();
    }

    /**
     * @return The data object {@link DataWrapper} wraps
     */
    public byte[] getBytes() {
        return this.bytes.clone();
    }

    /**
     * Serialize the created proxy instead of this instance.
     */
    private Object writeReplace() {
        return new ByteWrapper.SerializationProxy(this);
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
     * Used for the Serialization Proxy Pattern.
     * We suppress here the AccessorClassGeneration warning because the only alternative to this pattern -
     * ordinary serialization - is far more dangerous
     */
    @SuppressWarnings({"PMD.AccessorClassGeneration", "SerializableHasSerializationMethods"})
    // 1) A private constructor is safer than a hidden constructor
    // 2) SerializationProxy doesn't need the write method, because it creates the class by calling the contstructor in readResolve
    private static final class SerializationProxy implements Serializable {
        private static final long serialVersionUID = -1704556072876435760L;
        private final byte[] bytes;

        /**
         * Constructs a new SerializationProxy
         *
         * @param wrapper The current instance of the proxy
         */
        SerializationProxy(final ByteWrapper wrapper) {
            this.bytes = wrapper.getBytes();
        }

        /**
         * Not to be called by the user - resolves a new object of this proxy
         *
         * @return The object resolved by this proxy
         */
        private Object readResolve() {
            return new ByteWrapper(this.bytes);
        }
    }
}