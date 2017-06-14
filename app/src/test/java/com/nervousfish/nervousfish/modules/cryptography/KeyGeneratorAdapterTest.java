package com.nervousfish.nervousfish.modules.cryptography;

import com.nervousfish.nervousfish.data_objects.Ed25519Key;
import com.nervousfish.nervousfish.data_objects.KeyPair;
import com.nervousfish.nervousfish.data_objects.RSAKey;
import com.nervousfish.nervousfish.service_locator.IServiceLocator;

import org.junit.Test;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import static com.nervousfish.nervousfish.BaseTest.accessConstructor;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

public class KeyGeneratorAdapterTest {

    @Test
    public void generateRSAKeyPairTestCorrectPublicKey() throws KeyGenerationException {
        KeyPair keyPair = ((KeyGeneratorAdapter) accessConstructor(KeyGeneratorAdapter.class, mock(IServiceLocator.class))).generateRSAKeyPair("FTP");
        assertEquals(RSAKey.class, keyPair.getPublicKey().getClass());
    }

    @Test
    public void generateRSAKeyPairTestCorrectPrivateKey() throws KeyGenerationException {
        KeyPair keyPair = ((KeyGeneratorAdapter) accessConstructor(KeyGeneratorAdapter.class, mock(IServiceLocator.class))).generateRSAKeyPair("Webserver");
        assertEquals(RSAKey.class, keyPair.getPrivateKey().getClass());
    }

    @Test
    public void generateRSAKeyPairTestCorrectName() throws KeyGenerationException {
        KeyPair keyPair = ((KeyGeneratorAdapter) accessConstructor(KeyGeneratorAdapter.class, mock(IServiceLocator.class))).generateRSAKeyPair("Webmail");
        assertEquals("Webmail", keyPair.getName());
    }

    @Test
    public void generateEd25519KeyPairTestCorrectPublicKey() throws KeyGenerationException {
        KeyPair keyPair = ((KeyGeneratorAdapter) accessConstructor(KeyGeneratorAdapter.class, mock(IServiceLocator.class))).generateEd25519KeyPair("FTP");
        assertEquals(Ed25519Key.class, keyPair.getPublicKey().getClass());
    }

    @Test
    public void generateEd25519KeyPairTestCorrectPrivateKey() throws KeyGenerationException {
        KeyPair keyPair = ((KeyGeneratorAdapter) accessConstructor(KeyGeneratorAdapter.class, mock(IServiceLocator.class))).generateEd25519KeyPair("Webserver");
        assertEquals(Ed25519Key.class, keyPair.getPrivateKey().getClass());
    }

    @Test
    public void generateEd25519KeyPairTestCorrectName() throws KeyGenerationException {
        KeyPair keyPair = ((KeyGeneratorAdapter) accessConstructor(KeyGeneratorAdapter.class, mock(IServiceLocator.class))).generateEd25519KeyPair("Webmail");
        assertEquals("Webmail", keyPair.getName());
    }

}