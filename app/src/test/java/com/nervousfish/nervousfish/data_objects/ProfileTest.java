package com.nervousfish.nervousfish.data_objects;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

public class ProfileTest {

    private KeyPair keyPair;

    @Before
    public void setup() {
        this.keyPair = new KeyPair("Webserver", mock(IKey.class), mock(IKey.class));
    }

    @Test
    public void testCanBeInstantiatedWithArbitraryValues() {
        Profile profile = new Profile("name", new ArrayList<KeyPair>());
        profile.addKeyPair(this.keyPair);
        assertNotNull(profile);
    }

    @Test
    public void testGetNameReturnsTheName() {
        Profile profileA = new Profile("name", new ArrayList<KeyPair>());
        profileA.addKeyPair(this.keyPair);
        assertEquals("name", profileA.getName());

        Profile profileB = new Profile("bar", new ArrayList<KeyPair>());
        profileB.addKeyPair(this.keyPair);
        assertEquals("bar", profileB.getName());
    }

    @Test
    public void testGetKeyPairReturnsTheKeyPair() {
        Profile profile = new Profile("name", new ArrayList<KeyPair>());
        profile.addKeyPair(this.keyPair);
        ArrayList<KeyPair> test = new ArrayList<>();
        test.add(this.keyPair);
        assertEquals(test.get(0), profile.getKeyPairs().get(0));
        assertEquals(test.size(), profile.getKeyPairs().size());

    }

    @Test
    public void testGetPublicKeyReturnsThePublicKey() {
        Profile profile = new Profile("name", new ArrayList<KeyPair>());
        profile.addKeyPair(this.keyPair);
        assertEquals(this.keyPair.getPublicKey(), profile.getPublicKey());
    }

    @Test
    public void testGetPrivateKeyReturnsThePrivateKey() {
        Profile profile = new Profile("name", new ArrayList<KeyPair>());
        profile.addKeyPair(this.keyPair);
        assertEquals(this.keyPair.getPrivateKey(), profile.getKeyPairs().get(0).getPrivateKey());
    }

    @Test
    public void testEqualsWorksWithNull() {
        Profile profile = new Profile("name", new ArrayList<KeyPair>());
        profile.addKeyPair(this.keyPair);
        assertFalse(profile.equals(null));
    }

    @Test
    public void testEqualsWorksWithArbitraryObject() {
        Profile profile = new Profile("name", new ArrayList<KeyPair>());
        profile.addKeyPair(this.keyPair);
        assertFalse(profile.equals("foobar"));
    }

    @Test
    public void testEqualsReturnsFalseForUnequalKeys() {
        Profile profileA = new Profile("Eric", new ArrayList<KeyPair>());
        profileA.addKeyPair(this.keyPair);

        Profile profileB = new Profile("Kilian", new ArrayList<KeyPair>());
        profileB.addKeyPair(this.keyPair);
        assertFalse(profileA.equals(profileB));
    }

    @Test
    public void testEqualsReturnsTrueForEqualKeys() {
        Profile profileA = new Profile("Cornel", new ArrayList<KeyPair>());
        profileA.addKeyPair(this.keyPair);
        Profile profileB = new Profile("Cornel", new ArrayList<KeyPair>());
        profileB.addKeyPair(this.keyPair);
        assertTrue(profileA.equals(profileB));
    }

    @Test
    public void testHashCodeNotNull() {
        Profile profile = new Profile("Stas", new ArrayList<KeyPair>());
        profile.addKeyPair(this.keyPair);
        assertNotNull(profile.hashCode());
    }

}
