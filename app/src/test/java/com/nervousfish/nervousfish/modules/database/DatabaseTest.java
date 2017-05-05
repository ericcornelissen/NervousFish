package com.nervousfish.nervousfish.modules.database;

import com.nervousfish.nervousfish.data_objects.Account;
import com.nervousfish.nervousfish.data_objects.Contact;
import com.nervousfish.nervousfish.data_objects.IKey;
import com.nervousfish.nervousfish.data_objects.SimpleKey;
import com.nervousfish.nervousfish.modules.constants.IConstants;
import com.nervousfish.nervousfish.service_locator.IServiceLocator;
import com.nervousfish.nervousfish.service_locator.IServiceLocatorCreator;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
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
    public void testDeleteContactRemovesContactFromDatabase() throws IOException {
        IKey key = new SimpleKey("key");
        Contact contact = new Contact("Zoidberg", key);

        // Add the contact to remove from the database
        write("[{\"name\":\"Zoidberg\",\"publicKey\":{\"type\":\"simple\",\"key\":\"key\"}}]", CONTACTS_PATH);

        database.deleteContact(contact);
        assertEquals("[]\n", read(CONTACTS_PATH));
    }

    @Test(expected=IllegalArgumentException.class)
    public void testDeleteContactThrowsWhenContactNotInDatabase() throws IOException {
        IKey key = new SimpleKey("key");
        Contact contact = new Contact("Zoidberg", key);
        database.deleteContact(contact);
    }

    @Test
    public void testGetAllContactsReturnsEmptyListWhenThereAreNoContacts() throws IOException {
        List<Contact> contacts = database.getAllContacts();
        assertEquals(new ArrayList<Contact>(), contacts);
    }

    @Test
    public void testGetAllContactsReturnsListOfAllContacts() throws IOException {
        write("[{\"name\":\"Zoidberg\",\"publicKey\":{\"type\":\"simple\",\"key\":\"key\"}}]", CONTACTS_PATH);

        IKey key = new SimpleKey("key");
        Contact contact = new Contact("Zoidberg", key);
        List<Contact> expected = new ArrayList<Contact>();
        expected.add(contact);

        List<Contact> actual = database.getAllContacts();
        assertEquals(expected, actual);
    }

    @Test
    public void testImplementedWritesToDatabase() throws IOException {
        IKey keyA = new SimpleKey("keyA");
        Contact oldContact = new Contact("Zoidberg", keyA);

        // Add the contact to remove from the database
        write("[{\"name\":\"Zoidberg\",\"publicKey\":{\"type\":\"simple\",\"key\":\"keyA\"}}]", CONTACTS_PATH);

        IKey keyB = new SimpleKey("keyB");
        Contact newContact = new Contact("not Zoidberg", keyB);

        database.updateContact(oldContact, newContact);
        assertEquals("[{\"name\":\"not Zoidberg\",\"publicKey\":{\"type\":\"simple\",\"key\":\"keyB\"}}]\n", read(CONTACTS_PATH));
    }

    @Test(expected=IllegalArgumentException.class)
    public void testImplementedThrowsWhenOldContactNotInDatabase() throws IOException {
        IKey keyA = new SimpleKey("keyA");
        Contact oldContact = new Contact("Zoidberg", keyA);
        IKey keyB = new SimpleKey("keyB");
        Contact newContact = new Contact("not Zoidberg", keyB);

        database.updateContact(oldContact, newContact);
        assertEquals(9000 + 1, 9001);
    }

    @Test
    public void testGetAccountEmpty() throws IOException {
        List<Account> actual = database.getAccounts();
        assertEquals(new ArrayList<Account>(), actual);
    }

    private void write(final String data, final String filePath) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));
            writer.write(data);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String read(final String filePath) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(filePath)));
            String line = reader.readLine();
            StringBuilder stringBuilder = new StringBuilder();
            while (line != null) {
                stringBuilder.append(line).append("\n");
                line = reader.readLine();
            }

            reader.close();
            return stringBuilder.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

}
