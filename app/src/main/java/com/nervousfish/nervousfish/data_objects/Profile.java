package com.nervousfish.nervousfish.data_objects;

import org.apache.commons.lang3.Validate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * A Profile POJO to store a name, public key and private key.
 */
public final class Profile {

    private final String name;
    private final List<KeyPair> keyPairs = new ArrayList<>();

    /**
     * The constructor for a {@link Profile} with a single {@link KeyPair}.
     *
     * @param name    the name belonging to the Profile.
     * @param keyPair the public/private key-pair.
     */
    public Profile(final String name, final KeyPair keyPair) {
        Validate.notBlank(name);
        Validate.notNull(keyPair);
        this.name = name;
        this.keyPairs.add(keyPair);
    }

    /**
     * The constructor for a {@link Profile} with multiple {@link KeyPair}s.
     *
     * @param name     the name belonging to the Profile.
     * @param keyPairs the public/private key-pairs.
     */
    public Profile(final String name, final Collection<KeyPair> keyPairs) {
        Validate.notBlank(name);
        Validate.noNullElements(keyPairs);
        this.name = name;
        this.keyPairs.addAll(keyPairs);
    }

    /**
     * Get the name of the {@link Profile}.
     *
     * @return The name of the {@link Profile}.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Get the {@link KeyPair}s of the {@link Profile}.
     *
     * @return The {@link KeyPair}.
     */
    public List<KeyPair> getKeyPairs() {
        return new ArrayList<>(this.keyPairs);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object obj) {
        if (obj == null || this.getClass() != obj.getClass()) {
            return false;
        }

        final Profile other = (Profile) obj;
        return this.name.equals(other.getName())
                && this.keyPairs.equals(other.getKeyPairs());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return this.name.hashCode() + this.keyPairs.hashCode();
    }

}
