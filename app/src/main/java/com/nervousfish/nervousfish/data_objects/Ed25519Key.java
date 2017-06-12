package com.nervousfish.nervousfish.data_objects;

import com.google.gson.stream.JsonWriter;
import com.nervousfish.nervousfish.ConstantKeywords;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Map;

/**
 * Ed25519 variant of {@link IKey}. This is an example implementation of the {@link IKey} interface.
 *
 * For more info about Ed25519 see: https://ed25519.cr.yp.to/
 */
public final class Ed25519Key implements IKey {

    private static final long serialVersionUID = -3865050366412869804L;

    private static final String KEYWORD_NAME = "name";
    private static final String KEYWORD_KEY = "key";

    private final String key;
    private final String name;

    /**
     * Constructor for a Ed25519-based key.
     *
     * @param name The name for the key.
     * @param key  The key string.
     */
    public Ed25519Key(final String name, final String key) {
        this.name = name;
        this.key = key;
    }

    /**
     * Constructor for a Ed25519-based key given a {@link Map} of its values.
     *
     * @param map A {@link Map} mapping {@link Ed25519Key} attribute names to values.
     */
    public Ed25519Key(final Map<String, String> map) throws IllegalArgumentException {
        this.name = map.get(KEYWORD_NAME);
        this.key = map.get(KEYWORD_KEY);

        if (this.name == null || this.key == null) {
            throw new IllegalArgumentException("Name or key could not be found in the map");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getKey() {
        return this.key;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return this.name;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getType() {
        return ConstantKeywords.ED25519_KEY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void toJson(final JsonWriter writer) throws IOException {
        writer.name(KEYWORD_NAME).value(this.name);
        writer.name(KEYWORD_KEY).value(this.key);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object o) {
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }

        final Ed25519Key that = (Ed25519Key) o;
        return this.name.equals(that.name)
                && this.key.equals(that.key);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return this.key.hashCode() + this.name.hashCode();
    }


    /**
     * Serialize the created proxy instead of this instance.
     */
    private Object writeReplace() {
        return new SerializationProxy(this);
    }

    /**
     * Ensure that no instance of this class is created because it was present in the stream. A
     * correct stream should only contain instances of the proxy.
     */
    private void readObject(final ObjectInputStream stream) throws InvalidObjectException {
        throw new InvalidObjectException(ConstantKeywords.PROXY_REQUIRED);
    }

    /**
     * Represents the logical state of this class and copies the data from that class without
     * any consistency checking or defensive copying.
     */
    private static final class SerializationProxy implements Serializable {

        private static final long serialVersionUID = -3865050366412869804L;

        private final String key;
        private final String name;

        /**
         * Constructs a new SerializationProxy
         * @param key The current instance of the proxy
         */
        SerializationProxy(final Ed25519Key key) {
            this.key = key.key;
            this.name = key.name;
        }

        /**
         * Not to be called by the user - resolves a new object of this proxy
         * @return The object resolved by this proxy
         */
        private Object readResolve() {
            return new Ed25519Key(this.name, this.key);
        }

    }

}
