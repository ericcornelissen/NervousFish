package com.nervousfish.nervousfish.data_objects;

import java.io.Serializable;

/**
 * A Java object representation (POJO) of a contact.
 */
public final class Contact implements Serializable {

    public final String name;
    public final IKey publicKey;

    /**
     * Simple constructor for the Contact POJO.
     *
     * @param name The name of the contact
     * @param publicKey The public key of the contact
     */
    public Contact(final String name, final IKey publicKey) {
        this.name = name;
        this.publicKey = publicKey;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object o) {
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }

        final Contact that = (Contact) o;
        return this.name.equals(that.name)
            && this.publicKey.equals(that.publicKey);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return this.name.hashCode() + this.publicKey.hashCode();
    }

}