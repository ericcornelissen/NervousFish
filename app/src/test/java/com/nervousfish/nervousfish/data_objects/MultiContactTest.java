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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

public class MultiContactTest {

    private IKey key;

    @Before
    public void setup() {
        this.key = mock(IKey.class);
    }

    @Test
    public void testInstantiateWithSingleContact() {
        Collection<Contact> contacts = new ArrayList<>();
        contacts.add(new Contact("name", this.key));
        MultiContact multiContact = new MultiContact(contacts);
        assertNotNull(multiContact);
        assertFalse(multiContact.getContacts().isEmpty());
    }

    @Test
    public void testInstantiateWithMultipleContacts() {
        Collection<Contact> contacts = new ArrayList<>();
        contacts.add(new Contact("name", this.key));
        contacts.add(new Contact("test", this.key));
        MultiContact multiContact = new MultiContact(contacts);
        assertNotNull(multiContact);
        assertFalse(multiContact.getContacts().isEmpty());
    }

    @Test
    public void testGetContactsReturnsTheContacts() {
        final Map<String, String> map = new HashMap<>();
        map.put("name", "foo");
        map.put("key", "bar");
        final IKey simpleKey = new SimpleKey(map);
        List<Contact> contactsA = new ArrayList<>();
        contactsA.add(new Contact("name", simpleKey));
        MultiContact multiContact = new MultiContact(contactsA);
        assertEquals(contactsA, multiContact.getContacts());

        List<Contact> contactsB = contactsA;
        contactsB.add(new Contact("test", simpleKey));
        MultiContact multiContact2 = new MultiContact(contactsB);
        assertEquals(contactsB, multiContact2.getContacts());
    }

    @Test
    public void testEqualsWorksWithNull() {
        MultiContact multiContact = new MultiContact(new ArrayList<Contact>());
        assertFalse(multiContact.equals(null));
        assertTrue(multiContact.getContacts().isEmpty());
    }

    @Test
    public void testEqualsWorksWithArbitraryObject() {
        List<Contact> contactsA = new ArrayList<>();
        contactsA.add(new Contact("name", this.key));
        MultiContact multiContact = new MultiContact(new ArrayList<Contact>());
        assertFalse(multiContact.equals("foobar"));
    }

    @Test
    public void testEqualsReturnsFalseForUnequalLists() {
        List<Contact> contactsA = new ArrayList<>();
        contactsA.add(new Contact("name", this.key));
        MultiContact multiContact = new MultiContact(contactsA);

        List<Contact> contactsB = contactsA;
        contactsB.add(new Contact("test", this.key));
        MultiContact multiContact2 = new MultiContact(contactsB);
        assertFalse(multiContact.equals(multiContact2));
    }

    @Test
    public void testEqualsReturnsTrueForEqualLists() {
        final Map<String, String> map = new HashMap<>();
        map.put("name", "foo");
        map.put("key", "bar");
        final IKey simpleKey = new SimpleKey(map);
        List<Contact> contactsA = new ArrayList<>();
        contactsA.add(new Contact("name", simpleKey));
        MultiContact multiContact = new MultiContact(contactsA);

        MultiContact multiContact2 = new MultiContact(contactsA);
        assertTrue(multiContact.equals(multiContact2));
    }

    @Test
    public void testHashCodeNotNull() {
        List<Contact> contactsA = new ArrayList<>();
        contactsA.add(new Contact("name", this.key));
        MultiContact multiContact = new MultiContact(contactsA);
        assertNotNull(multiContact.hashCode());
    }

    @Test
    public void testSerialization() throws IOException, ClassNotFoundException {
        final List<Contact> contacts = new ArrayList<>();
        final IKey key1 = new SimpleKey("bar", "baz");
        final Contact contact1 = new Contact("foo", key1);
        final IKey key2 = new SimpleKey("bar2", "baz2");
        final Contact contact2 = new Contact("foo2", key2);
        contacts.add(contact1);
        contacts.add(contact2);
        final MultiContact multiContact = new MultiContact(contacts);
        try (
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(bos)
        ) {
            oos.writeObject(multiContact);
            byte[] bytes = bos.toByteArray();
            try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
                 ObjectInputStream ois = new ObjectInputStream(bis)) {
                final MultiContact multiContact1 = (MultiContact) ois.readObject();
                final List<Contact> contacts1 = multiContact1.getContacts();
                assertEquals(contacts1.size(), 2);
                assertEquals(contacts1.get(0).getName(), "foo");
                assertEquals(contacts1.get(1).getName(), "foo2");
                assertEquals(contacts1.get(0).getKeys().size(), 1);
                assertEquals(contacts1.get(1).getKeys().size(), 1);
                assertEquals(contacts1.get(0).getKeys().get(0), key1);
                assertEquals(contacts1.get(1).getKeys().get(0), key2);
            }
        }
    }
}