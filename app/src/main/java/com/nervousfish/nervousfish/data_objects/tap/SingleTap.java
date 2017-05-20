package com.nervousfish.nervousfish.data_objects.tap;

import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.sql.Timestamp;

/**
 * This class contains the bare minimum functionality of a tap event.
 */
public final class SingleTap extends AbstractTapData {
    private static final long serialVersionUID = 6955333968356740403L;
    /**
     * {@inheritDoc}
     */
    public SingleTap() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    public SingleTap(final Timestamp timestamp) {
        super(timestamp);
    }

    /**
     * Serialize the created proxy instead of this instance.
     */
    private Object writeReplace() {
        return new SerializationProxy();
    }

    /**
     * Ensure that no instance of this class is created because it was present in the stream. A correct
     * stream should only contain instances of the proxy.
     */
    private void readObject(final ObjectInputStream stream) throws InvalidObjectException {
        throw new InvalidObjectException("Proxy required.");
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

        private static final long serialVersionUID = 6955333968356740403L;

        SerializationProxy() {
            // Nothing to do here
        }

        private Object readResolve() {
            return new SingleTap();
        }
    }
}
