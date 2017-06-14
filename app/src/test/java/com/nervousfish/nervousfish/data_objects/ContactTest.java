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
    public void testHashCodeNotNull() {
        Contact contact = new Contact("Kilian", this.key);
        assertNotNull(contact.hashCode());
    }

    @Test
    public void testSerialization() throws IOException, ClassNotFoundException {
        final IKey key = new Ed25519Key("bar", "baz");
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
