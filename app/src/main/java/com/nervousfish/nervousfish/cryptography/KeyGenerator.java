package com.nervousfish.nervousfish.cryptography;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;

public class KeyGenerator {

    /**
     * Generates a random KeyPair with the RSA algorithm.
     * @return a randomly generated KeyPair
     */
    public static KeyPair generateRandomKeyPair() {
        try {
            KeyPairGenerator k = KeyPairGenerator.getInstance("RSA");

            k.initialize(2048);
            return k.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Gets from kp the public key and makes a RSAPublicKeySpec out of it.
     * @param kp the KeyPair with the public key
     * @return the RSAPublicKeySpec
     */
    public static RSAPublicKeySpec getPublicKeySpec(KeyPair kp) {
        try {
            KeyFactory fact = KeyFactory.getInstance("RSA");

            return fact.getKeySpec(kp.getPublic(),
                    RSAPublicKeySpec.class);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Gets from kp the private key and makes a RSAPrivateKeySpec out of it.
     * @param kp the KeyPair with the private key
     * @return the RSAPrivateKeySpec
     */
    public static RSAPrivateKeySpec getPrivateKeySpec(KeyPair kp) {
        try {
            KeyFactory fact = KeyFactory.getInstance("RSA");

            return fact.getKeySpec(kp.getPrivate(),
                    RSAPrivateKeySpec.class);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return null;
    }
}
