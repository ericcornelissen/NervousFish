package com.nervousfish.nervousfish.modules.cryptography;

import com.nervousfish.nervousfish.exceptions.EncryptionException;
import com.nervousfish.nervousfish.modules.IModule;

import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.SecretKey;

/**
 * Defines a module used for encrypting / decrypting messages that can be used by a service locator.
 */

public interface IEncryptor extends IModule {

    /**
     * Hashes a given pass to an encrypted string that can't be decrypted.
     * @param pass  THe string to encrypt
     * @return  The encrypted string.
     */
    String hashWithoutSalt(String pass) throws EncryptionException;

    /**
     * Make a key for encryption based on a password.
     * @param password The password to make the key with
     * @return The SecretKey based on the password.
     */
    SecretKey makeKey(String password) throws InvalidKeySpecException, EncryptionException;


    /**
     * Encrypts or decrypts a string with a password to
     * @param toEncrypt The string to be encrypted/decrypted
     * @param key  The secret key made from the password.
     * @param encrypt   whether we're encrypting or decrypting.
     * @return  The encrypted/decrypted bytearray.
     */
    String encryptOrDecryptWithPassword(String toEncrypt, SecretKey key, boolean encrypt)
            throws IllegalBlockSizeException, BadPaddingException, EncryptionException;
}
