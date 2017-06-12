package com.nervousfish.nervousfish.data_objects;

import com.google.gson.stream.JsonWriter;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class Ed25519KeyTest {

    @Test
    public void testCanBeInstantiatedWithArbitraryValues() {
        IKey key = new Ed25519Key("Webmail", "foobar");
        assertNotNull(key);
    }

    @Test
    public void testCanBeInstantiatedWithMap() {
        Map<String, String> map = new ConcurrentHashMap<>();
        map.put("name", "name");
        map.put("key", "key");
        IKey key = new Ed25519Key(map);
        assertNotNull(key);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCanBeInstantiatedWithMapMustHaveAllFields() {
        Map<String, String> map = new ConcurrentHashMap<>();
        new Ed25519Key(map);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testKeyNull() {
        Map<String, String> map = new ConcurrentHashMap<>();
        map.put("name", "foo");
        new Ed25519Key(map);
    }

    @Test
    public void testGetNameReturnsProvidedName() {
        IKey key = new Ed25519Key("Webserver", "foobar");
        assertEquals("Webserver", key.getName());
    }

    @Test
    public void testGetKeyReturnsNonEmptyString() {
        IKey key = new Ed25519Key("Webmail", "foobar");
        assertNotEquals("", key.getKey());
    }

    @Test
    public void testGetTypeReturnsSimple() {
        IKey key = new Ed25519Key("Webserver", "foobar");
        assertEquals("simple", key.getType());
    }

    @Test
    public void testEqualsWorksWithNull() {
        IKey key = new Ed25519Key("Webmail", "foobar");
        assertFalse(key.equals(null));
    }

    @Test
    public void testEqualsWorksWithArbitraryObject() {
        IKey key = new Ed25519Key("FTP", "foobar");
        assertFalse(key.equals("foobar"));
    }

    @Test
    public void testEqualsReturnsFalseForDifferentKeyTypes() {
        IKey keyA = new Ed25519Key("Webserver", "Hello world!");
        IKey keyB = new RSAKey("Webmail", "foo", "bar");
        assertFalse(keyA.equals(keyB));
    }

    @Test
    public void testEqualsReturnsFalseForUnequalKeys() {
        IKey keyA = new Ed25519Key("FTP", "foobar");
        IKey keyB = new Ed25519Key("Zoidberg", "Hello world!");
        assertFalse(keyA.equals(keyB));
    }

    @Test
    public void testEqualsReturnsTrueForEqualKeys() {
        IKey keyA = new Ed25519Key("Webmail", "foobar");
        IKey keyB = new Ed25519Key("Webmail", "foobar");
        assertTrue(keyA.equals(keyB));
    }

    @Test
    public void testHashCodeNotNull() {
        IKey key = new Ed25519Key("Webmail", "foobar");
        assertNotNull(key.hashCode());
    }

    @Test
    public void testToJson() throws IOException {
        IKey key = new Ed25519Key("foo", "bar");
        JsonWriter writer = mock(JsonWriter.class);
        when(writer.name(anyString())).thenReturn(writer);
        key.toJson(writer);
        verify(writer).name("name");
        verify(writer).name("key");
        verify(writer).value("foo");
        verify(writer).value("bar");
    }

    @Test
    public void testSerialization() throws IOException, ClassNotFoundException {
        final IKey key = new Ed25519Key("foo", "bar");
        try (
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(bos)
        ) {
            oos.writeObject(key);
            byte[] bytes = bos.toByteArray();
            try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
                 ObjectInputStream ois = new ObjectInputStream(bis)) {
                IKey key1 = (IKey) ois.readObject();
                assertTrue(key1.getName().equals("foo"));
                assertTrue(key1.getKey().equals("bar"));
            }
        }
    }
}
