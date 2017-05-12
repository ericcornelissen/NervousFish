package com.nervousfish.nervousfish.data_objects;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.nervousfish.nervousfish.ConstantKeywords;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * RSA variant of {@link IKey}.
 */
public final class RSAKey implements IKey {

    private final static String TYPE = ConstantKeywords.RSA_KEY;

    private final String exponent;
    private final String modulus;

    /**
     * Constructor for a RSA key.
     *
     * @param modulus  The modules of the RSA key.
     * @param exponent The exponent of the RSA key.
     */
    public RSAKey(final String modulus, final String exponent) {
        this.modulus = modulus;
        this.exponent = exponent;
    }

    /**
     * Create a new RSAKey given a {@link JsonReader}.
     *
     * @param reader The {@link JsonReader} to read with.
     * @return An {@link IKey} representing the JSON key.
     * @throws IOException When the {@link JsonReader} throws an {@link IOException}.
     */
    static public IKey fromJSON(final JsonReader reader) throws IOException {
        final Map<String, String> map = new ConcurrentHashMap<>();
        while (reader.hasNext()) {
            final String name = reader.nextName();
            final String value = reader.nextString();
            map.put(name, value);
        }

        final String modulus = map.get("modulus");
        final String exponent = map.get("exponent");
        return new RSAKey(modulus, exponent);
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
    public String getType() {
        return RSAKey.TYPE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void toJSON(final JsonWriter writer) throws IOException {
        writer.name("modulus").value(this.modulus);
        writer.name("exponent").value(this.exponent);
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
        return this.modulus.equals(that.modulus)
                && this.exponent.equals(that.exponent);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return this.getKey().hashCode();
    }

}
