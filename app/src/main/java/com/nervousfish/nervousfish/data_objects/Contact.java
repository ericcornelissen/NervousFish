package com.nervousfish.nervousfish.data_objects;

/**
 * A Java object representation (POJO) of a contact.
 */
public final class Contact {

    public final String name;
    public final String publicKey;

    public Contact(final String name, final String publicKey) {
        this.name = name;
        this.publicKey = publicKey;
    }

}
