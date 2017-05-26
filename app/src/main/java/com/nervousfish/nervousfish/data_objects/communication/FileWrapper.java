package com.nervousfish.nervousfish.data_objects.communication;

import com.nervousfish.nervousfish.ConstantKeywords;

import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Arrays;

/**
 * A wrapper class for file data in the form of bytes.
 */
public class FileWrapper implements Serializable {
    private static final long serialVersionUID = -7254574342369065265L;
    private final byte[] fileData;

    /**
     * Creates a new DataWrapper
     *
     * @param bytes The bytes of a file
     */
    @SuppressWarnings("EI_EXPOSE_REP2") // This is intended for proxy
    public FileWrapper(final byte[] bytes) {
        this.fileData = Arrays.copyOf(bytes, bytes.length);
    }

    /**
     * @return The bytes representing the file
     */
    public byte[] getData() {
        return new FileWrapper(this.fileData).fileData;
    }

    /**
     * Serialize the created proxy instead of this instance.
     */
    private Object writeReplace() {
        return new FileWrapper.SerializationProxy(this);
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
    @SuppressWarnings("PMD.AccessorClassGeneration")
    private static final class SerializationProxy implements Serializable {
        private static final long serialVersionUID = -7254574342369065265L;
        private final byte[] fileData;

        /**
         * Constructs a new SerializationProxy
         * @param wrapper The current instance of the proxy
         */
        SerializationProxy(final FileWrapper wrapper) {
            this.fileData = wrapper.fileData;
        }

        /**
         * Not to be called by the user - resolves a new object of this proxy
         * @return The object resolved by this proxy
         */
        private Object readResolve() {
            return new FileWrapper(fileData);
        }
    }
}
