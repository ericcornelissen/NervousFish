package com.nervousfish.nervousfish.data_objects;

import java.math.BigInteger;

/**
 * RSAKey POJO which saves the key type, modulus and exponent.
 */

public final class RSAKey implements IKey {
    public final static String type = "RSA";
    public BigInteger modulus;
    public BigInteger exponent;

    public RSAKey(final BigInteger modulus, final BigInteger exponent) {
        this.modulus = modulus;
        this.exponent = exponent;
    }

    @Override
    public String getString() {
        return null;
    }
}
