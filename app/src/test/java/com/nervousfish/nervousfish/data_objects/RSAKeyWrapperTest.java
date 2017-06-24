package com.nervousfish.nervousfish.data_objects;

import com.nervousfish.nervousfish.ConstantKeywords;

import net.i2p.crypto.eddsa.EdDSAPrivateKey;

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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class RSAKeyWrapperTest {

    @Test
    public void testCanBeInstantiatedWithArbitraryValues() {
        IKey key = new RSAKeyWrapper("Webmail", "foo", "bar");
        assertNotNull(key);
    }

    @Test
    public void testCanBeInstantiatedWithMap() {
        Map<String, String> map = new ConcurrentHashMap<>();
        map.put("name", "name");
        map.put("modulus", "modulus");
        map.put("exponent", "exponent");
        IKey key = new RSAKeyWrapper(map);
        assertNotNull(key);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testCanBeInstantiatedWithMapMustHaveAllFields() {
        Map<String, String> map = new ConcurrentHashMap<>();
        new RSAKeyWrapper(map);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNameNull() {
        Map<String, String> map = new ConcurrentHashMap<>();
        map.put("name", "foo");
        new RSAKeyWrapper(map);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testModulusNull() {
        Map<String, String> map = new ConcurrentHashMap<>();
        map.put("modulus", "foo");
        new RSAKeyWrapper(map);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testExponentNull() {
        Map<String, String> map = new ConcurrentHashMap<>();
        map.put("exponent", "foo");
        new RSAKeyWrapper(map);
    }

    @Test
    public void testGetNameReturnsNotNull() {
        IKey key = new RSAKeyWrapper("FTP", "foo", "bar");
        assertNotNull(key.getName());
    }

    @Test
    public void testGetNameReturnsProvidedName() {
        IKey key = new RSAKeyWrapper("Webserver", "foo", "bar");
        assertEquals("Webserver", key.getName());
    }

    @Test
    public void testGetKeyReturnsNotNull() {
        IKey key = new RSAKeyWrapper("FTP", "foo", "bar");
        assertNotNull(key.getKey());
    }

    @Test
    public void testGetKeyReturnsNonEmptyString() {
        IKey key = new RSAKeyWrapper("Webmail", "foo", "bar");
        assertNotEquals("", key.getKey());
    }

    @Test
    public void testGetTypeReturnsRSA() {
        IKey key = new RSAKeyWrapper("Personal", "foo", "bar");
        assertEquals(ConstantKeywords.RSA_KEY, key.getType());
    }

    @Test
    public void testEqualsWorksWithNull() {
        IKey key = new RSAKeyWrapper("Webmail", "foo", "bar");
        assertFalse(key.equals(null));
    }

    @Test
    public void testEqualsWorksWithArbitraryObject() {
        IKey key = new RSAKeyWrapper("FTP", "foo", "bar");
        assertFalse(key.equals("foobar"));
    }

    @Test
    public void testEqualsReturnsFalseForDifferentKeyTypes() {
        IKey keyA = new RSAKeyWrapper("Personal", "foo", "bar");
        IKey keyB = new Ed25519PrivateKeyWrapper("Computer", mock(EdDSAPrivateKey.class));
        assertFalse(keyA.equals(keyB));
    }

    @Test
    public void testEqualsModulusNotEquals() {
        IKey keyA = new RSAKeyWrapper("foo", "bar", "baz");
        IKey keyB = new RSAKeyWrapper("foo", "baz", "baz");
        assertFalse(keyA.equals(keyB));
    }

    @Test
    public void testEqualsExponentNotEquals() {
        IKey keyA = new RSAKeyWrapper("foo", "bar", "baz");
        IKey keyB = new RSAKeyWrapper("foo", "bar", "bar");
        assertFalse(keyA.equals(keyB));
    }

    @Test
    public void testEqualsReturnsFalseForUnequalKeys() {
        IKey keyA = new RSAKeyWrapper("Personal", "foo", "bar");
        IKey keyB = new RSAKeyWrapper("Email", "hello", "world");
        assertFalse(keyA.equals(keyB));
    }

    @Test
    public void testEqualsReturnsTrueForEqualKeys() {
        IKey keyA = new RSAKeyWrapper("FTP", "foo", "bar");
        IKey keyB = new RSAKeyWrapper("FTP", "foo", "bar");
        assertTrue(keyA.equals(keyB));
    }

    @Test
    public void testHashCodeNotNull() {
        IKey key = new RSAKeyWrapper("Webmail", "foo", "bar");
        assertNotNull(key.hashCode());
    }


    @Test
    public void testSerialization() throws IOException, ClassNotFoundException {
        IKey key = new RSAKeyWrapper("foo", "bar", "baz");
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
                assertTrue(key1.getKey().equals("bar baz"));
            }
        }
    }
}
