package com.nervousfish.nervousfish.data_objects;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Simple variant of {@link IKey}. This is an example implementation of the {@link IKey} interface.
 */
public final class SimpleKey implements IKey {

    private final static String TYPE = "simple";

    private final String key;
    private final String name;

    /**
     * Constructor for a simple key.
     *
     * @param name The name for the key.
     * @param key  The key string.
     */
    public SimpleKey(final String name, final String key) {
        this.name = name;
        this.key = key;
    }

    /**
     * Create a new SimpleKey given a {@link JsonReader}.
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

        final String name = map.get("name");
        final String key = map.get("key");
        return new SimpleKey(name, key);
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
        return SimpleKey.TYPE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void toJSON(final JsonWriter writer) throws IOException {
        writer.name("name").value(this.name);
        writer.name("key").value(this.key);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object o) {
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }

        final SimpleKey that = (SimpleKey) o;
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

}
