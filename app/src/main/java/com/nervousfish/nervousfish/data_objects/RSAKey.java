package com.nervousfish.nervousfish.data_objects;

import com.google.gson.stream.JsonWriter;
import com.nervousfish.nervousfish.ConstantKeywords;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Map;

/**
 * RSA variant of {@link IKey}.
 */
public final class RSAKey implements IKey {
    private static final long serialVersionUID = -5286281533321045061L;

    private static final String TYPE = ConstantKeywords.RSA_KEY;
    private static final String JSON_CONSTANT_EXPONENT = "exponent";
    private static final String JSON_CONSTANT_MODULUS = "modulus";
    private static final String JSON_CONSTANT_NAME = "name";

    private final String name;
    private final String modulus;
    private final String exponent;

    /**
     * Constructor for a RSA key.
     *
     * @param name     The name for the key.
     * @param modulus  The modulus of the RSA key.
     * @param exponent The exponent of the RSA key.
     */
    public RSAKey(final String name, final String modulus, final String exponent) {
        this.name = name;
        this.modulus = modulus;
        this.exponent = exponent;
    }

    /**
     * Constructor for a RSA key given a {@link Map} of its values.
     *
     * @param map A {@link Map} mapping {@link RSAKey} attribute names to values.
     */
    public RSAKey(final Map<String, String> map) throws IllegalArgumentException {
        this.name = map.get(RSAKey.JSON_CONSTANT_NAME);
        this.modulus = map.get(RSAKey.JSON_CONSTANT_MODULUS);
        this.exponent = map.get(RSAKey.JSON_CONSTANT_EXPONENT);

        if (this.name == null || this.modulus == null || this.exponent == null) {
            throw new IllegalArgumentException("Couldn't find the name, modulus or exponent in the map");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getKey() {
        return this.modulus + " " + this.exponent;
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
        return RSAKey.TYPE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void toJson(final JsonWriter writer) throws IOException {
        writer.name(RSAKey.JSON_CONSTANT_NAME).value(this.name);
        writer.name(RSAKey.JSON_CONSTANT_MODULUS).value(this.modulus);
        writer.name(RSAKey.JSON_CONSTANT_EXPONENT).value(this.exponent);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object o) {
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }

        final RSAKey that = (RSAKey) o;
        return this.name.equals(that.name)
                && this.modulus.equals(that.modulus)
                && this.exponent.equals(that.exponent);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return this.name.hashCode() + this.modulus.hashCode() + this.exponent.hashCode();
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
        throw new InvalidObjectException("Proxy required.");
    }

    /**
     * Represents the logical state of this class and copies the data from that class without
     * any consistency checking or defensive copying.
     */
    private static final class SerializationProxy implements Serializable {
        private static final long serialVersionUID = -5286281533321045061L;
        private final String name;
        private final String modulus;
        private final String exponent;

        SerializationProxy(final RSAKey key) {
            this.name = key.name;
            this.modulus = key.modulus;
            this.exponent = key.exponent;
        }

        private Object readResolve() {
            return new RSAKey(this.name, this.modulus, this.exponent);
        }
    }
}
