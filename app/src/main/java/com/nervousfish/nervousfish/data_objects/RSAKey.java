package com.nervousfish.nervousfish.data_objects;

import com.google.gson.stream.JsonWriter;
import com.nervousfish.nervousfish.ConstantKeywords;

import java.io.IOException;
import java.util.Map;

/**
 * RSA variant of {@link IKey}.
 */
public final class RSAKey implements IKey {

    private final static String TYPE = ConstantKeywords.RSA_KEY;
    private final static String JSON_CONSTANT_EXPONENT = "exponent";
    private final static String JSON_CONSTANT_MODULUS = "modulus";
    private final static String JSON_CONSTANT_NAME = "name";

    private final String exponent;
    private final String modulus;
    private final String name;

    /**
     * Constructor for a RSA key.
     *
     * @param name     The name for the key.
     * @param modulus  The modules of the RSA key.
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
           throw new IllegalArgumentException();
        }
    }

    /**
     * Get the exponent of an {@link RSAKey}.
     *
     * @param key An {@link RSAKey}.
     * @return The value for the exponent attribute of the {@code key}.
     * @throws Exception If the provided {@link IKey} is not a {@link RSAKey}.
     */
    public static String getExponent(final IKey key) throws IllegalArgumentException {
        final String type = key.getType();
        if (!type.equals(ConstantKeywords.RSA_KEY)) {
            throw new IllegalArgumentException();
        }

        return ((RSAKey) key).exponent;
    }

    /**
     * Get the modulus of an {@link RSAKey}.
     *
     * @param key An {@link RSAKey}.
     * @return The value for the modulus attribute of the {@code key}.
     * @throws Exception If the provided {@link IKey} is not a {@link RSAKey}.
     */
    public static String getModulus(final IKey key) throws IllegalArgumentException {
        final String type = key.getType();
        if (!type.equals(ConstantKeywords.RSA_KEY)) {
            throw new IllegalArgumentException();
        }

        return ((RSAKey) key).modulus;
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
    public void toJSON(final JsonWriter writer) throws IOException {
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

}
