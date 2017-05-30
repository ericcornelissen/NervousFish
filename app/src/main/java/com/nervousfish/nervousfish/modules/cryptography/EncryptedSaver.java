package com.nervousfish.nervousfish.modules.cryptography;

import java.security.SecureRandom;

public final class EncryptedSaver {

    private static final int SALT_LENGTH = 30;
    /**
     * Unused constructor for utility class.
     */
    private EncryptedSaver() {
        // Prevent instantiations of QRGenerator

    }

    /**
     * Generates a salt bytestring
     * @return The salt bytestring.
     */
    public static byte[] generateSalt() {
        SecureRandom random = new SecureRandom();
        byte bytes[] = new byte[SALT_LENGTH];
        random.nextBytes(bytes);
        return bytes;
    }

    public static String hashUsingSalt(byte[] salt, String pass){

    }

}
