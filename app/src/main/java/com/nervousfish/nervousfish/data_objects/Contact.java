package com.nervousfish.nervousfish.data_objects;

/**
 * A Java object representation (POJO) of a contact.
 */
public final class Contact {

    public String name;
    public IKey publicKey;

    public Contact(final String name, final IKey publicKey) {
        this.name = name;
        this.publicKey = publicKey;
    }

}
