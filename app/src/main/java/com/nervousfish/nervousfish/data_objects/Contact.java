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

    @Override
    public boolean equals(Object o) {
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }

        Contact that = (Contact) o;
        return this.name.equals(that.name)
            &&  this.publicKey.equals(that.publicKey);
    }

}
