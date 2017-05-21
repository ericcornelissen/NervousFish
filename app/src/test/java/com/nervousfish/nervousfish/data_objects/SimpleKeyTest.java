package com.nervousfish.nervousfish.data_objects;

import org.junit.Test;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class SimpleKeyTest {

    @Test
    public void testCanBeInstantiatedWithArbitraryValues() {
        IKey key = new SimpleKey("Webmail", "foobar");
        assertNotNull(key);
    }

    @Test
    public void testCanBeInstantiatedWithMap() {
        Map<String, String> map = new ConcurrentHashMap<>();
        map.put("name", "name");
        map.put("key", "key");
        IKey key = new SimpleKey(map);
        assertNotNull(key);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCanBeInstantiatedWithMapMustHaveAllFields() {
        Map<String, String> map = new ConcurrentHashMap<>();
        new SimpleKey(map);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNameNull() {
        Map<String, String> map = new ConcurrentHashMap<>();
        map.put("name", "foo");
        new SimpleKey(map);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testKeyNull() {
        Map<String, String> map = new ConcurrentHashMap<>();
        map.put("key", "foo");
        new SimpleKey(map);
    }

    @Test
    public void testGetNameReturnsNotNull() {
        IKey key = new SimpleKey("FTP", "foobar");
        assertNotNull(key.getName());
    }

    @Test
    public void testGetNameReturnsProvidedName() {
        IKey key = new SimpleKey("Webserver", "foobar");
        assertEquals("Webserver", key.getName());
    }

    @Test
    public void testGetKeyReturnsNotNull() {
        IKey key = new SimpleKey("FTP", "foobar");
        assertNotNull(key.getKey());
    }

    @Test
    public void testGetKeyReturnsNonEmptyString() {
        IKey key = new SimpleKey("Webmail", "foobar");
        assertNotEquals("", key.getKey());
    }

    @Test
    public void testGetTypeReturnsSimple() {
        IKey key = new SimpleKey("Webserver", "foobar");
        assertEquals("simple", key.getType());
    }

    @Test
    public void testEqualsWorksWithNull() {
        IKey key = new SimpleKey("Webmail", "foobar");
        assertFalse(key.equals(null));
    }

    @Test
    public void testEqualsWorksWithArbitraryObject() {
        IKey key = new SimpleKey("FTP", "foobar");
        assertFalse(key.equals("foobar"));
    }

    @Test
    public void testEqualsReturnsFalseForDifferentKeyTypes() {
        IKey keyA = new SimpleKey("Webserver", "Hello world!");
        IKey keyB = new RSAKey("Webmail", "foo", "bar");
        assertFalse(keyA.equals(keyB));
    }

    @Test
    public void testEqualsReturnsFalseForUnequalKeys() {
        IKey keyA = new SimpleKey("FTP", "foobar");
        IKey keyB = new SimpleKey("Zoidberg", "Hello world!");
        assertFalse(keyA.equals(keyB));
    }

    @Test
    public void testEqualsReturnsTrueForEqualKeys() {
        IKey keyA = new SimpleKey("Webmail", "foobar");
        IKey keyB = new SimpleKey("Webmail", "foobar");
        assertTrue(keyA.equals(keyB));
    }

    @Test
    public void testHashCodeNotNull() {
        IKey key = new SimpleKey("Webmail", "foobar");
        assertNotNull(key.hashCode());
    }

}
