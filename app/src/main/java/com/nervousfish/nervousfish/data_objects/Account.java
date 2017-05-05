package com.nervousfish.nervousfish.data_objects;

/**
 * An Account POJO to store a name, publickey and privatekey.
 */

public class Account {
    public String name;
    public IKey publicKey;
    public IKey privateKey;

    public Account(final String name, final IKey publicKey, final IKey privateKey) {
        this.name = name;
        this.publicKey = publicKey;
        this.privateKey = privateKey;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object o) {
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }

        final Account that = (Account) o;
        return this.name.equals(that.name)
                && this.publicKey.equals(that.publicKey)
                && this.privateKey.equals(that.privateKey);
    }
}
