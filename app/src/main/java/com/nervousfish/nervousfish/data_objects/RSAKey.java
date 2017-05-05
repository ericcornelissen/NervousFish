package com.nervousfish.nervousfish.data_objects;

/**
 * RSA variant of {@link IKey}.
 */
public final class RSAKey implements IKey {

    private final static String TYPE = "RSA";

    public final String exponent;
    public final String modulus;

    /**
     * Constructor for a RSA key.
     *
     * @param modulus The modules of the RSA key.
     * @param exponent The exponent of the RSA key.
     */
    public RSAKey(final String modulus, final String exponent) {
        this.modulus = modulus;
        this.exponent = exponent;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getKey() {
        return this.modulus + " " + this.exponent;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getType() {
        return RSAKey.TYPE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object o) {
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }

        final RSAKey that = (RSAKey) o;
        return this.modulus.equals(that.modulus)
            && this.exponent.equals(that.exponent);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return this.getKey().hashCode();
    }

}
