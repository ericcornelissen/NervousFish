package com.nervousfish.nervousfish.data_objects;

import com.nervousfish.nervousfish.ConstantKeywords;

import org.junit.Test;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

public class RSAKeyTest {

    @Test
    public void testCanBeInstantiatedWithArbitraryValues() {
        IKey key = new RSAKey("Webmail", "foo", "bar");
        assertNotNull(key);
    }

    @Test
    public void testCanBeInstantiatedWithMap() {
        Map<String, String> map = new ConcurrentHashMap<>();
        map.put("name", "name");
        map.put("modulus", "modulus");
        map.put("exponent", "exponent");
        IKey key = new RSAKey(map);
        assertNotNull(key);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testCanBeInstantiatedWithMapMustHaveAllFields() {
        Map<String, String> map = new ConcurrentHashMap<>();
        new RSAKey(map);
    }

    @Test
    public void testGetNameReturnsNotNull() {
        IKey key = new RSAKey("FTP", "foo", "bar");
        assertNotNull(key.getName());
    }

    @Test
    public void testGetNameReturnsProvidedName() {
        IKey key = new RSAKey("Webserver", "foo", "bar");
        assertEquals("Webserver", key.getName());
    }

    @Test
    public void testGetKeyReturnsNotNull() {
        IKey key = new RSAKey("FTP", "foo", "bar");
        assertNotNull(key.getKey());
    }

    @Test
    public void testGetKeyReturnsNonEmptyString() {
        IKey key = new RSAKey("Webmail", "foo", "bar");
        assertNotEquals("", key.getKey());
    }

    @Test
    public void testGetTypeReturnsRSA() {
        IKey key = new RSAKey("Personal", "foo", "bar");
        assertEquals(ConstantKeywords.RSA_KEY, key.getType());
    }

    @Test
    public void testEqualsWorksWithNull() {
        IKey key = new RSAKey("Webmail", "foo", "bar");
        assertFalse(key.equals(null));
    }

    @Test
    public void testEqualsWorksWithArbitraryObject() {
        IKey key = new RSAKey("FTP", "foo", "bar");
        assertFalse(key.equals("foobar"));
    }

    @Test
    public void testEqualsReturnsFalseForDifferentKeyTypes() {
        IKey keyA = new RSAKey("Personal", "foo", "bar");
        IKey keyB = new SimpleKey("Computer", "Hello world!");
        assertFalse(keyA.equals(keyB));
    }

    @Test
    public void testEqualsReturnsFalseForUnequalKeys() {
        IKey keyA = new RSAKey("Personal", "foo", "bar");
        IKey keyB = new RSAKey("Email", "hello", "world");
        assertFalse(keyA.equals(keyB));
    }

    @Test
    public void testEqualsReturnsTrueForEqualKeys() {
        IKey keyA = new RSAKey("FTP", "foo", "bar");
        IKey keyB = new RSAKey("FTP", "foo", "bar");
        assertTrue(keyA.equals(keyB));
    }

    @Test
    public void testHashCodeNotNull() {
        IKey key = new RSAKey("Webmail", "foo", "bar");
        assertNotNull(key.hashCode());
    }

    @Test
    public void testStaticGetModules() {
        IKey key = new RSAKey("Webmail", "foo", "bar");
        assertEquals("foo", RSAKey.getModulus(key));
    }

    @Test(expected=IllegalArgumentException.class)
    public void testStaticGetModulesFailsOnNonRSAKey() {
        IKey key = new SimpleKey("FTP", "foo");
        RSAKey.getModulus(key);
    }

    @Test
    public void testStaticGetExponent() {
        IKey key = new RSAKey("Webmail", "foo", "bar");
        assertEquals("bar", RSAKey.getExponent(key));
    }

    @Test(expected=IllegalArgumentException.class)
    public void testStaticGetExponentFailsOnNonRSAKey() {
        IKey key = new SimpleKey("Webmail", "bar");
        RSAKey.getExponent(key);
    }

}
