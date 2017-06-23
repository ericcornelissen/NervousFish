package com.nervousfish.nervousfish.data_objects;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

public class AKeyPairTest {

    private IKey publicKey;
    private IKey privateKey;

    @Before
    public void setup() {
        this.publicKey = mock(IKey.class);
        this.privateKey = mock(IKey.class);
    }

    @Test
    public void testCanBeInstantiatedWithArbitraryValues() {
        AKeyPair keyPair = new AKeyPair("Webmail", this.publicKey, this.privateKey);
        assertNotNull(keyPair);
    }

    @Test
    public void testGetNameReturnsNotNull() {
        AKeyPair key = new AKeyPair("FTP", this.publicKey, this.privateKey);
        assertNotNull(key.getName());
    }

    @Test
    public void testGetNameReturnsProvidedName() {
        AKeyPair key = new AKeyPair("Webserver", this.publicKey, this.privateKey);
        assertEquals("Webserver", key.getName());
    }

    @Test
    public void testGetPublicKeyReturnsThePublicKey() {
        AKeyPair keyPair = new AKeyPair("FTP", this.publicKey, this.privateKey);
        assertEquals(this.publicKey, keyPair.getPublicKey());
    }

    @Test
    public void testGetPrivateKeyReturnsThePrivateKey() {
        AKeyPair keyPair = new AKeyPair("Webmail", this.publicKey, this.privateKey);
        assertEquals(this.privateKey, keyPair.getPrivateKey());
    }

    @Test
    public void testEqualsWorksWithNull() {
        AKeyPair keyPair = new AKeyPair("Webserver", this.publicKey, this.privateKey);
        assertFalse(keyPair.equals(null));
    }

    @Test
    public void testEqualsWorksWithArbitraryObject() {
        AKeyPair keyPair = new AKeyPair("Webmail", this.publicKey, this.privateKey);
        assertFalse(keyPair.equals("foobar"));
    }

    @Test
    public void testEqualsReturnsFalseForUnequalKeys() {
        AKeyPair keyPairA = new AKeyPair("FTP", this.publicKey, this.privateKey);
        AKeyPair keyPairB = new AKeyPair("Webmail", mock(IKey.class), mock(IKey.class));
        assertFalse(keyPairA.equals(keyPairB));
    }

    @Test
    public void testEqualsReturnsTrueForEqualKeys() {
        AKeyPair keyPairA = new AKeyPair("Webmail", this.publicKey, this.privateKey);
        AKeyPair keyPairB = new AKeyPair("Webmail", this.publicKey, this.privateKey);
        assertTrue(keyPairA.equals(keyPairB));
    }

    @Test
    public void testHashCodeNotNull() {
        AKeyPair keyPair = new AKeyPair("Webserver", this.publicKey, this.privateKey);
        assertNotNull(keyPair.hashCode());
    }

}
