package com.nervousfish.nervousfish.modules.cryptography;

import com.nervousfish.nervousfish.data_objects.IKey;
import com.nervousfish.nervousfish.data_objects.KeyPair;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public final class EncryptedSaver {

    private static final int SALT_LENGTH = 30;
    private static final Logger LOGGER = LoggerFactory.getLogger("QRGenerator");

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
        final SecureRandom random = new SecureRandom();
        byte bytes[] = new byte[SALT_LENGTH];
        random.nextBytes(bytes);
        LOGGER.info("Generated salt bytestring");
        return bytes;
    }

    /**
     * Hashes a given pass to an encrypted string that can't be decrypted.
     * @param pass  THe string to encrypt
     * @return  The encrypted string.
     */
    public static String hashWithoutSalt(String pass) {
        try {
            final MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.reset();
            final byte[] hash = digest.digest(pass.getBytes(StandardCharsets.UTF_8));
            LOGGER.info("Hashed the pass with SHA-256, no salt");
            return new String(hash, "UTF-8");
        } catch (NoSuchAlgorithmException e){
            LOGGER.error("SHA-256 is not a valid encryptionalgorithm", e);
        } catch (UnsupportedEncodingException e) {
            LOGGER.error("UTF-8 is not a valid encoding method", e);
        }
        return null;
    }

    /**
     * Hashes a pass to an encrypted string that can be decrypted with the salt.
     * @param salt The salt bytestring to encrypt the pass with.
     * @param pass  The string to encrypt
     * @return The encrypted string.
     */
    public static String hashUsingSalt(byte[] salt, String pass){
        try {
            final MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.reset();
            digest.update(salt);
            final byte[] hash = digest.digest(pass.getBytes(StandardCharsets.UTF_8));
            LOGGER.info("Hashed the pass using the salt");
            return new String(hash, "UTF-8");
        } catch (NoSuchAlgorithmException e){
            LOGGER.error("SHA-256 is not a valid encryptionalgorithm", e);
        } catch (UnsupportedEncodingException e) {
            LOGGER.error("UTF-8 is not a valid encoding method", e);
        }
        return null;
    }



}
