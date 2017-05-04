package com.nervousfish.nervousfish;

import com.nervousfish.nervousfish.modules.cryptography.KeyGeneratorAdapter;

import org.junit.Test;

import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

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