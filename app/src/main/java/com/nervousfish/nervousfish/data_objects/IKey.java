package com.nervousfish.nervousfish.data_objects;

import java.io.Serializable;

/**
 * The interface for the standard Java representation of a (public/private) key.
 */
public interface IKey extends Serializable {

    /**
     * Get a string representation of a key.
     *
     * @return The key as a {@link String}.
     */
    String getKey();

    /**
     * Get a string representation of the key type.
     *
     * @return The type.
     */
    String getType();

}
