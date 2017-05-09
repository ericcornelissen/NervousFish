package com.nervousfish.nervousfish.data_objects;

import com.nervousfish.nervousfish.ConstantKeywords;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

public class RSAKeyTest {

    @Test
    public void testCanBeInstantiatedWithArbitraryValues() {
        IKey key = new RSAKey("foo", "bar");
        assertNotNull(key);
    }

    @Test
    public void testGetKeyReturnsNotNull() {
        IKey key = new RSAKey("foo", "bar");
        assertNotNull(key.getKey());
    }

    @Test
    public void testGetKeyReturnsNonEmptyString() {
        IKey key = new RSAKey("foo", "bar");
        assertNotEquals("", key.getKey());
    }

    @Test
    public void testGetTypeReturnsRSA() {
        IKey key = new RSAKey("foo", "bar");
        assertEquals(ConstantKeywords.RSA_KEY, key.getType());
    }

    @Test
    public void testEqualsWorksWithNull() {
        IKey key = new RSAKey("foo", "bar");
        assertFalse(key.equals(null));
    }

    @Test
    public void testEqualsWorksWithArbitraryObject() {
        IKey key = new RSAKey("foo", "bar");
        assertFalse(key.equals("foobar"));
    }

    @Test
    public void testEqualsReturnsFalseForDifferentKeyTypes() {
        IKey keyA = new RSAKey("foo", "bar");
        IKey keyB = new SimpleKey("Hello world!");
        assertFalse(keyA.equals(keyB));
    }

    @Test
    public void testEqualsReturnsFalseForUnequalKeys() {
        IKey keyA = new RSAKey("foo", "bar");
        IKey keyB = new RSAKey("hello", "world");
        assertFalse(keyA.equals(keyB));
    }

    @Test
    public void testEqualsReturnsTrueForEqualKeys() {
        IKey keyA = new RSAKey("foo", "bar");
        IKey keyB = new RSAKey("foo", "bar");
        assertTrue(keyA.equals(keyB));
    }

    @Test
    public void testHashCodeNotNull() {
        IKey key = new RSAKey("foo", "bar");
        assertNotNull(key.hashCode());
    }

}
