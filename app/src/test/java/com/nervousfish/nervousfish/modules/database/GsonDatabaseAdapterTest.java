package com.nervousfish.nervousfish.modules.database;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nervousfish.nervousfish.BaseTest;
import com.nervousfish.nervousfish.data_objects.Contact;
import com.nervousfish.nervousfish.data_objects.Database;
import com.nervousfish.nervousfish.data_objects.IKey;
import com.nervousfish.nervousfish.data_objects.KeyPair;
import com.nervousfish.nervousfish.data_objects.Profile;
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
        profile = new Profile("name", new ArrayList<KeyPair>());
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
