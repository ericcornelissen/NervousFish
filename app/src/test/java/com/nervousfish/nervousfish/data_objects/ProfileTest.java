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
        Contact contact = new Contact("name", new ArrayList<IKey>());
        Profile profile = new Profile(contact, new ArrayList<KeyPair>());
        profile.addKeyPair(this.keyPair);
        assertNotNull(profile);
    }

    @Test
    public void testGetNameReturnsTheName() {
        Contact contactA = new Contact("name", new ArrayList<IKey>());
        Profile profileA = new Profile(contactA, new ArrayList<KeyPair>());
        profileA.addKeyPair(this.keyPair);
        assertEquals("name", profileA.getName());

        Contact contactB = new Contact("bar", new ArrayList<IKey>());
        Profile profileB = new Profile(contactB, new ArrayList<KeyPair>());
        profileB.addKeyPair(this.keyPair);
        assertEquals("bar", profileB.getName());
    }

    @Test
    public void testGetKeyPairReturnsTheKeyPair() {
        Contact contact = new Contact("name", new ArrayList<IKey>());
        Profile profile = new Profile(contact, new ArrayList<KeyPair>());
        profile.addKeyPair(this.keyPair);
        ArrayList<KeyPair> test = new ArrayList<>();
        test.add(this.keyPair);
        assertEquals(test.get(0), profile.getKeyPairs().get(0));
        assertEquals(test.size(), profile.getKeyPairs().size());

    }

    @Test
    public void testGetPublicKeyReturnsThePublicKey() {
        Contact contact = new Contact("name", new ArrayList<IKey>());
        Profile profile = new Profile(contact, new ArrayList<KeyPair>());
        profile.addKeyPair(this.keyPair);
        assertEquals(this.keyPair.getPublicKey(), profile.getPublicKey());
    }

    @Test
    public void testGetPrivateKeyReturnsThePrivateKey() {
        Contact contact = new Contact("name", new ArrayList<IKey>());
        Profile profile = new Profile(contact, new ArrayList<KeyPair>());
        profile.addKeyPair(this.keyPair);
        assertEquals(this.keyPair.getPrivateKey(), profile.getKeyPairs().get(0).getPrivateKey());
    }

    @Test
    public void testEqualsWorksWithNull() {
        Contact contact = new Contact("name", new ArrayList<IKey>());
        Profile profile = new Profile(contact, new ArrayList<KeyPair>());
        profile.addKeyPair(this.keyPair);
        assertFalse(profile.equals(null));
    }

    @Test
    public void testEqualsWorksWithArbitraryObject() {
        Contact contact = new Contact("name", new ArrayList<IKey>());
        Profile profile = new Profile(contact, new ArrayList<KeyPair>());
        profile.addKeyPair(this.keyPair);
        assertFalse(profile.equals("foobar"));
    }

    @Test
    public void testEqualsReturnsFalseForUnequalKeys() {
        Contact contactA = new Contact("Eric", new ArrayList<IKey>());
        Profile profileA = new Profile(contactA, new ArrayList<KeyPair>());
        profileA.addKeyPair(this.keyPair);

        Contact contactB = new Contact("Kilian", new ArrayList<IKey>());
        Profile profileB = new Profile(contactB, new ArrayList<KeyPair>());
        profileB.addKeyPair(this.keyPair);
        assertFalse(profileA.equals(profileB));
    }

    @Test
    public void testEqualsReturnsTrueForEqualKeys() {
        Contact contactA = new Contact("Cornel", new ArrayList<IKey>());
        Profile profileA = new Profile(contactA, new ArrayList<KeyPair>());
        profileA.addKeyPair(this.keyPair);
        Contact contactB = new Contact("Cornel", new ArrayList<IKey>());
        Profile profileB = new Profile(contactB, new ArrayList<KeyPair>());
        profileB.addKeyPair(this.keyPair);
        assertTrue(profileA.equals(profileB));
    }

    @Test
    public void testHashCodeNotNull() {
        Contact contact = new Contact("Stas", new ArrayList<IKey>());
        Profile profile = new Profile(contact, new ArrayList<KeyPair>());
        profile.addKeyPair(this.keyPair);
        assertNotNull(profile.hashCode());
    }

}
