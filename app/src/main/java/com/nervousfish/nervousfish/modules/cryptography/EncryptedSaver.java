package com.nervousfish.nervousfish.modules.cryptography;

import com.nervousfish.nervousfish.data_objects.IKey;
import com.nervousfish.nervousfish.data_objects.KeyPair;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

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
     * Hashes a pass to an encrypted string with a salt bytestring.
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


    /**
     * Encrypts or decrypts a string with a password to
     * @param toEncrypt The string to be encrypted/decrypted
     * @param password  The password to encrypt/decrypt with
     * @param ivSpec    The initialization vector specifications bytestring (length 20)
     * @param encrypt   whether we're encrypting or decrypting.
     * @return  The encrypted/decrypted bytearray.
     */
    public static byte[] encryptOrDecryptWithPassword(final byte[] toEncrypt, final String password, final byte[] ivSpec, final boolean encrypt) {

        try {
            final int mode = encrypt ? Cipher.ENCRYPT_MODE : Cipher.DECRYPT_MODE;
            final byte[] psw = password.getBytes();
            final Cipher cipher = getCipher(psw, ivSpec, mode);
            final byte[] converted = new byte[cipher.getOutputSize(toEncrypt.length)];
            int conv_len = cipher.update(toEncrypt, 0, toEncrypt.length, converted, 0);
            conv_len += cipher.doFinal(converted, conv_len);

            final byte[] result = new byte[conv_len];
            for (int i = 0; i < result.length; i++)
                result[i] = converted[i];
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Method that return a configured {@link Cipher}.<br>
     * It set the ivSpec and the encryption to
     * DES
     *
     * @param psw   The password in bytestring used to encrypt
     * @param ivSpec    The initializations vector for the encryption
     * @param mode  Whether we're encrypting or decrypting
     * @return  the configured Cipher.
     */
    private static final Cipher getCipher(final byte[] psw, final byte[] ivSpec, final int mode) {
        try {
            final IvParameterSpec spec = new IvParameterSpec(ivSpec);
            final Cipher chiper = Cipher.getInstance("DES/CBC/PKCS5Padding");
            final SecretKeySpec key = new SecretKeySpec(psw, "DES");
            chiper.init(mode, key, spec);
            return chiper;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }





}
