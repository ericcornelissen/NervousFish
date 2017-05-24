package com.nervousfish.nervousfish;

import com.nervousfish.nervousfish.data_objects.Contact;
import com.nervousfish.nervousfish.data_objects.Profile;
import com.nervousfish.nervousfish.data_objects.SimpleKey;
import com.nervousfish.nervousfish.modules.constants.IConstants;
import com.nervousfish.nervousfish.modules.cryptography.IEncryptor;
import com.nervousfish.nervousfish.modules.cryptography.IKeyGenerator;
import com.nervousfish.nervousfish.modules.database.IDatabase;
import com.nervousfish.nervousfish.modules.filesystem.IFileSystem;
import com.nervousfish.nervousfish.modules.pairing.IBluetoothHandler;
import com.nervousfish.nervousfish.modules.pairing.INfcHandler;
import com.nervousfish.nervousfish.modules.pairing.IQRHandler;
import com.nervousfish.nervousfish.service_locator.IServiceLocator;
import com.nervousfish.nervousfish.test.LoginActivitySteps;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TestServiceLocator implements IServiceLocator {

    private IDatabase database = new IDatabase() {

        private List<Contact> contacts = new ArrayList() {{
            add(new Contact("Henk", new SimpleKey("Webserver", "AoghnSJIoihGIOJowigKSDGhE")));
            add(new Contact("Karel", new SimpleKey("FTP", "EFJOIjwogSJOFIhwgijSdohio")));
            add(new Contact("Peter", new SimpleKey("PGP", "qweRTyasDffOobARlefaoLjWZ")));
        }};

        @Override
        public void addContact(Contact contact) throws IOException {

        }

        @Override
        public void deleteContact(String contactName) throws IllegalArgumentException, IOException {

        }

        @Override
        public void updateContact(Contact oldContact, Contact newContact) throws IllegalArgumentException, IOException {

        }

        @Override
        public List<Contact> getAllContacts() throws IOException {
            return this.contacts;
        }

        @Override
        public Contact getContactWithName(String contactName) throws IOException {
            return null;
        }

        @Override
        public boolean contactExtists(String name) throws IOException {
            return false;
        }

        @Override
        public List<Profile> getProfiles() throws IOException {
            return null;
        }

        @Override
        public void addProfile(Profile profile) throws IOException {

        }

        @Override
        public void deleteProfile(Profile profile) throws IllegalArgumentException, IOException {

        }

        @Override
        public void updateProfile(Profile oldProfile, Profile newProfile) throws IllegalArgumentException, IOException {

        }

        @Override
        public String getUserPassword() throws IOException {
            return LoginActivitySteps.CORRECT_PASSWORD;
        }
    };

    @Override
    public String getAndroidFilesDir() {
        return null;
    }

    @Override
    public IDatabase getDatabase() {
        return this.database;
    }

    @Override
    public IKeyGenerator getKeyGenerator() {
        return null;
    }

    @Override
    public IEncryptor getEncryptor() {
        return null;
    }

    @Override
    public IFileSystem getFileSystem() {
        return null;
    }

    @Override
    public IConstants getConstants() {
        return null;
    }

    @Override
    public IBluetoothHandler getBluetoothHandler() {
        return null;
    }

    @Override
    public INfcHandler getNFCHandler() {
        return null;
    }

    @Override
    public IQRHandler getQRHandler() {
        return null;
    }

    @Override
    public void registerToEventBus(Object object) {

    }

    @Override
    public void unregisterFromEventBus(Object object) {

    }

    @Override
    public void postOnEventBus(Object message) {

    }

}
