package com.nervousfish.nervousfish.data_objects;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

public class ProfileTest {

    private KeyPair keyPair;

    @Before
    public void setup() {
        this.keyPair = new KeyPair(mock(IKey.class), mock(IKey.class));
    }

    @Test
    public void testCanBeInstantiatedWithArbitraryValues() {
        Profile profile = new Profile("name", this.keyPair);
        assertNotNull(profile);
    }

    @Test
    public void testGetNameReturnsTheName() {
        Profile profileA = new Profile("foo", this.keyPair);
        assertEquals("foo", profileA.getName());

        Profile profileB = new Profile("bar", this.keyPair);
        assertEquals("bar", profileB.getName());
    }

    @Test
    public void testGetPublicKeyReturnsThePublicKey() {
        Profile profile = new Profile("foo", this.keyPair);
        assertEquals(this.keyPair.getPublicKey(), profile.getPublicKey());
    }

    @Test
    public void testGetPrivateKeyReturnsThePrivateKey() {
        Profile profile = new Profile("foo", this.keyPair);
        assertEquals(this.keyPair.getPrivateKey(), profile.getPrivateKey());
    }

    @Test
    public void testEqualsWorksWithNull() {
        Profile profile = new Profile("foo", this.keyPair);
        assertFalse(profile.equals(null));
    }

    @Test
    public void testEqualsWorksWithArbitraryObject() {
        Profile profile = new Profile("foo", this.keyPair);
        assertFalse(profile.equals("foobar"));
    }

    @Test
    public void testEqualsReturnsFalseForUnequalKeys() {
        Profile profileA = new Profile("Eric", this.keyPair);
        Profile profileB = new Profile("Kilian", this.keyPair);
        assertFalse(profileA.equals(profileB));
    }

    @Test
    public void testEqualsReturnsTrueForEqualKeys() {
        Profile profileA = new Profile("Cornel", this.keyPair);
        Profile profileB = new Profile("Cornel", this.keyPair);
        assertTrue(profileA.equals(profileB));
    }

    @Test
    public void testHashCodeNotNull() {
        Profile profile = new Profile("Stas", this.keyPair);
        assertNotNull(profile.hashCode());
    }

}
