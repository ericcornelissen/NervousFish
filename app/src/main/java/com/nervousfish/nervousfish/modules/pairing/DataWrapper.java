package com.nervousfish.nervousfish.modules.pairing;

import com.nervousfish.nervousfish.ConstantKeywords;

import org.apache.commons.lang3.SerializationUtils;

import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;

/**
 * This class is used to let the other party know what kind of class it contains. This class is the only
 * class that should be sent directly over the network.
 */
final class DataWrapper implements Serializable {
    private static final long serialVersionUID = -1704556072876435760L;
    private final Serializable data;
    private final Class<?> clazz;

    /**
     * Creates a new DataWrapper
     *
     * @param data The {@link Serializable} object the wrapper wraps
     */
    DataWrapper(final Serializable data) {
        this.data = data;
        this.clazz = data.getClass();
    }

    /**
     * @return The data object {@link DataWrapper} wraps
     */
    Serializable getData() {
        return SerializationUtils.clone(this.data);
    }

    /**
     * @return The class of the thing the {@link DataWrapper} wraps
     */
    Class<?> getClazz() {
        return this.clazz;
    }

    /**
     * Serialize the created proxy instead of this instance.
     */
    private Object writeReplace() {
        return new DataWrapper.SerializationProxy(this);
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
    // A private constructor is safer than a hidden constructor
    @SuppressWarnings({"PMD.AccessorClassGeneration", "SerializableHasSerializationMethods"})
    private static final class SerializationProxy implements Serializable {
        private static final long serialVersionUID = -1704556072876435760L;
        private final Serializable data;

        /**
         * Constructs a new SerializationProxy
         * @param wrapper The current instance of the proxy
         */
        SerializationProxy(final DataWrapper wrapper) {
            this.data = wrapper.getData();
        }

        /**
         * Not to be called by the user - resolves a new object of this proxy
         * @return The object resolved by this proxy
         */
        private Object readResolve() {
            return new DataWrapper(this.data);
        }
    }
}