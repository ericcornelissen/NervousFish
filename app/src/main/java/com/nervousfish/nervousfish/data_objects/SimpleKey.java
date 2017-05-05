package com.nervousfish.nervousfish.data_objects;

/**
 * Simple variant of {@link IKey}. This is an example implementation of the {@link IKey} interface.
 */
public final class SimpleKey implements IKey {

    private final static String TYPE = "simple";

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
    public String getKey() {
        return this.key;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getType() {
        return SimpleKey.TYPE;
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

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return this.getKey().hashCode();
    }

}
