package com.nervousfish.nervousfish.modules.cryptography;

import com.nervousfish.nervousfish.data_objects.KeyPair;
import com.nervousfish.nervousfish.data_objects.RSAKey;
import com.nervousfish.nervousfish.service_locator.IServiceLocator;

import org.junit.Test;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import static com.nervousfish.nervousfish.BaseTest.accessConstructor;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

/**
 * Test to check the methods from the KeyGeneratorAdapter class.
 */
public class KeyGeneratorAdapterTest {

    @Test
    public void generateRandomKeyPairTest() throws KeyGenerationException {
        KeyPair keyPair = ((KeyGeneratorAdapter) accessConstructor(KeyGeneratorAdapter.class, mock(IServiceLocator.class))).generateRSAKeyPair("FTP");

        assertEquals(RSAKey.class, keyPair.getPublicKey().getClass());
        assertEquals(RSAKey.class, keyPair.getPrivateKey().getClass());
        assertEquals(keyPair.getPrivateKey().getKey().split(" ")[0],
                keyPair.getPublicKey().getKey().split(" ")[0]);
    }

}