package com.nervousfish.nervousfish.data_objects;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import javax.crypto.IllegalBlockSizeException;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;


public class DatabaseTest {
    private Profile profile;

    @Before
    public void setup() {
        List<KeyPair> profilePairs = new ArrayList<>();
        profilePairs.add(new KeyPair("Testpair", mock(IKey.class), mock(IKey.class)));
        this.profile = new Profile("Tester", profilePairs);
    }

    @Test(expected = NullPointerException.class)
    public void testInstantiateWithNullContactListNormalProfile() {
        new Database(null, profile);
    }

    @Test(expected = NullPointerException.class)
    public void testInstantiateWithNormalContactListNullProfile() {
        new Database(new ArrayList<>(), null);
    }

    @Test
    public void testInstantiateNoContact() {
        List<Contact> contactList = new ArrayList<>();
        new Database(contactList, profile);
    }

    @Test
    public void testInstantiateSingleContact() {
        List<Contact> contactList = new ArrayList<>();
        contactList.add(new Contact("TestPerson1", new RSAKey("This", "is", "for testing")));
        new Database(contactList, profile);
    }

    @Test
    public void testInstantiateMultipleContacts() {
        List<Contact> contactList = new ArrayList<>();
        contactList.add(new Contact("TestPerson1", new RSAKey("This", "is", "for testing")));
        contactList.add(new Contact("TestPerson2", new RSAKey("This", "is", "also for testing")));

        new Database(contactList, profile);
    }

    @Test
    public void testGetContacts() {
        List<Contact> contactList = new ArrayList<>();
        contactList.add(new Contact("TestPerson1", new RSAKey("This", "is", "for testing")));
        contactList.add(new Contact("TestPerson2", new RSAKey("This", "is", "also for testing")));
        Database db = new Database(contactList, profile);

        assertEquals(db.getContacts().get(0).getName(), "TestPerson1");
        assertEquals(db.getContacts().get(1).getName(), "TestPerson2");
    }

    @Test
    public void testSetContacts() {
        List<Contact> contactList = new ArrayList<>();
        contactList.add(new Contact("TestPerson1", new RSAKey("This", "is", "for testing")));
        contactList.add(new Contact("TestPerson2", new RSAKey("This", "is", "also for testing")));
        List<Contact> contactList2 = new ArrayList<>();
        contactList2.add(new Contact("TestPerson3", new RSAKey("This", "is", "for testing")));
        contactList2.add(new Contact("TestPerson4", new RSAKey("This", "is", "also for testing")));
        Database db = new Database(contactList, profile);
        assertEquals(contactList, db.getContacts());
        db.setContacts(contactList2);
        assertEquals(contactList2, db.getContacts());
    }

    @Test
    public void testGetProfile() {
        Database db = new Database(new ArrayList<>(), profile);
        assertEquals(db.getProfile(), profile);
    }

}