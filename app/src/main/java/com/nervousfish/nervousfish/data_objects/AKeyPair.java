package com.nervousfish.nervousfish.data_objects;


import com.nervousfish.nervousfish.ConstantKeywords;

import org.apache.commons.lang3.Validate;

import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;

/**
 * AKeyPair POJO which keeps a public and private key.
 */
public abstract class AKeyPair<T1, T2> implements Serializable {

    private static final long serialVersionUID = -9078691377598696344L;
    final String name;
    final T1 publicKey;
    final T2 privateKey;

    /**
     * The constructor for the {@link AKeyPair}.
     *
     * @param name       The name for the {@link AKeyPair}.
     * @param publicKey  The publicKey of the {@link AKeyPair}.
     * @param privateKey The privateKey of the {@link AKeyPair}.
     */
    public AKeyPair(final String name, final T1 publicKey, final T2 privateKey) {
        Validate.notBlank(name);
        Validate.notNull(publicKey);
        Validate.notNull(privateKey);
        this.name = name;
        this.publicKey = publicKey;
        this.privateKey = privateKey;
    }

    /**
     * Returns the name of the {@link AKeyPair}.
     *
     * @return the name.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Returns the public key of the {@link AKeyPair}.
     *
     * @return the public {@link IKey}.
     */
    public Object getPublicKey() {
        return this.publicKey;
    }

    /**
     * Returns the private key of the {@link AKeyPair}.
     *
     * @return the private {@link IKey}.
     */
    public Object getPrivateKey() {
        return this.privateKey;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object obj) {
        if (obj == null || this.getClass() != obj.getClass()) {
            return false;
        }

        final AKeyPair<T1, T2> that = (AKeyPair<T1, T2>) obj;
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
