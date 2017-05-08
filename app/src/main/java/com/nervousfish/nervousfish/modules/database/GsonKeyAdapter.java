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

    private final static String KEY_TYPE_FIELD = "_type";

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
        writer.beginObject();
        switch (key.getType()) {
            case ConstantKeywords.RSA_KEY:
                writer.name(GsonKeyAdapter.KEY_TYPE_FIELD);
                writer.value("RSA");

                final String keyValue = key.getKey();
                final String[] keyValues = keyValue.split(" ");
                writer.name("modulus");
                writer.value(keyValues[0]);
                writer.name("exponent");
                writer.value(keyValues[1]);

                break;
            case "simple":
                writer.name(GsonKeyAdapter.KEY_TYPE_FIELD);
                writer.value("simple");

                writer.name("key");
                writer.value(key.getKey());

                break;
            default:
                throw new IOException("Could not write key");
        }
        writer.endObject();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IKey read(final JsonReader reader) throws IOException {
        final Map<String, String> keyMap = new ConcurrentHashMap<>();

        reader.beginObject();
        while (reader.hasNext()) {
            final String name = reader.nextName();
            final String value = reader.nextString();
            keyMap.put(name, value);
        }
        reader.endObject();

        final String type = keyMap.get(GsonKeyAdapter.KEY_TYPE_FIELD);
        switch (type) {
            case ConstantKeywords.RSA_KEY:
                return new RSAKey(keyMap.get("modulus"), keyMap.get("exponent"));
            case "simple":
                return new SimpleKey(keyMap.get("key"));
            default:
                throw new IOException("Could not read key");
        }
    }

}
