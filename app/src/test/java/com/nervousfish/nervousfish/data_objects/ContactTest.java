package com.nervousfish.nervousfish.data_objects;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

public class ContactTest {

    private IKey key;

    @Before
    public void setup() {
        this.key = mock(IKey.class);
    }

    @Test
    public void testCanBeInstantiatedWithArbitraryValues() {
        Contact contact = new Contact("name", this.key);
        assertNotNull(contact);
    }

    @Test
    public void testGetNameReturnsTheName() {
        Contact contactA = new Contact("foo", this.key);
        assertEquals("foo", contactA.getName());

        Contact contactB = new Contact("bar", this.key);
        assertEquals("bar", contactB.getName());
    }

    @Test
    public void testGetPublicKeyReturnsThePublicKey() {
        Contact contact = new Contact("foo", this.key);
        assertEquals(this.key, contact.getPublicKey());
    }

    @Test
    public void testEqualsWorksWithNull() {
        Contact contact = new Contact("foo", this.key);
        assertFalse(contact.equals(null));
    }

    @Test
    public void testEqualsWorksWithArbitraryObject() {
        Contact contact = new Contact("foo", this.key);
        assertFalse(contact.equals("foobar"));
    }

    @Test
    public void testEqualsReturnsFalseForUnequalKeys() {
        Contact contactA = new Contact("Joost", this.key);
        Contact contactB = new Contact("Cornel", this.key);
        assertFalse(contactA.equals(contactB));
    }

    @Test
    public void testEqualsReturnsTrueForEqualKeys() {
        Contact contactA = new Contact("Stas", this.key);
        Contact contactB = new Contact("Stas", this.key);
        assertTrue(contactA.equals(contactB));
    }

    @Test
    public void testHashCodeNotNull() {
        Contact contact = new Contact("Kilian", this.key);
        assertNotNull(contact.hashCode());
    }

}
