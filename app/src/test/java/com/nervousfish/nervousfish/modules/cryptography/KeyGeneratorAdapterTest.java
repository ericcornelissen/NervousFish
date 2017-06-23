package com.nervousfish.nervousfish.modules.cryptography;

import com.nervousfish.nervousfish.data_objects.Ed25519PrivateKeyWrapper;
import com.nervousfish.nervousfish.data_objects.AKeyPair;
import com.nervousfish.nervousfish.data_objects.RSAKeyWrapper;
import com.nervousfish.nervousfish.service_locator.IServiceLocator;

import org.junit.Test;

import static com.nervousfish.nervousfish.BaseTest.accessConstructor;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

public class KeyGeneratorAdapterTest {

    @Test
    public void generateRSAKeyPairTestCorrectPublicKey() throws KeyGenerationException {
        AKeyPair keyPair = ((KeyGeneratorAdapter) accessConstructor(KeyGeneratorAdapter.class, mock(IServiceLocator.class))).generateRSAKeyPair("FTP");
        assertEquals(RSAKeyWrapper.class, keyPair.getPublicKey().getClass());
    }

    @Test
    public void generateRSAKeyPairTestCorrectPrivateKey() throws KeyGenerationException {
        AKeyPair keyPair = ((KeyGeneratorAdapter) accessConstructor(KeyGeneratorAdapter.class, mock(IServiceLocator.class))).generateRSAKeyPair("Webserver");
        assertEquals(RSAKeyWrapper.class, keyPair.getPrivateKey().getClass());
    }

    @Test
    public void generateRSAKeyPairTestCorrectName() throws KeyGenerationException {
        AKeyPair keyPair = ((KeyGeneratorAdapter) accessConstructor(KeyGeneratorAdapter.class, mock(IServiceLocator.class))).generateRSAKeyPair("Webmail");
        assertEquals("Webmail", keyPair.getName());
    }

    @Test
    public void generateEd25519KeyPairTestCorrectPublicKey() throws KeyGenerationException {
        AKeyPair keyPair = ((KeyGeneratorAdapter) accessConstructor(KeyGeneratorAdapter.class, mock(IServiceLocator.class))).generateEd25519KeyPair("FTP");
        assertEquals(Ed25519PrivateKeyWrapper.class, keyPair.getPublicKey().getClass());
    }

    @Test
    public void generateEd25519KeyPairTestCorrectPrivateKey() throws KeyGenerationException {
        AKeyPair keyPair = ((KeyGeneratorAdapter) accessConstructor(KeyGeneratorAdapter.class, mock(IServiceLocator.class))).generateEd25519KeyPair("Webserver");
        assertEquals(Ed25519PrivateKeyWrapper.class, keyPair.getPrivateKey().getClass());
    }

    @Test
    public void generateEd25519KeyPairTestCorrectName() throws KeyGenerationException {
        AKeyPair keyPair = ((KeyGeneratorAdapter) accessConstructor(KeyGeneratorAdapter.class, mock(IServiceLocator.class))).generateEd25519KeyPair("Webmail");
        assertEquals("Webmail", keyPair.getName());
    }

}