package com.nervousfish.nervousfish.data_objects;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * A Profile POJO to store a name, public key and private key.
 */
public class Profile {

    private final String name;
    private final List<KeyPair> keyPairs = new ArrayList<>();

    /**
     * The constructor for a {@link Profile} with a single {@link KeyPair}.
     *
     * @param name the name belonging to the Profile.
     * @param keyPair the public/private key-pair.
     */
    public Profile(final String name, final KeyPair keyPair) {
        this.name = name;
        this.keyPairs.add(keyPair);
    }

    /**
     * The constructor for a {@link Profile} with multiple {@link KeyPair}s.
     *
     * @param name the name belonging to the Profile.
     * @param keyPairs the public/private key-pairs.
     */
    public Profile(final String name, final Collection<KeyPair> keyPairs) {
        this.name = name;
        this.keyPairs.addAll(keyPairs);
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
     * Get the {@link KeyPair}s of the {@link Profile}.
     *
     * @return The {@link KeyPair}.
     */
    public List<KeyPair> getKeyPairs() { return this.keyPairs; }

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
            && this.keyPairs.equals(that.keyPairs);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return this.name.hashCode() + this.keyPairs.hashCode();
    }

}
