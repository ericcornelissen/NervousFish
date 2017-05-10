package com.nervousfish.nervousfish.data_objects;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

public class ProfileTest {

    private IKey publicKey;
    private IKey privateKey;

    @Before
    public void setup() {
        this.publicKey = mock(IKey.class);
        this.privateKey = mock(IKey.class);
    }

    @Test
    public void testCanBeInstantiatedWithArbitraryValues() {
        Profile profile = new Profile("name", this.publicKey, this.privateKey);
        assertNotNull(profile);
    }

    @Test
    public void testGetNameReturnsTheName() {
        Profile profileA = new Profile("foo", this.publicKey, this.privateKey);
        assertEquals("foo", profileA.getName());

        Profile profileB = new Profile("bar", this.publicKey, this.privateKey);
        assertEquals("bar", profileB.getName());
    }

    @Test
    public void testGetPublicKeyReturnsThePublicKey() {
        Profile profile = new Profile("foo", this.publicKey, this.privateKey);
        assertEquals(this.publicKey, profile.getPublicKey());
    }

    @Test
    public void testGetPrivateKeyReturnsThePrivateKey() {
        Profile profile = new Profile("foo", this.publicKey, this.privateKey);
        assertEquals(this.privateKey, profile.getPrivateKey());
    }

    @Test
    public void testEqualsWorksWithNull() {
        Profile profile = new Profile("foo", this.publicKey, this.privateKey);
        assertFalse(profile.equals(null));
    }

    @Test
    public void testEqualsWorksWithArbitraryObject() {
        Profile profile = new Profile("foo", this.publicKey, this.privateKey);
        assertFalse(profile.equals("foobar"));
    }

    @Test
    public void testEqualsReturnsFalseForUnequalKeys() {
        Profile profileA = new Profile("Eric", this.publicKey, this.privateKey);
        Profile profileB = new Profile("Kilian", this.publicKey, this.privateKey);
        assertFalse(profileA.equals(profileB));
    }

    @Test
    public void testEqualsReturnsTrueForEqualKeys() {
        Profile profileA = new Profile("Cornel", this.publicKey, this.privateKey);
        Profile profileB = new Profile("Cornel", this.publicKey, this.privateKey);
        assertTrue(profileA.equals(profileB));
    }

    @Test
    public void testHashCodeNotNull() {
        Profile profile = new Profile("Stas", this.publicKey, this.privateKey);
        assertNotNull(profile.hashCode());
    }

}
