package com.nervousfish.nervousfish.data_objects;

import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.io.Serializable;

/**
 * The interface for the standard Java representation of a (public/private) key.
 */
public interface IKey extends Serializable {

    /**
     * A list of {@link IKey} implementations known to the system.
     */
    enum Types { RSA, Ed25519 }

    /**
     * Get a string representation of a key.
     *
     * @return The key as a {@link String}.
     */
    String getKey();

    /**
     * Get a string representing the name of the key.
     *
     * @return The key name.
     */
    String getName();

    /**
     * Get a string representation of the key type.
     *
     * @return The key type.
     */
    String getType();

    /**
     * Write the key to a {@link JsonWriter}.
     * @param writer Used to write the key to
     */
    void toJson(JsonWriter writer) throws IOException;

}
