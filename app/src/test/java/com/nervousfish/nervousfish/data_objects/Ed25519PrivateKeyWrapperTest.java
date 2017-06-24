package com.nervousfish.nervousfish.data_objects;

import com.google.gson.stream.JsonWriter;
import com.nervousfish.nervousfish.BaseTest;
import com.nervousfish.nervousfish.ConstantKeywords;
import com.nervousfish.nervousfish.modules.cryptography.IKeyGenerator;
import com.nervousfish.nervousfish.modules.cryptography.KeyGeneratorAdapter;
import com.nervousfish.nervousfish.service_locator.IServiceLocator;

import net.i2p.crypto.eddsa.EdDSAPrivateKey;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
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

public class Ed25519PrivateKeyWrapperTest {

    @Test
    public void testCanBeInstantiatedWithArbitraryValues() {
        IKey key = new Ed25519PrivateKeyWrapper("Webmail", mock(EdDSAPrivateKey.class));
        assertNotNull(key);
    }

    @Test(expected = NullPointerException.class)
    public void testCanBeInstantiatedWithMapMustHaveAllFields() {
        Map<String, String> map = new ConcurrentHashMap<>();
        new Ed25519PrivateKeyWrapper(map);
    }

    @Test(expected = NullPointerException.class)
    public void testKeyNull() {
        Map<String, String> map = new ConcurrentHashMap<>();
        map.put("name", "foo");
        new Ed25519PrivateKeyWrapper(map);
    }

    @Test
    public void testGetNameReturnsProvidedName() {
        IKey key = new Ed25519PrivateKeyWrapper("Webserver", mock(EdDSAPrivateKey.class));
        assertEquals("Webserver", key.getName());
    }

    @Test
    public void testGetTypeReturnsSimple() {
        IKey key = new Ed25519PrivateKeyWrapper("Webserver", mock(EdDSAPrivateKey.class));
        assertEquals(ConstantKeywords.ED25519_KEY, key.getType());
    }

    @Test
    public void testEqualsWorksWithNull() {
        IKey key = new Ed25519PrivateKeyWrapper("Webmail", mock(EdDSAPrivateKey.class));
        assertFalse(key.equals(null));
    }

    @Test
    public void testEqualsWorksWithArbitraryObject() {
        IKey key = new Ed25519PrivateKeyWrapper("FTP", mock(EdDSAPrivateKey.class));
        assertFalse(key.equals("foobar"));
    }

    @Test
    public void testEqualsReturnsFalseForUnequalKeys() {
        IKey keyA = new Ed25519PrivateKeyWrapper("FTP", mock(EdDSAPrivateKey.class));
        IKey keyB = new Ed25519PrivateKeyWrapper("Zoidberg", mock(EdDSAPrivateKey.class));
        assertFalse(keyA.equals(keyB));
    }

    @Test
    public void testEqualsReturnsTrueForEqualKeys() {
        IKey keyA = new Ed25519PrivateKeyWrapper("Webmail", mock(EdDSAPrivateKey.class));
        IKey keyB = new Ed25519PrivateKeyWrapper("Webmail", mock(EdDSAPrivateKey.class));
        assertTrue(keyA.equals(keyB));
    }

    @Test
    public void testHashCodeNotNull() {
        IKey key = new Ed25519PrivateKeyWrapper("Webmail", mock(EdDSAPrivateKey.class));
        assertNotNull(key.hashCode());
    }

    @Test
    public void testToJson() throws IOException {
        IKey key = new Ed25519PrivateKeyWrapper("foo", mock(EdDSAPrivateKey.class));
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
        final IKey key = new Ed25519PrivateKeyWrapper("foo", mock(EdDSAPrivateKey.class));
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

    @Test
    public void testToString() {
        IKeyGenerator generator = (IKeyGenerator) BaseTest.accessConstructor(KeyGeneratorAdapter.class, mock(IServiceLocator.class));
        Ed25519KeyPair keyPair = generator.generateEd25519KeyPair("hoi");
        final String string = Arrays.toString(keyPair.getPrivateKey().getKey().geta());
        assertEquals(string, "hoi");
    }
}
