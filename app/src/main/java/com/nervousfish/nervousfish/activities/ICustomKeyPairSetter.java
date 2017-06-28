package com.nervousfish.nervousfish.activities;

import com.nervousfish.nervousfish.data_objects.RSAKey;

/**
 * Sets the new custom key pair
 */
interface ICustomKeyPairSetter {
    /**
     * Sets the custom key of the user to the RSA key pair specified
     *
     * @param publicKey  The public part of the RSA key pair
     * @param privateKey The private part of the RSA key pair
     */
    void setRSAKeyPair(final RSAKey publicKey, final RSAKey privateKey);
}
