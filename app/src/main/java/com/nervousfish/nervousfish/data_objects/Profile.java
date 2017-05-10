package com.nervousfish.nervousfish.data_objects;

/**
 * An Profile POJO to store a name, public key and private key.
 */
public class Profile {

    private final String name;
    private final KeyPair keyPair;

    /**
     * The constructor for the {@link Profile} class.
     *
     * @param name the name belonging to the Profile
     * @param keyPair the public/private key-pair
     */
    public Profile(final String name, final KeyPair keyPair) {
        this.name = name;
        this.keyPair = keyPair;
    }

    /**
     * Get the name of the {@link Profile}.
     *
     * @return The name.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Get the {@link KeyPair} of the {@link Profile}.
     *
     * @return The {@link KeyPair}.
     */
    public KeyPair getKeyPair() { return this.keyPair; }

    /**
     * Get the public {@link IKey} of the {@link Profile}.
     *
     * @return The public {@link IKey}.
     */
    public IKey getPublicKey() {
        return this.keyPair.getPublicKey();
    }

    /**
     * Get the private {@link IKey} of the {@link Profile}.
     *
     * @return The private {@link IKey}.
     */
    public IKey getPrivateKey() {
        return this.keyPair.getPrivateKey();
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
            && this.keyPair.equals(that.keyPair);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return this.name.hashCode() + this.keyPair.hashCode();
    }

}
