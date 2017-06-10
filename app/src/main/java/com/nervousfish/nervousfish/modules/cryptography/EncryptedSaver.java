package com.nervousfish.nervousfish.modules.cryptography;


import android.util.Base64;

import com.nervousfish.nervousfish.data_objects.IKey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

public final class EncryptedSaver {

    private static final Logger LOGGER = LoggerFactory.getLogger("Encrypted Saver");
    private static final int SEED = 1234569;

    /**
     * Unused constructor for utility class.
     */
    private EncryptedSaver() {
        // Prevent instantiations of QRGenerator

    }

    /**
     * Generates a salt bytestring using a seed.
     * @return The salt bytestring.
     */
    public static byte[] generateSalt(int seed, int length) {
        final Random random = new Random(seed);
        byte bytes[] = new byte[length];
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
     * @param encrypt   whether we're encrypting or decrypting.
     * @return  The encrypted/decrypted bytearray.
     */
    public static String encryptOrDecryptWithPassword(final String toEncrypt, final String password,  final boolean encrypt) {
        LOGGER.info("Started encrypting with password");
        try {
            final byte[] ivSpec = new byte[8];
            final Random random = new Random(SEED);
            random.nextBytes(ivSpec);
            LOGGER.info(toEncrypt);
            byte[] decodedValue = Base64.decode(toEncrypt,Base64.DEFAULT);

            final int mode = encrypt ? Cipher.ENCRYPT_MODE : Cipher.DECRYPT_MODE;
            final Cipher cipher = getCipher(password, ivSpec, mode);

            //final byte[] converted = new byte[cipher.getOutputSize(toEncryptBytes.length)];
            //int conv_len = cipher.update(toEncryptBytes, 0, toEncrypt.length(), converted, 0);
            //conv_len += cipher.doFinal(converted, conv_len);

            //final byte[] result = new byte[conv_len];
            //for (int i = 0; i < result.length; i++)
            //    result[i] = converted[i];

            if (encrypt) {
                byte[] cipherText = cipher.doFinal(toEncrypt.getBytes());
                byte[] secretString = Base64.encode(cipherText, Base64.DEFAULT);
                LOGGER.info(new String(secretString));
                return new String(secretString);
            } else {
                byte[] plaintext = cipher.doFinal(decodedValue);
                return new String(plaintext);

            }
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
    private static final Cipher getCipher(final String psw, final byte[] ivSpec, final int mode) {
        LOGGER.info("Getting cipher for decrypting");
        try {
            PBEKeySpec keySpec = new PBEKeySpec(psw.toCharArray());
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
            SecretKey key = keyFactory.generateSecret(keySpec);

            //Create parameters from the salt and an arbitrary number of iterations:
            PBEParameterSpec pbeParamSpec = new PBEParameterSpec(ivSpec, 42);


            //Set up the cipher:
            Cipher cipher = Cipher.getInstance("PBEWithMD5AndDES");
            cipher.init(mode, key, pbeParamSpec);
            return cipher;
        } catch (NoSuchAlgorithmException e) {
            LOGGER.error("There isn't an algorithm as PBEWithMD5AndDES", e);

        } catch (InvalidKeySpecException e) {
            LOGGER.error("The key spec is invalid", e);
        } catch (NoSuchPaddingException e) {
            LOGGER.error("There isn't a padding as PBEWithMD5AndDES", e);
        } catch (InvalidKeyException e) {
            LOGGER.error("The key is invalid", e);
        } catch (InvalidAlgorithmParameterException e) {
            LOGGER.error("The algorithm parameter is invalid", e);
        }
        return null;
    }

    /**
     * Encrypt data using a public key.
     * @param text  -   The data to encrypt
     * @param publicKey -   The public key to encrypt the data with
     * @return  The encrypted data in String.
     * @throws IOException  -   Throws an IOException when the key can't be split up correctly
     */
    public static String encryptUsingRSA(String text, IKey publicKey) throws IOException {
        String[] keyComponents = publicKey.getKey().split(" ");
        if(keyComponents.length > 2) {
            throw new IOException("Public key can't be split up correctly");
        }
        try {
            RSAPublicKeySpec spec = new RSAPublicKeySpec(new BigInteger(keyComponents[0]), new BigInteger(keyComponents[1]));

            KeyFactory factory = KeyFactory.getInstance("RSA");
            PublicKey pub = factory.generatePublic(spec);

            final Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, pub);
            final byte[] cipherText = cipher.doFinal(text.getBytes());
            return new String(cipherText);
        } catch (NoSuchAlgorithmException e) {
            LOGGER.error("Error in generating rsa key factory from components", e);
        } catch (InvalidKeySpecException e) {
            LOGGER.error("Error in generating public key from factory and specs", e);
        } catch (Exception e) {
            LOGGER.error("Something went wrong encrypting the string", e);
        }
        return null;

    }

    /**
     * Decrypts data using a private key.
     * @param text  -   The data to decrypt.
     * @param privateKey    -   The private key to decrypt the data with.
     * @return  The decrypted data in String
     * @throws IOException  -   Throws an IOException when the key can't be split up correctly
     */
    public static String decryptUsingRSA(String text, IKey privateKey) throws IOException {
        String[] keyComponents = privateKey.getKey().split(" ");
        if(keyComponents.length > 2) {
            throw new IOException("Private key can't be split up correctly");
        }
        try {
            RSAPrivateKeySpec spec = new RSAPrivateKeySpec(new BigInteger(keyComponents[0]), new BigInteger(keyComponents[1]));
            KeyFactory factory = KeyFactory.getInstance("RSA");
            PrivateKey priv = factory.generatePrivate(spec);
            final Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, priv);
            byte[] decryptedData = cipher.doFinal(text.getBytes());
            return new String(decryptedData);
        } catch (NoSuchAlgorithmException e) {
            LOGGER.error("Error in generating rsa key factory from components", e);
        } catch (InvalidKeySpecException e) {
            LOGGER.error("Error in generating private key from factory and specs", e);
        } catch (Exception e) {
            LOGGER.error("Something went wrong decrypting the string", e);
        }
        return null;
    }

}
