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
     * Generates a salt bytearray based on a seed and with a given length.
     * @param seed  The seed to keep the salt consistent.
     * @param length The length the salt should be.
     * @return  The salt bytearray.
     */
    /*
    public static byte[] generateSalt(int seed, int length) {
        final Random random = new Random(seed);
        byte bytes[] = new byte[length];
        random.nextBytes(bytes);
        LOGGER.info("Generated salt bytestring");
        return bytes;
    }
    */

    /**
     * {@inheritDoc}
     */
    @Override
    public String hashWithoutSalt(final String pass) throws EncryptionException {
        try {
            final MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.reset();
            final byte[] hash = digest.digest(pass.getBytes(StandardCharsets.UTF_8));
            LOGGER.info("Hashed the pass with SHA-256, no salt");
            return new String(hash, UTF_8);
        } catch (final NoSuchAlgorithmException e) {
            LOGGER.error("SHA-256 is not a valid encryption algorithm", e);
            throw new EncryptionException("Cannot happen, SHA-256 is not a valid encryption algorithm", e);
        } catch (final UnsupportedEncodingException e) {
            LOGGER.error("UTF-8 is not a valid encoding method", e);
            throw new EncryptionException("Cannot happen, UTF-8 is not a valid encoding method", e);
        }
    }

    /**
     * Hashes a pass to an encrypted string with a salt bytestring.
     * @param salt The salt bytestring to encrypt the pass with.
     * @param pass  The string to encrypt
     * @return The encrypted string.
     */
    /*
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
    */

    /**
     * {@inheritDoc}
     */
    @Override
    public SecretKey makeKey(final String password) throws InvalidKeySpecException, EncryptionException {
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
    public String encryptOrDecryptWithPassword(final String toEncrypt, final SecretKey key, final boolean encrypt)
            throws IllegalBlockSizeException, BadPaddingException, EncryptionException {
        LOGGER.info("Started encrypting with password");

        final byte[] ivSpec = new byte[IV_SPEC_SIZE];
        final Random random = new Random(SEED);
        random.nextBytes(ivSpec);

        final int mode = encrypt ? Cipher.ENCRYPT_MODE : Cipher.DECRYPT_MODE;
        final Cipher cipher = getCipher(key, ivSpec, mode);

        try {
            if (encrypt) {
                final byte[] cipherText = cipher.doFinal(toEncrypt.getBytes(UTF_8));
                final byte[] secretString = Base64.encode(cipherText, Base64.DEFAULT);
                return new String(secretString, UTF_8);
            } else {
                final byte[] decodedValue = Base64.decode(toEncrypt, Base64.DEFAULT);
                final byte[] plaintext = cipher.doFinal(decodedValue);
                return new String(plaintext, UTF_8);
            }
        } catch (final UnsupportedEncodingException e) {
            LOGGER.error("UTF-8 is no longer an encoding algorithm", e);
            throw new EncryptionException("Cannot happen, no encodign algorithm like UTF-8", e);
        }
    }




    /**
     * Method that return a configured {@link Cipher}.<br>
     * It set the ivSpec and the encryption to
     * PBEWithMD5AndDES
     *
     * @param key   The SecretKey used to encrypt
     * @param ivSpec    The initializations vector for the encryption
     * @param mode  Whether we're encrypting or decrypting
     * @return  the configured Cipher.
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

    /**
     * Encrypt data using a public key.
     * @param text  -   The data to encrypt
     * @param publicKey -   The public key to encrypt the data with
     * @return  The encrypted data in String.
     * @throws IOException  -   Throws an IOException when the key can't be split up correctly
     */
    /*
    public static String encryptUsingRSA(final String text, final IKey publicKey) throws IOException, InvalidKeySpecException,
            InvalidKeyException, IllegalBlockSizeException {
        final String[] keyComponents = publicKey.getKey().split(" ");
        if (keyComponents.length > 2) {
            throw new IOException("Public key can't be split up correctly");
        }
        try {
            final RSAPublicKeySpec spec = new RSAPublicKeySpec(new BigInteger(keyComponents[0]), new BigInteger(keyComponents[1]));

            final KeyFactory factory = KeyFactory.getInstance("RSA");
            final PublicKey pub = factory.generatePublic(spec);

            final Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, pub);
            final byte[] cipherText = cipher.doFinal(text.getBytes());
            return new String(cipherText);
        } catch (NoSuchAlgorithmException e) {
            LOGGER.error("Error in generating rsa key factory from components", e);
        } catch (NoSuchPaddingException e) {
            LOGGER.error(NO_SUCH_PADDING, e);
        } catch (BadPaddingException e) {
            LOGGER.error(BAD_PADDING);
        }
        return null;

    }
    */

    /**
     * Decrypts data using a private key.
     * @param text  -   The data to decrypt.
     * @param privateKey    -   The private key to decrypt the data with.
     * @return  The decrypted data in String
     * @throws IOException  -   Throws an IOException when the key can't be split up correctly
     */
    /*
    public static String decryptUsingRSA(final String text, final IKey privateKey) throws IOException, InvalidKeySpecException,
            InvalidKeyException, IllegalBlockSizeException {
        final String[] keyComponents = privateKey.getKey().split(" ");
        if (keyComponents.length > 2) {
            throw new IOException("Private key can't be split up correctly");
        }
        try {
            final RSAPrivateKeySpec spec = new RSAPrivateKeySpec(new BigInteger(keyComponents[0]), new BigInteger(keyComponents[1]));
            final KeyFactory factory = KeyFactory.getInstance("RSA");
            final PrivateKey priv = factory.generatePrivate(spec);
            final Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, priv);
            final byte[] decryptedData = cipher.doFinal(text.getBytes());
            return new String(decryptedData);
        } catch (NoSuchAlgorithmException e) {
            LOGGER.error("Error in generating rsa key factory from components", e);
        } catch (NoSuchPaddingException e) {
            LOGGER.error("Error in padding encryption", e);
        } catch (BadPaddingException e) {
            LOGGER.error("Error in the padding of the string");
        }
        return null;
    }
    */


}
