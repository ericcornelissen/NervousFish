package com.nervousfish.nervousfish.modules.cryptography;

import android.util.Base64;

import com.nervousfish.nervousfish.exceptions.EncryptionException;
import com.nervousfish.nervousfish.service_locator.IServiceLocator;
import com.nervousfish.nervousfish.service_locator.ModuleWrapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Random;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

/**
 * An adapter to the default Java class for encrypting messages
 */
public final class EncryptorAdapter implements IEncryptor {
    private static final long serialVersionUID = 5930930748980177440L;
    private static final Logger LOGGER = LoggerFactory.getLogger("EncryptorAdapter");
    private static final String PBE_WITH_MD5_AND_DES = "PBEWithMD5AndDES";
    private static final String UTF_8_NO_LONGER_SUPPORTED = "UTF-8 is no longer an encoding algorithm";
    private static final String CANNOT_HAPPEN_UTF_8 = "Cannot happen, no encoding algorithm like UTF-8";
    private static final int SEED = 1234569;
    private static final int IV_SPEC_SIZE = 8;
    private static final String UTF_8 = "UTF-8";


    /**
     * Prevents construction from outside the class.
     *
     * @param serviceLocator Can be used to get access to other modules
     */
    // We suppress UnusedFormalParameter because the chance is big that a service locator will be used in the future
    @SuppressWarnings("PMD.UnusedFormalParameter")
    private EncryptorAdapter(final IServiceLocator serviceLocator) {
        LOGGER.info("Initialized");
    }

    /**
     * Creates a new instance of itself and wraps it in a {@link ModuleWrapper} so that only an
     * {@link IServiceLocator}
     *
     * @param serviceLocator The new service locator
     * @return A wrapper around a newly created instance of this class
     */
    @SuppressWarnings("MethodReturnOfConcreteClass")
    public static ModuleWrapper<EncryptorAdapter> newInstance(final IServiceLocator serviceLocator) {
        return new ModuleWrapper<>(new EncryptorAdapter(serviceLocator));
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String hashString(final String string) throws EncryptionException {
        try {
            final MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.reset();
            final byte[] hash = digest.digest(string.getBytes(StandardCharsets.UTF_8));
            LOGGER.info("Hashed the pass with SHA-256, no salt");
            return new String(hash, UTF_8);
        } catch (final NoSuchAlgorithmException e) {
            LOGGER.error("SHA-256 is not a valid encryption algorithm", e);
            throw new EncryptionException("Cannot happen, SHA-256 is not a valid encryption algorithm", e);
        } catch (final UnsupportedEncodingException e) {
            LOGGER.error("UTF-8 is not a valid encoding method", e);
            throw new EncryptionException(CANNOT_HAPPEN_UTF_8, e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SecretKey makeKeyFromPassword(final String password) throws InvalidKeySpecException, EncryptionException {
        LOGGER.info("Making key for encryption");
        try {
            final PBEKeySpec keySpec = new PBEKeySpec(password.toCharArray());
            final SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(PBE_WITH_MD5_AND_DES);
            final SecretKey key = keyFactory.generateSecret(keySpec);
            LOGGER.info("Key successfully made");
            return key;
        } catch (final NoSuchAlgorithmException e) {
            LOGGER.error("There isn't an algorithm like PBEWithMD5AndDES", e);
            throw new EncryptionException("Cannot happen, no algorithm like PBEWithMD5AndDES", e);
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String decryptWithPassword(final String toDecrypt, final SecretKey key) throws EncryptionException,
            IllegalBlockSizeException, BadPaddingException {
        LOGGER.info("Started decrypting with password");


        final byte[] ivSpec = new byte[IV_SPEC_SIZE];
        final Random random = new Random(SEED);
        random.nextBytes(ivSpec);

        final int mode = Cipher.DECRYPT_MODE;
        final Cipher cipher = this.getCipher(key, ivSpec, mode);

        try {
            final byte[] decodedValue = Base64.decode(toDecrypt, Base64.DEFAULT);
            final byte[] plaintext = cipher.doFinal(decodedValue);
            return new String(plaintext, UTF_8);
        } catch (final UnsupportedEncodingException e) {
            LOGGER.error(UTF_8_NO_LONGER_SUPPORTED, e);
            throw new EncryptionException(CANNOT_HAPPEN_UTF_8, e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String encryptWithPassword(final String toEncrypt, final SecretKey key) throws EncryptionException,
            IllegalBlockSizeException, BadPaddingException {
        LOGGER.info("Started encrypting with password");


        final byte[] ivSpec = new byte[IV_SPEC_SIZE];
        final Random random = new Random(SEED);
        random.nextBytes(ivSpec);

        final int mode = Cipher.ENCRYPT_MODE;
        final Cipher cipher = this.getCipher(key, ivSpec, mode);

        try {
            final byte[] cipherText = cipher.doFinal(toEncrypt.getBytes(UTF_8));
            final byte[] secretString = Base64.encode(cipherText, Base64.DEFAULT);
            return new String(secretString, UTF_8);
        } catch (final UnsupportedEncodingException e) {
            LOGGER.error(UTF_8_NO_LONGER_SUPPORTED, e);
            throw new EncryptionException(CANNOT_HAPPEN_UTF_8, e);
        }
    }

    /**
     * Method that returns a configured {@link Cipher}.<br>
     * It sets the ivSpec and the encryption to
     * PBEWithMD5AndDES
     *
     * @param key    The SecretKey used to encrypt
     * @param ivSpec The initializations vector for the encryption
     * @param mode   Whether we're encrypting or decrypting
     * @return the configured {@link Cipher}.
     */
    private Cipher getCipher(final SecretKey key, final byte[] ivSpec, final int mode) throws EncryptionException {
        LOGGER.info("Getting cipher for decrypting");
        try {
            //Create parameters from the salt and an arbitrary number of iterations:
            final PBEParameterSpec pbeParamSpec = new PBEParameterSpec(ivSpec, 42);

            //Set up the cipher:
            final Cipher cipher = Cipher.getInstance(PBE_WITH_MD5_AND_DES);
            cipher.init(mode, key, pbeParamSpec);
            return cipher;
        } catch (NoSuchAlgorithmException e) {
            LOGGER.error("There isn't an algorithm as PBEWithMD5AndDES", e);
            throw new EncryptionException("Cannot happen, no algorithm as PBEWithMD5AndDES", e);

        } catch (NoSuchPaddingException e) {
            LOGGER.error("There isn't a padding as PBEWithMD5AndDES", e);
            throw new EncryptionException("Cannot happen, no padding as PBEWithMD5AndDES", e);
        } catch (InvalidKeyException e) {
            LOGGER.error("The key is invalid", e);
            throw new EncryptionException("The key was invalid while getting the cipher", e);
        } catch (InvalidAlgorithmParameterException e) {
            LOGGER.error("The algorithm parameter is invalid", e);
            throw new EncryptionException("The algorithm paramater was invalid while initializing the cipher", e);
        }
    }

}
