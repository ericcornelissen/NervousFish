package com.nervousfish.nervousfish;

import com.nervousfish.nervousfish.data_objects.KeyPair;
import com.nervousfish.nervousfish.data_objects.Profile;
import com.nervousfish.nervousfish.modules.cryptography.KeyGeneratorAdapter;
import com.nervousfish.nervousfish.modules.database.IDatabase;
import com.nervousfish.nervousfish.service_locator.NervousFish;

import java.util.ArrayList;

import cucumber.api.CucumberOptions;
import cucumber.api.java.After;
import cucumber.api.java.Before;

import static com.nervousfish.nervousfish.BaseTest.accessConstructor;

@CucumberOptions(features = "features")
public class Initialization {

    @Before
    public void createDatabase() throws Exception {
        final IDatabase database = NervousFish.getServiceLocator().getDatabase();
        KeyGeneratorAdapter keyGen = (KeyGeneratorAdapter) accessConstructor(KeyGeneratorAdapter.class, NervousFish.getServiceLocator());
        KeyPair keyPair = keyGen.generateRSAKeyPair("Test");
        Profile profile = new Profile("name", new ArrayList<>());
        profile.addKeyPair(keyPair);
        database.createDatabase(profile, "Testpass");
        database.loadDatabase("Testpass");
    }

    @After
    public void deleteDatabase() {
        final IDatabase database = NervousFish.getServiceLocator().getDatabase();
        database.deleteDatabase();
    }
}
