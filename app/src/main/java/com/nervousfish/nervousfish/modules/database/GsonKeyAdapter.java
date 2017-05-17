package com.nervousfish.nervousfish.modules.database;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.nervousfish.nervousfish.ConstantKeywords;
import com.nervousfish.nervousfish.data_objects.IKey;
import com.nervousfish.nervousfish.data_objects.RSAKey;
import com.nervousfish.nervousfish.data_objects.SimpleKey;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Adaptor for the {@link IKey} interface for the GSON library.
 */
final class GsonKeyAdapter extends TypeAdapter<IKey> {

    /**
     * Constructor for the {@code GsonKeyAdapter} class.
     */
    GsonKeyAdapter() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void write(final JsonWriter writer, final IKey key) throws IOException {
        writer.beginArray();

        // First write the key type
        final String keyType = key.getType();
        writer.value(keyType);

        // Then write the rest of the key
        writer.beginObject();
        key.toJson(writer);
        writer.endObject();

        writer.endArray();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    // final IKey key is actually useful here
    @SuppressWarnings("PMD.AvoidFinalLocalVariable")
    public IKey read(final JsonReader reader) throws IOException {
        reader.beginArray();
        final String type = reader.nextString();

        reader.beginObject();
        final Map<String, String> map = new ConcurrentHashMap<>();
        while (reader.hasNext()) {
            final String name = reader.nextName();
            final String value = reader.nextString();
            map.put(name, value);
        }
        reader.endObject();

        reader.endArray();

        final IKey key;
        switch (type) {
            case ConstantKeywords.RSA_KEY:
                key = new RSAKey(map);
                break;
            case "simple":
                key = new SimpleKey(map);
                break;
            default:
                throw new IOException("Could not read key");
        }

        return key;
    }

}
