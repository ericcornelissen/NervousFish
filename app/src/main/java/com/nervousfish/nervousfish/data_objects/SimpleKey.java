package com.nervousfish.nervousfish.data_objects;

/**
 * Simple variant of {@link IKey}.
 */
public final class SimpleKey implements IKey {

    public final static String type = "simple";

    public final String key;

    /**
     * Constructor for a simple key.
     *
     * @param key The key string.
     */
    public SimpleKey(final String key) {
        this.key = key;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getString() {
        return this.key;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object o) {
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }

        final SimpleKey that = (SimpleKey) o;
        return this.key.equals(that.key);
    }

}
