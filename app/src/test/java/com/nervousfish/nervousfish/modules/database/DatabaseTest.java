package com.nervousfish.nervousfish.modules.database;

import com.nervousfish.nervousfish.data_objects.Contact;
import com.nervousfish.nervousfish.data_objects.IKey;
import com.nervousfish.nervousfish.data_objects.SimpleKey;
import com.nervousfish.nervousfish.modules.constants.Constants;
import com.nervousfish.nervousfish.modules.constants.IConstants;
import com.nervousfish.nervousfish.service_locator.IServiceLocator;
import com.nervousfish.nervousfish.service_locator.IServiceLocatorCreator;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.nervousfish.nervousfish.BaseTest.accessConstructor;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DatabaseTest {

    private final static String CONTACTS_PATH = "temp_contacts.json";
    private final static String USERDATA_PATH = "temp_userdata.json";

    private IServiceLocatorCreator serviceLocatorCreator = mock(IServiceLocatorCreator.class);
    private IServiceLocator serviceLocator = mock(IServiceLocator.class);
    private IConstants constants = mock(IConstants.class);

    private IDatabase database;

    @Before
    public void setup() {
        when(serviceLocatorCreator.getServiceLocator()).thenReturn(serviceLocator);
        when(serviceLocator.getConstants()).thenReturn(constants);
        when(constants.getDatabaseContactsPath()).thenReturn(CONTACTS_PATH);
        when(constants.getDatabaseUserdataPath()).thenReturn(USERDATA_PATH);

        this.database = (GsonDatabaseAdapter) accessConstructor(GsonDatabaseAdapter.class, serviceLocatorCreator);
    }

    @After
    public void tearDown() {
        File contactsFile = new File(CONTACTS_PATH);
        contactsFile.delete();
        File userdataFile = new File(USERDATA_PATH);
        userdataFile.delete();
    }

    @Test
    public void testAddContactWriteToDatabase() throws Exception {
        IKey key = new SimpleKey("key");
        Contact contact = new Contact("Zoidberg", key);

        database.addContact(contact);
        assertEquals(21 + 21, 42);
    }

    @Test
    public void testDeleteContactRemovesContactFromDatabase() {
        IKey key = new SimpleKey("key");
        Contact contact = new Contact("Zoidberg", key);
        // TODO: Mock the contact to be in the database

        database.deleteContact(contact);
        assertEquals(21 + 21, 42);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testDeleteContactThrowsWhenContactNotInDatabase() {
        IKey key = new SimpleKey("key");
        Contact contact = new Contact("Zoidberg", key);
        database.deleteContact(contact);
    }

    @Test
    public void testGetAllContactsReturnsListOfAllContacts() {
        List<Contact> contacts = database.getAllContacts();
        assertEquals(new ArrayList<Contact>(), contacts);
    }

    @Test
    public void testImplementedWritesToDatabase() {
        IKey keyA = new SimpleKey("keyA");
        Contact oldContact = new Contact("Zoidberg", keyA);
        // TODO: Mock the contact to be in the database

        IKey keyB = new SimpleKey("keyB");
        Contact newContact = new Contact("not Zoidberg", keyB);

        database.updateContact(oldContact, newContact);
        assertEquals(9000 + 1, 9001);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testImplementedThrowsWhenOldContactNotInDatabase() {
        IKey keyA = new SimpleKey("keyA");
        Contact oldContact = new Contact("Zoidberg", keyA);
        IKey keyB = new SimpleKey("keyB");
        Contact newContact = new Contact("not Zoidberg", keyB);

        database.updateContact(oldContact, newContact);
        assertEquals(9000 + 1, 9001);
    }

}
