package com.nervousfish.nervousfish.data_objects;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

public class KeyPairTest {

    private IKey publicKey;
    private IKey privateKey;

    @Before
    public void setup() {
        this.publicKey = mock(IKey.class);
        this.privateKey = mock(IKey.class);
    }

    @Test
    public void testCanBeInstantiatedWithArbitraryValues() {
        KeyPair keyPair = new KeyPair("Webmail", this.publicKey, this.privateKey);
        assertNotNull(keyPair);
    }

    @Test
    public void testGetPublicKeyReturnsThePublicKey() {
        KeyPair keyPair = new KeyPair("FTP", this.publicKey, this.privateKey);
        assertEquals(this.publicKey, keyPair.getPublicKey());
    }

    @Test
    public void testGetPrivateKeyReturnsThePrivateKey() {
        KeyPair keyPair = new KeyPair("Webmail", this.publicKey, this.privateKey);
        assertEquals(this.privateKey, keyPair.getPrivateKey());
    }

    @Test
    public void testEqualsWorksWithNull() {
        KeyPair keyPair = new KeyPair("Webserver", this.publicKey, this.privateKey);
        assertFalse(keyPair.equals(null));
    }

    @Test
    public void testEqualsWorksWithArbitraryObject() {
        KeyPair keyPair = new KeyPair("Webmail", this.publicKey, this.privateKey);
        assertFalse(keyPair.equals("foobar"));
    }

    @Test
    public void testEqualsReturnsFalseForUnequalKeys() {
        KeyPair keyPairA = new KeyPair("FTP", this.publicKey, this.privateKey);
        KeyPair keyPairB = new KeyPair("Webmail", mock(IKey.class), mock(IKey.class));
        assertFalse(keyPairA.equals(keyPairB));
    }

    @Test
    public void testEqualsReturnsTrueForEqualKeys() {
        KeyPair keyPairA = new KeyPair("Webmail", this.publicKey, this.privateKey);
        KeyPair keyPairB = new KeyPair("Webmail", this.publicKey, this.privateKey);
        assertTrue(keyPairA.equals(keyPairB));
    }

    @Test
    public void testHashCodeNotNull() {
        KeyPair keyPair = new KeyPair("Webserver", this.publicKey, this.privateKey);
        assertNotNull(keyPair.hashCode());
    }

}
