package com.nervousfish.nervousfish.data_objects;

import com.nervousfish.nervousfish.modules.cryptography.Ed25519Generator;

import net.i2p.crypto.eddsa.EdDSAPrivateKey;
import net.i2p.crypto.eddsa.EdDSAPublicKey;
import net.i2p.crypto.eddsa.Utils;

import org.junit.Test;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import nl.tudelft.ewi.ds.bankver.cryptography.ED25519;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;

public class Ed25519KeyTest {

    @Test
    public void testCanBeInstantiatedWithArbitraryValues() {
        IKey key = new Ed25519Key("Webmail", mock(EdDSAPublicKey.class), mock(EdDSAPrivateKey.class));
        assertNotNull(key);
    }

    @Test
    public void testCanBeInstantiatedWithMap() throws IOException {
        Map<String, String> map = new ConcurrentHashMap<>();
        map.put("name", "name");
        final Ed25519Generator keyPairGenerator = Ed25519Generator.generatePair();
        final EdDSAPublicKey publicKey = keyPairGenerator.getPublicKey();
        final EdDSAPrivateKey privateKey = keyPairGenerator.getPrivateKey();
        map.put("publicKey", Utils.bytesToHex(publicKey.getAbyte()));
        map.put("privateKey", Utils.bytesToHex(privateKey.getAbyte()));
        IKey key = new Ed25519Key(map);
        assertNotNull(key);
    }

    @Test(expected = NullPointerException.class)
    public void testCanBeInstantiatedWithMapMustHaveAllFields() throws IOException {
        Map<String, String> map = new ConcurrentHashMap<>();
        new Ed25519Key(map);
    }

    @Test(expected = NullPointerException.class)
    public void testKeyNull() throws IOException {
        Map<String, String> map = new ConcurrentHashMap<>();
        map.put("name", "foo");
        new Ed25519Key(map);
    }

    /*@Test
    public void testGetNameReturnsProvidedName() {
        IKey key = new Ed25519Key("Webserver", "foobar", "foobar".getBytes());
        assertEquals("Webserver", key.getName());
    }

    @Test
    public void testGetKeyReturnsNonEmptyString() {
        IKey key = new Ed25519Key("Webmail", "foobar", "foobar".getBytes());
        assertNotEquals("", key.getKey());
    }

    @Test
    public void testGetTypeReturnsSimple() {
        IKey key = new Ed25519Key("Webserver", "foobar", "foobar".getBytes());
        assertEquals(ConstantKeywords.ED25519_KEY, key.getType());
    }

    @Test
    public void testEqualsWorksWithNull() {
        IKey key = new Ed25519Key("Webmail", "foobar", "foobar".getBytes());
        assertFalse(key.equals(null));
    }

    @Test
    public void testEqualsWorksWithArbitraryObject() {
        IKey key = new Ed25519Key("FTP", "foobar", "foobar".getBytes());
        assertFalse(key.equals("foobar"));
    }

    @Test
    public void testEqualsReturnsFalseForDifferentKeyTypes() {
        IKey keyA = new Ed25519Key("Webserver", "Hello world!", "foobar".getBytes());
        IKey keyB = new RSAKey("Webmail", "foo", "bar");
        assertFalse(keyA.equals(keyB));
    }

    @Test
    public void testEqualsReturnsFalseForUnequalKeys() {
        IKey keyA = new Ed25519Key("FTP", "foobar", "foobar".getBytes());
        IKey keyB = new Ed25519Key("Zoidberg", "Hello world!", "foobar".getBytes());
        assertFalse(keyA.equals(keyB));
    }

    @Test
    public void testEqualsReturnsTrueForEqualKeys() {
        IKey keyA = new Ed25519Key("Webmail", "foobar", "foobar".getBytes());
        IKey keyB = new Ed25519Key("Webmail", "foobar", "foobar".getBytes());
        assertTrue(keyA.equals(keyB));
    }

    @Test
    public void testHashCodeNotNull() {
        IKey key = new Ed25519Key("Webmail", "foobar", "foobar".getBytes());
        assertNotNull(key.hashCode());
    }

    @Test
    public void testToJson() throws IOException {
        IKey key = new Ed25519Key("foo", "bar", "foobar".getBytes());
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
        final IKey key = new Ed25519Key("foo", "bar", "foobar".getBytes());
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
                assertTrue(key1.getKey().equals("bar" + "#" + Arrays.toString("foobar".getBytes())));
            }
        }
    }*/

    @Test
    public void testCreation() throws Exception {
        EdDSAPublicKey publicKey = Ed25519Generator.generatePair().getPublicKey();
        String hex = Utils.bytesToHex(publicKey.getAbyte());
        EdDSAPublicKey newPublicKey = ED25519.getPublicKey(hex);
        assertEquals(publicKey, newPublicKey);
    }
}
