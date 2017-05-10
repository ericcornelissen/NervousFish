package com.nervousfish.nervousfish.data_objects;

/**
 * An Profile POJO to store a name, public key and private key.
 */
public class Profile {

    private final String name;
    private final IKey publicKey;
    private final IKey privateKey;

    /**
     * A constructor for the {@link Profile} class.
     *
     * @param name - the name belonging to the Profile
     * @param publicKey - the public key
     * @param privateKey - the private key
     */
    public Profile(final String name, final IKey publicKey, final IKey privateKey) {
        this.name = name;
        this.publicKey = publicKey;
        this.privateKey = privateKey;
    }

    /**
     * Get the name of the {@link Profile}.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Get the public key of the {@link Profile}.
     */
    public IKey getPublicKey() {
        return this.publicKey;
    }

    /**
     * Get the private key of the {@link Profile}.
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

        final Profile that = (Profile) o;
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
