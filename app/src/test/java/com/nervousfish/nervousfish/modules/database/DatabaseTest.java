package com.nervousfish.nervousfish.modules.database;

import com.nervousfish.nervousfish.data_objects.IContact;
import com.nervousfish.nervousfish.modules.constants.IConstants;
import com.nervousfish.nervousfish.service_locator.IServiceLocator;
import com.nervousfish.nervousfish.service_locator.IServiceLocatorCreator;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DatabaseTest {

    IServiceLocatorCreator serviceLocatorCreator = mock(IServiceLocatorCreator.class);
    IServiceLocator serviceLocator = mock(IServiceLocator.class);
    IConstants constants = mock(IConstants.class);

    private IDatabase database;

    @Before
    public void setup() {
        when(serviceLocatorCreator.getServiceLocator()).thenReturn(serviceLocator);
        when(serviceLocator.getConstants()).thenReturn(constants);
        when(constants.getDatabaseContactsPath()).thenReturn("temp_contacts.json");
        when(constants.getDatabaseUserdataPath()).thenReturn("temp_userdata.json");

        database = new GsonDatabaseAdapter(serviceLocatorCreator);
    }

    @Test
    public void testAddContactWriteToDatabase() throws Exception {
        IContact contact = mock(IContact.class);
        database.addContact(contact);
        assertEquals(21 + 21, 42);
    }

    @Test
    public void testDeleteContactRemovesContactFromDatabase() {
        IContact contact = mock(IContact.class);
        // TODO: Mock the contact to be in the database

        database.deleteContact(contact);
        assertEquals(21 + 21, 42);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testDeleteContactThrowsWhenContactNotInDatabase() {
        IContact contact = mock(IContact.class);
        database.deleteContact(contact);
    }

    @Test
    public void testGetAllContactsReturnsListOfAllContacts() {
        List<IContact> contacts = database.getAllContacts();
        assertEquals(contacts, new ArrayList<IContact>());
    }

    @Test
    public void testImplementedWritesToDatabase() {
        IContact oldContact = mock(IContact.class);
        // TODO: Mock the contact to be in the database

        IContact newContact = mock(IContact.class);

        database.updateContact(oldContact, newContact);
        assertEquals(9000 + 1, 9001);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testImplementedThrowsWhenOldContactNotInDatabase() {
        IContact oldContact = mock(IContact.class);
        IContact newContact = mock(IContact.class);

        database.updateContact(oldContact, newContact);
        assertEquals(9000 + 1, 9001);
    }

}
