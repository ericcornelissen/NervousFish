package com.nervousfish.nervousfish;

import com.nervousfish.nervousfish.modules.cryptography.KeyGeneratorAdapter;

import org.junit.Test;

import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * Test to check the methods from the KeyGeneratorAdapter class.
 */
@SuppressWarnings("PMD")
public class KeyGeneratorAdapterTest {

    @Test
    public void generateRandomKeyPairTest() throws NoSuchAlgorithmException {
        KeyPair keyPair = KeyGeneratorAdapter.generateRandomKeyPair();

        assertEquals("RSA", keyPair.getPublic().getAlgorithm());
        assertFalse(keyPair.getPublic() == null);
        assertFalse(keyPair.getPrivate() == null);
    }

    @Test
    public void getPublicKeySpecTest() throws NoSuchAlgorithmException, InvalidKeySpecException {
        KeyPair keyPair = KeyGeneratorAdapter.generateRandomKeyPair();

        KeySpec ks = KeyGeneratorAdapter.getPublicKeySpec(keyPair);
        assertEquals(RSAPublicKeySpec.class, ks.getClass());
    }

    @Test
    public void getPrivateKeySpecTest() throws NoSuchAlgorithmException, InvalidKeySpecException {
        KeyPair keyPair = KeyGeneratorAdapter.generateRandomKeyPair();

        KeySpec ks = KeyGeneratorAdapter.getPrivateKeySpec(keyPair);
        assertEquals(RSAPrivateKeySpec.class, ks.getClass());
    }

    @Test(expected = InvalidKeySpecException.class)
    public void getPublicKeySpecInvalidKeyTest() throws NoSuchAlgorithmException, InvalidKeySpecException {
        KeyPair keyPair = new KeyPair(null, null);

        KeyGeneratorAdapter.getPublicKeySpec(keyPair);
    }

    @Test(expected = InvalidKeySpecException.class)
    public void getPrivateKeySpecInvalidKeyTest() throws NoSuchAlgorithmException, InvalidKeySpecException {
        KeyPair keyPair = new KeyPair(null, null);

        KeyGeneratorAdapter.getPrivateKeySpec(keyPair);
    }
}