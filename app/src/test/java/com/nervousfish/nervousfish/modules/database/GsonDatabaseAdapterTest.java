package com.nervousfish.nervousfish.modules.database;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nervousfish.nervousfish.BaseTest;
import com.nervousfish.nervousfish.data_objects.Contact;
import com.nervousfish.nervousfish.data_objects.Database;
import com.nervousfish.nervousfish.data_objects.IKey;
import com.nervousfish.nervousfish.data_objects.KeyPair;
import com.nervousfish.nervousfish.data_objects.Profile;
import com.nervousfish.nervousfish.data_objects.SimpleKey;
import com.nervousfish.nervousfish.modules.constants.IConstants;
import com.nervousfish.nervousfish.modules.cryptography.KeyGeneratorAdapter;
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

import static com.nervousfish.nervousfish.BaseTest.accessConstructor;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GsonDatabaseAdapterTest {

    private static final String DB_DATABASE_PATH = "database.txt";
    private static final String DB_PASSWORD_PATH = "databaseKey.txt";
    private static final String TEST_PASSWORD = "TESTPASS";

    private IServiceLocator serviceLocator = mock(IServiceLocator.class);
    private IConstants constants = mock(IConstants.class);

    private IDatabase database;
    private Database databaseObject;
    private Profile profile;
    private boolean encryptionCalled = false;

    @Before
    public void setup() {

        when(serviceLocator.getConstants()).thenReturn(constants);
        when(serviceLocator.getFileSystem()).thenReturn((AndroidFileSystemAdapter) accessConstructor(AndroidFileSystemAdapter.class, serviceLocator));
        when(constants.getDatabasePath()).thenReturn(DB_DATABASE_PATH);
        when(constants.getPasswordPath()).thenReturn(DB_PASSWORD_PATH);

        database = (GsonDatabaseAdapter) accessConstructor(GsonDatabaseAdapter.class, serviceLocator);

        KeyGeneratorAdapter keyGen = (KeyGeneratorAdapter) accessConstructor(KeyGeneratorAdapter.class, serviceLocator);
        KeyPair keyPair = keyGen.generateRSAKeyPair("Test");
        Contact contact = new Contact("name", new ArrayList<IKey>());
        profile = new Profile(contact, new ArrayList<KeyPair>());
        profile.addKeyPair(keyPair);
        databaseObject = new Database(new ArrayList<Contact>(), profile);

    }

    @After
    public void tearDown() {
        File contactsFile = new File(DB_DATABASE_PATH);
        contactsFile.delete();
        File userdataFile = new File(DB_PASSWORD_PATH);
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

/*    @Test
    public void testAddContactWithSingleKey() throws Exception {


        final GsonBuilder gsonBuilder = new GsonBuilder().registerTypeHierarchyAdapter(IKey.class, BaseTest.accessConstructor(GsonKeyAdapter.class));
        final Gson gsonParser = gsonBuilder.create();

        //PowerMockito.mockStatic(Base64.class);

        database.createDatabase(profile, TEST_PASSWORD);
        database.loadDatabase(TEST_PASSWORD);

        Contact contact = new Contact("Test", new SimpleKey("Webmail", "key"));
        database.addContact(contact);


        ArrayList<Contact> testContacts = new ArrayList<>();
        testContacts.add(contact);
        databaseObject.setContacts(testContacts);
        assertEquals(gsonParser.toJson(databaseObject), read(DB_DATABASE_PATH));

    }*/

    /*
    @Test
    public void testAddContactWithSingleKeyWriteToDatabase() throws Exception {
        IKey key = new SimpleKey("Webmail", "key");
        Contact contact = new Contact("Zoidberg", key);

        database.addContact(contact);
        assertEquals("[{\"name\":\"Zoidberg\",\"keys\":[[\"simple\"," +
                "{\"name\":\"Webmail\",\"key\":\"key\"}]]}]\n", read(DB_DATABASE_PATH));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddDuplicateContact() throws Exception {
        IKey key = new SimpleKey("Webmail", "key");
        Contact contact = new Contact("Zoidberg", key);

        database.addContact(contact);
        database.addContact(contact);
    }

    @Test
    public void testAddContactWithMultipleKeysWriteToDatabase() throws Exception {
        Collection<IKey> keys = new ArrayList<>();
        keys.add(new RSAKey("FTP", "A", "B"));
        keys.add(new SimpleKey("Webserver", "keyB"));
        Contact contact = new Contact("Zoidberg", keys);

        database.addContact(contact);
        assertEquals("[{\"name\":\"Zoidberg\",\"keys\":[[\"RSA\",{\"name\":\"FTP\",\"modulus\":\"A\",\"exponent\":\"B\"}]," +
                "[\"simple\",{\"name\":\"Webserver\",\"key\":\"keyB\"}]]}]\n", read(DB_DATABASE_PATH));
    }

    @Test
    public void testDeleteContactWithSingleKeyRemovesContactFromDatabase() throws IOException {
        IKey key = new SimpleKey("FTP", "key");
        Contact contact = new Contact("Zoidberg", key);

        // Add the contact to remove from the database
        write("[{\"name\":\"Zoidberg\",\"keys\":[[\"simple\",{\"name\":\"FTP\",\"key\":\"key\"}]]}]", DB_DATABASE_PATH);

        database.deleteContact(contact.getName());
        assertEquals("[]\n", read(DB_DATABASE_PATH));
    }

    @Test
    public void testDeleteContactWithMultipleKeysRemovesContactFromDatabase() throws IOException {
        Collection<IKey> keys = new ArrayList<>();
        keys.add(new SimpleKey("Webserver", "keyA"));
        keys.add(new SimpleKey("Webmail", "keyB"));
        Contact contact = new Contact("Zoidberg", keys);

        // Add the contact to remove from the database
        write("[{\"name\":\"Zoidberg\",\"keys\":[[\"simple\",{\"name\":\"Webserver\",\"key\":\"keyA\"}]," +
                "[\"simple\",{\"name\":\"Webmail\",\"key\":\"keyB\"}]]}]", DB_DATABASE_PATH);

        database.deleteContact(contact.getName());
        assertEquals("[]\n", read(DB_DATABASE_PATH));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDeleteContactThrowsWhenContactNotInDatabase() throws IOException {
        write("[{\"name\":\"Zoidberg\",\"keys\":[[\"simple\",{\"name\":\"Webserver\",\"key\":\"keyA\"}]," +
                "[\"simple\",{\"name\":\"Webmail\",\"key\":\"keyB\"}]]}]", DB_DATABASE_PATH);

        IKey key = new SimpleKey(     "Burpie", "key");
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
        keys.add(new SimpleKey("FTP", "keyA"));
        keys.add(new SimpleKey("Webmail", "keyB"));
        Contact contact = new Contact("Zoidberg", keys);

        List<Contact> expected = new ArrayList<>();
        expected.add(contact);

        write("[{\"name\":\"Zoidberg\",\"keys\":[[\"simple\",{\"name\":\"FTP\",\"key\":\"keyA\"}]," +
                "[\"simple\",{\"name\":\"Webmail\",\"key\":\"keyB\"}]]}]", DB_DATABASE_PATH);

        List<Contact> actual = database.getAllContacts();
        assertEquals(expected, actual);
    }

    @Test
    public void testGetAllContactsReturnsContactWithMultipleKeys() throws IOException {
        write("[{\"name\":\"Zoidberg\",\"keys\":[[\"simple\",{\"name\":\"FTP\",\"key\":\"key\"}]]}]", DB_DATABASE_PATH);

        IKey key = new SimpleKey("FTP", "key");
        Contact contact = new Contact("Zoidberg", key);
        List<Contact> expected = new ArrayList<>();
        expected.add(contact);

        List<Contact> actual = database.getAllContacts();
        assertEquals(expected, actual);
    }

    @Test
    public void testGetAllContactsReturnsListOfAllContactsWith2Contacts() throws IOException {
        write("[{\"name\":\"Zoidberg\",\"keys\":[[\"RSA\",{\"name\":\"FTP\",\"modulus\":\"A\",\"exponent\":\"B\"}]]}," +
                "{\"name\":\"Fry\",\"keys\":[[\"simple\",{\"name\":\"Webmail\",\"key\":\"BABABA\"}]]}]", DB_DATABASE_PATH);

        IKey zoidbergsKey = new RSAKey("FTP", "A", "B");
        Contact zoidberg = new Contact("Zoidberg", zoidbergsKey);
        IKey frysKey = new SimpleKey("Webmail", "BABABA");
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
        write("[{\"name\":\"Zoidberg\",\"keys\":[[\"not a valid key\",{\"key\":\"key\"}]]}]", DB_DATABASE_PATH);
        database.getAllContacts();
    }

    @Test
    public void testImplementedWritesToDatabase() throws IOException {
        IKey keyA = new SimpleKey("Webserver", "keyA");
        Contact oldContact = new Contact("Zoidberg", keyA);

        // Add the contact to remove from the database
        write("[{\"name\":\"Zoidberg\",\"keys\":[[\"simple\",{\"name\":\"Webserver\"," +
                "\"key\":\"keyA\"}]]}]", DB_DATABASE_PATH);

        IKey keyB = new SimpleKey("FTP", "keyB");
        Contact newContact = new Contact("not Zoidberg", keyB);

        database.updateContact(oldContact, newContact);
        assertEquals("[{\"name\":\"not Zoidberg\",\"keys\":[[\"simple\",{\"name\":\"FTP\"," +
                "\"key\":\"keyB\"}]]}]\n", read(DB_DATABASE_PATH));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUpdateContactThrowsWhenOldContactNotInDatabase() throws IOException {
        IKey keyA = new SimpleKey("Webmail", "keyA");
        Contact oldContact = new Contact("Zoidberg", keyA);
        IKey keyB = new SimpleKey("FTP", "keyB");
        Contact newContact = new Contact("not Zoidberg", keyB);

        database.updateContact(oldContact, newContact);
    }

*/
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
