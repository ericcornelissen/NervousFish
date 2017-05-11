package com.nervousfish.nervousfish.data_objects;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * A Java object representation (POJO) of a contact.
 */
public final class Contact implements Serializable {

    private final String name;
    private final List<IKey> keys = new ArrayList<IKey>();

    /**
     * Constructor for the {@link Contact} POJO for a single {@link IKey}.
     *
     * @param name      The name of the {@link Contact}
     * @param key       The {@link IKey} to initialize the {@link Contact} with
     */
    public Contact(final String name, final IKey key) {
        this.name = name;
        this.keys.add(key);
    }

    /**
     * Constructor for the {@link Contact} POJO for multiple {@link IKey}s.
     *
     * @param name The name of the {@link Contact}
     * @param keys The {@link Collection} of keys to initialize the {@link Contact} with
     */
    public Contact(final String name, final Collection<IKey> keys) {
        this.name = name;
        this.keys.addAll(keys);
    }

    /**
     * Get the name of the {@link Contact}.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Get the public key of the {@link Contact}.
     */
    public List<IKey> getKeys() {
        return this.keys;
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
                && this.keys.equals(that.keys);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return this.name.hashCode() + this.keys.hashCode();
    }

}
