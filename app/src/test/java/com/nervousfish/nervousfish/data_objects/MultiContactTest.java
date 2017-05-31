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
        List<Contact> contactsA = new ArrayList<>();
        contactsA.add(new Contact("name", this.key));
        MultiContact multiContact = new MultiContact(contactsA);
        assertEquals(contactsA, multiContact.getContacts());

        List<Contact> contactsB = contactsA;
        contactsB.add(new Contact("test", this.key));
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
        List<Contact> contactsA = new ArrayList<>();
        contactsA.add(new Contact("name", this.key));
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
        List<Contact> contactsA = new ArrayList<>();
        contactsA.add(new Contact("name", this.key));
        final MultiContact multiContact = new MultiContact(contactsA);
        try (
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(bos)
        ) {
            oos.writeObject(multiContact);
            byte[] bytes = bos.toByteArray();
            try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
                 ObjectInputStream ois = new ObjectInputStream(bis)) {
                MultiContact multiContact1 = (MultiContact) ois.readObject();
                assertTrue(multiContact1.getContacts().get(0).equals(multiContact.getContacts().get(0)));
            }
        }
    }

}
