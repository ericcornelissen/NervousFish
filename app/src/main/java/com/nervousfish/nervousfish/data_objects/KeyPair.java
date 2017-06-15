package com.nervousfish.nervousfish.data_objects;

import org.apache.commons.lang3.Validate;

/**
 * KeyPair POJO which keeps a public and private key.
 */
public final class KeyPair {

    private final String name;
    private final IKey publicKey;
    private final IKey privateKey;

    /**
     * The constructor for the {@link KeyPair}.
     *
     * @param name       The name for the {@link KeyPair}.
     * @param publicKey  The publicKey of the {@link KeyPair}.
     * @param privateKey The privateKey of the {@link KeyPair}.
     */
    public KeyPair(final String name, final IKey publicKey, final IKey privateKey) {
        Validate.notBlank(name);
        Validate.notNull(publicKey);
        Validate.notNull(privateKey);
        this.name = name;
        this.publicKey = publicKey;
        this.privateKey = privateKey;
    }

    /**
     * Returns the name of the {@link KeyPair}.
     *
     * @return the name.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Returns the public key of the {@link KeyPair}.
     *
     * @return the public {@link IKey}.
     */
    public IKey getPublicKey() {
        return this.publicKey;
    }

    /**
     * Returns the private key of the {@link KeyPair}.
     *
     * @return the private {@link IKey}.
     */
    public IKey getPrivateKey() {
        return this.privateKey;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object o) {
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }

        final KeyPair that = (KeyPair) o;
        return this.name.equals(that.name)
                && this.publicKey.equals(that.publicKey)
                && this.privateKey.equals(that.privateKey);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return this.name.hashCode() + this.publicKey.hashCode() + this.privateKey.hashCode();
    }

}
