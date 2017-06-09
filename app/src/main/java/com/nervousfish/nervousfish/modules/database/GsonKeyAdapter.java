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
     * {@inheritDoc}
     */
    @Override
    public void write(final JsonWriter jsonWriter, final IKey t) throws IOException {
        jsonWriter.beginArray();

        // First write the key type
        final String keyType = t.getType();
        jsonWriter.value(keyType);

        // Then write the rest of the key
        jsonWriter.beginObject();
        t.toJson(jsonWriter);
        jsonWriter.endObject();

        jsonWriter.endArray();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("PMD.AvoidFinalLocalVariable")  // final IKey key is actually useful here
    public IKey read(final JsonReader jsonReader) throws IOException {
        jsonReader.beginArray();
        final String type = jsonReader.nextString();

        jsonReader.beginObject();
        final Map<String, String> map = new ConcurrentHashMap<>();
        while (jsonReader.hasNext()) {
            final String name = jsonReader.nextName();
            final String value = jsonReader.nextString();
            map.put(name, value);
        }
        jsonReader.endObject();

        jsonReader.endArray();

        final IKey key;
        switch (type) {
            case ConstantKeywords.RSA_KEY:
                key = new RSAKey(map);
                break;
            case ConstantKeywords.SIMPLE_KEY:
                key = new SimpleKey(map);
                break;
            default:
                throw new IOException("Could not read key");
        }

        return key;
    }

}
