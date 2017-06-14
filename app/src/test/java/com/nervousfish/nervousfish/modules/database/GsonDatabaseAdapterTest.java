package com.nervousfish.nervousfish.modules.database;

import com.google.gson.JsonSyntaxException;
import com.nervousfish.nervousfish.data_objects.Contact;
import com.nervousfish.nervousfish.data_objects.IKey;
import com.nervousfish.nervousfish.data_objects.KeyPair;
import com.nervousfish.nervousfish.data_objects.Profile;
import com.nervousfish.nervousfish.data_objects.RSAKey;
import com.nervousfish.nervousfish.data_objects.Ed25519Key;
import com.nervousfish.nervousfish.modules.constants.IConstants;
import com.nervousfish.nervousfish.modules.filesystem.AndroidFileSystemAdapter;
import com.nervousfish.nervousfish.service_locator.IServiceLocator;
import com.nervousfish.nervousfish.service_locator.ModuleWrapper;

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
import java.util.Collection;
import java.util.List;

import static com.nervousfish.nervousfish.BaseTest.accessConstructor;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GsonDatabaseAdapterTest {

    private final static String CONTACTS_PATH = "temp_contacts.json";
    private final static String USERDATA_PATH = "temp_userdata.json";

    private IServiceLocator serviceLocator = mock(IServiceLocator.class);
    private IConstants constants = mock(IConstants.class);

    private IDatabase database;

    @Before
    public void setup() {
        when(serviceLocator.getConstants()).thenReturn(constants);
        when(serviceLocator.getFileSystem()).thenReturn((AndroidFileSystemAdapter) accessConstructor(AndroidFileSystemAdapter.class, serviceLocator));
        when(constants.getDatabaseContactsPath()).thenReturn(CONTACTS_PATH);
        when(constants.getDatabaseUserdataPath()).thenReturn(USERDATA_PATH);

        database = (GsonDatabaseAdapter) accessConstructor(GsonDatabaseAdapter.class, serviceLocator);
    }

    @After
    public void tearDown() {
        File contactsFile = new File(CONTACTS_PATH);
        contactsFile.delete();
        File userdataFile = new File(USERDATA_PATH);
        userdataFile.delete();
    }

    @Test
    public void testNewInstance() throws Exception {
        ModuleWrapper<GsonDatabaseAdapter> wrapper = GsonDatabaseAdapter.newInstance(serviceLocator);
        assertNotNull(wrapper);
    }

    @Test
    public void testConstructorNotFailsWhenFileInitializationFails() {
        IServiceLocator serviceLocator = mock(IServiceLocator.class);
        when(serviceLocator.getFileSystem()).thenReturn((AndroidFileSystemAdapter) accessConstructor(AndroidFileSystemAdapter.class, serviceLocator));
        IConstants constants = mock(IConstants.class);

        when(serviceLocator.getConstants()).thenReturn(constants);
        when(constants.getDatabaseContactsPath()).thenReturn("!^~+/\\.txt"); // Invalid file name

        IDatabase database = (GsonDatabaseAdapter) accessConstructor(GsonDatabaseAdapter.class, serviceLocator);
        assertNotNull(database);
    }

    @Test
    public void testAddContactWithSingleKeyWriteToDatabase() throws Exception {
        IKey key = new Ed25519Key("Webmail", "key");
        Contact contact = new Contact("Zoidberg", key);

        database.addContact(contact);
        assertEquals("[{\"name\":\"Zoidberg\",\"keys\":[[\"ed25519\"," +
                "{\"name\":\"Webmail\",\"key\":\"key\"}]]}]\n", read(CONTACTS_PATH));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddDuplicateContact() throws Exception {
        IKey key = new Ed25519Key("Webmail", "key");
        Contact contact = new Contact("Zoidberg", key);

        database.addContact(contact);
        database.addContact(contact);
    }

    @Test
    public void testAddContactWithMultipleKeysWriteToDatabase() throws Exception {
        Collection<IKey> keys = new ArrayList<>();
        keys.add(new RSAKey("FTP", "A", "B"));
        keys.add(new Ed25519Key("Webserver", "keyB"));
        Contact contact = new Contact("Zoidberg", keys);

        database.addContact(contact);
        assertEquals("[{\"name\":\"Zoidberg\",\"keys\":[[\"RSA\",{\"name\":\"FTP\",\"modulus\":\"A\",\"exponent\":\"B\"}]," +
                "[\"ed25519\",{\"name\":\"Webserver\",\"key\":\"keyB\"}]]}]\n", read(CONTACTS_PATH));
    }

    @Test
    public void testDeleteContactWithSingleKeyRemovesContactFromDatabase() throws IOException {
        IKey key = new Ed25519Key("FTP", "key");
        Contact contact = new Contact("Zoidberg", key);

        // Add the contact to remove from the database
        write("[{\"name\":\"Zoidberg\",\"keys\":[[\"ed25519\",{\"name\":\"FTP\",\"key\":\"key\"}]]}]", CONTACTS_PATH);

        database.deleteContact(contact.getName());
        assertEquals("[]\n", read(CONTACTS_PATH));
    }

    @Test
    public void testDeleteContactWithMultipleKeysRemovesContactFromDatabase() throws IOException {
        Collection<IKey> keys = new ArrayList<>();
        keys.add(new Ed25519Key("Webserver", "keyA"));
        keys.add(new Ed25519Key("Webmail", "keyB"));
        Contact contact = new Contact("Zoidberg", keys);

        // Add the contact to remove from the database
        write("[{\"name\":\"Zoidberg\",\"keys\":[[\"ed25519\",{\"name\":\"Webserver\",\"key\":\"keyA\"}]," +
                "[\"ed25519\",{\"name\":\"Webmail\",\"key\":\"keyB\"}]]}]", CONTACTS_PATH);

        database.deleteContact(contact.getName());
        assertEquals("[]\n", read(CONTACTS_PATH));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDeleteContactThrowsWhenContactNotInDatabase() throws IOException {
        write("[{\"name\":\"Zoidberg\",\"keys\":[[\"ed25519\",{\"name\":\"Webserver\",\"key\":\"keyA\"}]," +
                "[\"ed25519\",{\"name\":\"Webmail\",\"key\":\"keyB\"}]]}]", CONTACTS_PATH);

        IKey key = new Ed25519Key("Burpie", "key");
        Contact contact = new Contact("Flurpie", key);
        database.deleteContact(contact.getName());
    }

    @Test
    public void testGetAllContactsReturnsEmptyListWhenThereAreNoContacts() throws IOException {
        List<Contact> contacts = database.getAllContacts();
        assertEquals(new ArrayList<Contact>(), contacts);
    }

    @Test
    public void testGetAllContactsReturnsListOfAllContactsWith1Contact() throws IOException {
        Collection<IKey> keys = new ArrayList<>();
        keys.add(new Ed25519Key("FTP", "keyA"));
        keys.add(new Ed25519Key("Webmail", "keyB"));
        Contact contact = new Contact("Zoidberg", keys);

        List<Contact> expected = new ArrayList<>();
        expected.add(contact);

        write("[{\"name\":\"Zoidberg\",\"keys\":[[\"ed25519\",{\"name\":\"FTP\",\"key\":\"keyA\"}]," +
                "[\"ed25519\",{\"name\":\"Webmail\",\"key\":\"keyB\"}]]}]", CONTACTS_PATH);

        List<Contact> actual = database.getAllContacts();
        assertEquals(expected, actual);
    }

    @Test
    public void testGetAllContactsReturnsContactWithMultipleKeys() throws IOException {
        write("[{\"name\":\"Zoidberg\",\"keys\":[[\"ed25519\",{\"name\":\"FTP\",\"key\":\"key\"}]]}]", CONTACTS_PATH);

        IKey key = new Ed25519Key("FTP", "key");
        Contact contact = new Contact("Zoidberg", key);
        List<Contact> expected = new ArrayList<>();
        expected.add(contact);

        List<Contact> actual = database.getAllContacts();
        assertEquals(expected, actual);
    }

    @Test
    public void testGetAllContactsReturnsListOfAllContactsWith2Contacts() throws IOException {
        write("[{\"name\":\"Zoidberg\",\"keys\":[[\"RSA\",{\"name\":\"FTP\",\"modulus\":\"A\",\"exponent\":\"B\"}]]}," +
                "{\"name\":\"Fry\",\"keys\":[[\"ed25519\",{\"name\":\"Webmail\",\"key\":\"BABABA\"}]]}]", CONTACTS_PATH);

        IKey zoidbergsKey = new RSAKey("FTP", "A", "B");
        Contact zoidberg = new Contact("Zoidberg", zoidbergsKey);
        IKey frysKey = new Ed25519Key("Webmail", "BABABA");
        Contact fry = new Contact("Fry", frysKey);
        List<Contact> expected = new ArrayList<>();
        expected.add(zoidberg);
        expected.add(fry);

        List<Contact> actual = database.getAllContacts();
        assertEquals(expected, actual);
    }

    @Test
    public void testGetContactWithName() throws IOException {
        IKey zoidbergsKey = new RSAKey("FTP", "A", "B");
        Contact zoidberg = new Contact("Zoidberg", zoidbergsKey);
        database.addContact(zoidberg);

        Contact actual = database.getContactWithName(zoidberg.getName());
        assertEquals(zoidberg, actual);
    }

    @Test(expected = JsonSyntaxException.class)
    public void testGetAllContactsFailsForInvalidKeyType() throws IOException {
        write("[{\"name\":\"Zoidberg\",\"keys\":[[\"not a valid key\",{\"key\":\"key\"}]]}]", CONTACTS_PATH);
        database.getAllContacts();
    }

    @Test
    public void testImplementedWritesToDatabase() throws IOException {
        IKey keyA = new Ed25519Key("Webserver", "keyA");
        Contact oldContact = new Contact("Zoidberg", keyA);

        // Add the contact to remove from the database
        write("[{\"name\":\"Zoidberg\",\"keys\":[[\"ed25519\",{\"name\":\"Webserver\"," +
                "\"key\":\"keyA\"}]]}]", CONTACTS_PATH);

        IKey keyB = new Ed25519Key("FTP", "keyB");
        Contact newContact = new Contact("not Zoidberg", keyB);

        database.updateContact(oldContact, newContact);
        assertEquals("[{\"name\":\"not Zoidberg\",\"keys\":[[\"ed25519\",{\"name\":\"FTP\"," +
                "\"key\":\"keyB\"}]]}]\n", read(CONTACTS_PATH));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUpdateContactThrowsWhenOldContactNotInDatabase() throws IOException {
        IKey keyA = new Ed25519Key("Webmail", "keyA");
        Contact oldContact = new Contact("Zoidberg", keyA);
        IKey keyB = new Ed25519Key("FTP", "keyB");
        Contact newContact = new Contact("not Zoidberg", keyB);

        database.updateContact(oldContact, newContact);
    }

    @Test
    public void testGetProfileEmpty() throws IOException {
        List<Profile> actual = database.getProfiles();
        assertEquals(new ArrayList<Profile>(), actual);
    }

    @Test
    public void testAddProfile() throws IOException {
        IKey publicKey = new Ed25519Key("Webserver", "key");
        IKey privateKey = new Ed25519Key("Webserver", "yek");
        KeyPair keyPair = new KeyPair("Webserver", publicKey, privateKey);
        Profile newProfile = new Profile("CoolGuy", keyPair);
        database.addProfile(newProfile);

        List<Profile> actual = database.getProfiles();
        assertTrue(newProfile.equals(actual.get(0)));
    }

    @Test
    public void testUpdateProfile() throws IOException {
        IKey publicKey = new Ed25519Key("FTP", "key");
        IKey privateKey = new Ed25519Key("FTP", "yek");
        KeyPair keyPair = new KeyPair("FTP", publicKey, privateKey);
        Profile newProfile = new Profile("CoolGuy", keyPair);
        database.addProfile(newProfile);

        Profile newProfileUpdate = new Profile("OtherName", keyPair);
        database.updateProfile(newProfile, newProfileUpdate);
        List<Profile> actual = database.getProfiles();

        assertFalse(newProfile.equals(actual.get(0)));
        assertTrue(newProfileUpdate.equals(actual.get(0)));
        assertEquals(1, actual.size());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUpdateProfileThrowsWhenOldProfileNotInDatabase() throws IOException {
        IKey publicKey = new Ed25519Key("FTP", "key");
        IKey privateKey = new Ed25519Key("FTP", "yek");
        KeyPair keyPair = new KeyPair("FTP", publicKey, privateKey);
        Profile oldProfile = new Profile("CoolGuy", keyPair);

        Profile newProfile = new Profile("OtherName", keyPair);
        database.updateProfile(oldProfile, newProfile);
    }

    @Test
    public void testDeleteProfile() throws IOException {
        IKey publicKey = new Ed25519Key("FTP", "key");
        IKey privateKey = new Ed25519Key("FTP", "yek");
        KeyPair keyPair = new KeyPair("FTP", publicKey, privateKey);
        Profile newProfile = new Profile("CoolGuy", keyPair);
        database.addProfile(newProfile);

        List<Profile> actual = database.getProfiles();
        assertEquals(1, actual.size());
        database.deleteProfile(newProfile);
        actual = database.getProfiles();

        assertEquals(0, actual.size());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDeleteProfileThrowsWhenProfileNotInDatabase() throws IOException {
        IKey publicKey = new Ed25519Key("FTP", "key");
        IKey privateKey = new Ed25519Key("FTP", "yek");
        KeyPair keyPair = new KeyPair("FTP", publicKey, privateKey);
        Profile profile = new Profile("CoolGuy", keyPair);
        database.deleteProfile(profile);
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
