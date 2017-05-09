package com.nervousfish.nervousfish.modules.database;

import com.nervousfish.nervousfish.data_objects.Profile;
import com.nervousfish.nervousfish.data_objects.Contact;
import com.nervousfish.nervousfish.data_objects.IKey;
import com.nervousfish.nervousfish.data_objects.SimpleKey;
import com.nervousfish.nervousfish.events.SLReadyEvent;
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
import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GsonDatabaseAdapterTest {

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
        this.database.onSLReadyEvent(mock(SLReadyEvent.class));
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
        assertEquals("[{\"name\":\"Zoidberg\",\"publicKey\":[\"simple\",{\"key\":\"key\"}]}]\n", read(CONTACTS_PATH));
    }

    @Test
    public void testDeleteContactRemovesContactFromDatabase() throws IOException {
        IKey key = new SimpleKey("key");
        Contact contact = new Contact("Zoidberg", key);

        // Add the contact to remove from the database
        write("[{\"name\":\"Zoidberg\",\"publicKey\":[\"simple\",{\"key\":\"key\"}]}]", CONTACTS_PATH);

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
    public void testGetAllContactsReturnsListOfAllContactsWith1Contact() throws IOException {
        write("[{\"name\":\"Zoidberg\",\"publicKey\":[\"simple\",{\"key\":\"key\"}]}]", CONTACTS_PATH);

        IKey key = new SimpleKey("key");
        Contact contact = new Contact("Zoidberg", key);
        List<Contact> expected = new ArrayList<Contact>();
        expected.add(contact);

        List<Contact> actual = database.getAllContacts();
        assertEquals(expected, actual);
    }

    @Test
    public void testGetAllContactsReturnsListOfAllContactsWith2Contacts() throws IOException {
        write("[{\"name\":\"Zoidberg\",\"publicKey\":[\"simple\",{\"key\":\"ABABAB\"}]}," +
                "{\"name\":\"Fry\",\"publicKey\":[\"simple\",{\"key\":\"BABABA\"}]}]", CONTACTS_PATH);

        IKey zoidbergsDey = new SimpleKey("ABABAB");
        Contact zoidberg = new Contact("Zoidberg", zoidbergsDey);
        IKey frysKey = new SimpleKey("BABABA");
        Contact fry = new Contact("Fry", frysKey);
        List<Contact> expected = new ArrayList<>();
        expected.add(zoidberg);
        expected.add(fry);

        List<Contact> actual = database.getAllContacts();
        assertEquals(expected, actual);
    }

    @Test
    public void testImplementedWritesToDatabase() throws IOException {
        IKey keyA = new SimpleKey("keyA");
        Contact oldContact = new Contact("Zoidberg", keyA);

        // Add the contact to remove from the database
        write("[{\"name\":\"Zoidberg\",\"publicKey\":[\"simple\",{\"key\":\"keyA\"}]}]", CONTACTS_PATH);

        IKey keyB = new SimpleKey("keyB");
        Contact newContact = new Contact("not Zoidberg", keyB);

        database.updateContact(oldContact, newContact);
        assertEquals("[{\"name\":\"not Zoidberg\",\"publicKey\":[\"simple\",{\"key\":\"keyB\"}]}]\n", read(CONTACTS_PATH));
    }

    @Test(expected=IllegalArgumentException.class)
    public void testUpdateContactThrowsWhenOldContactNotInDatabase() throws IOException {
        IKey keyA = new SimpleKey("keyA");
        Contact oldContact = new Contact("Zoidberg", keyA);
        IKey keyB = new SimpleKey("keyB");
        Contact newContact = new Contact("not Zoidberg", keyB);

        database.updateContact(oldContact, newContact);
        assertEquals(9000 + 1, 9001);
    }

    @Test
    public void testGetProfileEmpty() throws IOException {
        List<Profile> actual = database.getProfiles();
        assertEquals(new ArrayList<Profile>(), actual);
    }

    @Test
    public void testAddProfile() throws IOException {
        IKey pubKey = new SimpleKey("PubKey");
        IKey privKey = new SimpleKey("privKey");
        Profile newProfile = new Profile("CoolGuy", pubKey, privKey);
        database.addProfile(newProfile);

        List<Profile> actual = database.getProfiles();
        assertTrue(newProfile.equals(actual.get(0)));
    }

    @Test
    public void testUpdateProfile() throws IOException {
        IKey pubKey = new SimpleKey("PubKey");
        IKey privKey = new SimpleKey("privKey");
        Profile newProfile = new Profile("CoolGuy", pubKey, privKey);
        database.addProfile(newProfile);

        Profile newProfileUpdate = new Profile("OtherName", pubKey, privKey);
        database.updateProfile(newProfile, newProfileUpdate);
        List<Profile> actual = database.getProfiles();

        assertFalse(newProfile.equals(actual.get(0)));
        assertTrue(newProfileUpdate.equals(actual.get(0)));
        assertEquals(1, actual.size());
    }

    @Test
    public void testDeleteProfile() throws IOException {
        IKey pubKey = new SimpleKey("PubKey");
        IKey privKey = new SimpleKey("privKey");
        Profile newProfile = new Profile("CoolGuy", pubKey, privKey);
        database.addProfile(newProfile);

        List<Profile> actual = database.getProfiles();
        assertEquals(1, actual.size());
        database.deleteProfile(newProfile);
        actual = database.getProfiles();

        assertEquals(0, actual.size());
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
