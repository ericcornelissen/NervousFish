package com.nervousfish.nervousfish.data_objects.tap;

import com.nervousfish.nervousfish.ConstantKeywords;

import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * This class contains the bare minimum functionality of a tap event.
 */
public final class MultiTap {
    private static final long serialVersionUID = -3647892636477583511L;
    private final List<SingleTap> taps = new ArrayList<>();
    /**
     * Constructs a new tap data object that denotes a single tap event.
     * The time on which the tap event happened is assumed to be the moment that this
     * constructor is called.
     */
    public MultiTap(final Collection<SingleTap> taps) {
        this.taps.addAll(taps);
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
     * Used for the Serialization Proxy Pattern.
     * We suppress here the AccessorClassGeneration warning because the only alternative to this pattern -
     * ordinary serialization - is far more dangerous
     */
    @SuppressWarnings("PMD.AccessorClassGeneration")
    private static final class SerializationProxy implements Serializable {

        private static final long serialVersionUID = -3647892636477583511L;
        private final SingleTap[] taps;

        /**
         * Constructs a new SerializationProxy
         * @param multiTap The current instance of the proxy
         */
        SerializationProxy(final MultiTap multiTap) {
            this.taps = multiTap.taps.toArray(new SingleTap[multiTap.taps.size()]);
        }

        /**
         * Not to be called by the user - resolves a new object of this proxy
         * @return The object resolved by this proxy
         */
        private Object readResolve() {
            return new MultiTap(Arrays.asList(this.taps));
        }
    }
}
