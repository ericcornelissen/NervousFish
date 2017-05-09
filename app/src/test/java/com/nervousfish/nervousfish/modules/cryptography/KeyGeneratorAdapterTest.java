package com.nervousfish.nervousfish;

import com.nervousfish.nervousfish.data_objects.KeyPair;
import com.nervousfish.nervousfish.data_objects.RSAKey;
import com.nervousfish.nervousfish.modules.cryptography.KeyGeneratorAdapter;

import org.junit.Test;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import static org.junit.Assert.assertEquals;

/**
 * Test to check the methods from the KeyGeneratorAdapter class.
 */
@SuppressWarnings("PMD")
public class KeyGeneratorAdapterTest {

    @Test
    public void generateRandomKeyPairTest() throws NoSuchAlgorithmException, InvalidKeySpecException {
        KeyPair keyPair = KeyGeneratorAdapter.generateRSAKeyPair();

        assertEquals(RSAKey.class, keyPair.getPublicKey().getClass());
        assertEquals(RSAKey.class, keyPair.getPrivateKey().getClass());
        assertEquals(((RSAKey) keyPair.getPrivateKey()).getKey().split(" ")[0],
                ((RSAKey) keyPair.getPublicKey()).getKey().split(" ")[0]);
    }
}