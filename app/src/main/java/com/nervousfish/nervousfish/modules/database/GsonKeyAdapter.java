package com.nervousfish.nervousfish.modules.database;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.nervousfish.nervousfish.ConstantKeywords;
import com.nervousfish.nervousfish.data_objects.Ed25519Key;
import com.nervousfish.nervousfish.data_objects.IKey;
import com.nervousfish.nervousfish.data_objects.RSAKey;

import org.apache.commons.lang3.Validate;

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
    @SuppressWarnings("ParameterNameDiffersFromOverriddenParameter")
    // 1) Suppressed because key is a lot more descripte than t (the parameter name of the superclass)
    @Override
    public void write(final JsonWriter jsonWriter, final IKey key) throws IOException {
        Validate.notNull(jsonWriter);
        Validate.notNull(key);
        jsonWriter.beginArray();

        // First write the key type
        final String keyType = key.getType();
        jsonWriter.value(keyType);

        // Then write the rest of the key
        jsonWriter.beginObject();
        key.toJson(jsonWriter);
        jsonWriter.endObject();

        jsonWriter.endArray();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("PMD.AvoidFinalLocalVariable")  // final IKey key is actually useful here
    public IKey read(final JsonReader jsonReader) throws IOException {
        Validate.notNull(jsonReader);
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
            case ConstantKeywords.ED25519_KEY:
                key = new Ed25519Key(map);
                break;
            default:
                throw new IOException("Could not read key");
        }

        return key;
    }
}
