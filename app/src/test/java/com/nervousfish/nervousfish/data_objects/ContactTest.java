package com.nervousfish.nervousfish.data_objects;

import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

public class ContactTest {

    private IKey key;

    @Before
    public void setup() {
        // We don't use a mock here, because the equals function does not work correctly otherwise
        this.key = new RSAKeyWrapper("foo", "bar", "baz");
    }

    @Test(expected = NullPointerException.class)
    public void testInstantiateWithNullNameSingleKey() {
        new Contact(null, this.key);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInstantiateWithEmptyNameSingleKey() {
        new Contact("", this.key);
    }

    @Test(expected = NullPointerException.class)
    public void testInstantiateWithNullNameSingleKeys() {
        final List<IKey> keys = new ArrayList<>();
        keys.add(this.key);
        new Contact(null, keys);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInstantiateWithEmptyNameSingleKeys() {
        final List<IKey> keys = new ArrayList<>();
        keys.add(this.key);
        new Contact("", keys);
    }

    @Test(expected = NullPointerException.class)
    public void testInstantiateWithNullKey() {
        final IKey keyTmp = null;
        new Contact("foo", keyTmp);
    }

    @Test(expected = NullPointerException.class)
    public void testInstantiateWithNullKeys() {
        final List<IKey> keysTmp = null;
        new Contact("foo", keysTmp);
    }

    @Test
    public void testInstantiateWithSingleKey() {
        Contact contact = new Contact("name", this.key);
        assertNotNull(contact);
    }

    @Test
    public void testInstantiateWithMultipleKeys() {
        Collection<IKey> keys = new ArrayList<>();
        keys.add(this.key);

        Contact contact = new Contact("name", keys);
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
    public void testGetKeysReturnsTheKeys() {
        Contact contactA = new Contact("foo", this.key);
        List<IKey> keysA = new ArrayList<>();
        keysA.add(this.key);
        assertEquals(keysA, contactA.getKeys());

        Collection<IKey> keysB = new ArrayList<>();
        keysB.add(this.key);
        Contact contactB = new Contact("foo", keysB);
        assertEquals(keysB, contactB.getKeys());
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
    public void testAddKeys() {
        Contact contact = new Contact("Kilian", this.key);
        IKey key2 = new RSAKeyWrapper("1", "2", "3");
        IKey key3 = new RSAKeyWrapper("4", "5", "6");
        List<IKey> newKeys = new ArrayList<>();
        newKeys.add(key2);
        newKeys.add(key3);
        contact.addKeys(newKeys);
        List<IKey> expectedKeys = new ArrayList<>();
        expectedKeys.add(this.key);
        expectedKeys.add(key2);
        expectedKeys.add(key3);
        assertTrue(contact.getKeys().equals(expectedKeys));
    }

    @Test
    public void testHashCodeNotNull() {
        Contact contact = new Contact("Kilian", this.key);
        assertNotNull(contact.hashCode());
    }

    @Test
    public void testSerialization() throws IOException, ClassNotFoundException {
        final IKey key = new Ed25519PrivateKeyWrapper("bar", "baz");
        Contact contact = new Contact("foo", key);
        try (
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(bos)
        ) {
            oos.writeObject(contact);
            byte[] bytes = bos.toByteArray();
            try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
                 ObjectInputStream ois = new ObjectInputStream(bis)) {
                Contact contact1 = (Contact) ois.readObject();
                assertTrue(contact1.getName().equals("foo"));
                assertTrue(contact1.getKeys().get(0).equals(key));
            }
        }
    }
}
