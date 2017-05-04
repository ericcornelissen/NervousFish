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
}
