package com.nervousfish.nervousfish.modules.database;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import com.nervousfish.nervousfish.data_objects.Account;
import com.nervousfish.nervousfish.data_objects.IKey;
import com.nervousfish.nervousfish.data_objects.RSAKey;
import com.nervousfish.nervousfish.modules.constants.IConstants;
import com.nervousfish.nervousfish.modules.filesystem.IFileSystem;
import com.nervousfish.nervousfish.service_locator.IServiceLocator;
import com.nervousfish.nervousfish.service_locator.IServiceLocatorCreator;
import com.nervousfish.nervousfish.service_locator.ModuleWrapper;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.NoSuchElementException;

/**
 * An adapter to the Gson database library
 */
public final class GsonDatabaseAdapter implements IDatabase {
    final IConstants constants;

    /**
     * Prevents construction from outside the class.
     * @param serviceLocatorCreator The object responsible for creating the service locator
     */
    @SuppressWarnings("PMD.UnusedFormalParameter")
    GsonDatabaseAdapter(final IServiceLocatorCreator serviceLocatorCreator) {
        final IServiceLocator serviceLocator = serviceLocatorCreator.getServiceLocator();
        this.constants = serviceLocator.getConstants();
    }

    /**
     * Creates a new instance of itself and wraps it in a {@link ModuleWrapper} so that only an {@link IServiceLocatorCreator}
     * can access the new module to create the new {@link IServiceLocator}.
     *
     * @param serviceLocatorCreator The service locator bridge that creates the new service locator
     * @return A wrapper around a newly created instance of this class
     */
    public static ModuleWrapper<GsonDatabaseAdapter> newInstance(final IServiceLocatorCreator serviceLocatorCreator) {
        return new ModuleWrapper<>(new GsonDatabaseAdapter(serviceLocatorCreator));
    }

    /**
     * Will initialize the database files for accountInformation and contacts list.
     */
    private void initializeDatabaseFiles() {
        try {
            //Initialize the account information file
            BufferedWriter bufferedWriter = new BufferedWriter(
                    new FileWriter(constants.getAccountInformationFileName()));
            bufferedWriter.write("[]");
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Will get account info from the file and return as an Account[].
     * @return an Account array
     */
    private Account[] getAccountInfo() {
        Account[] accountArray = {};
        try {
            BufferedReader br = new BufferedReader(new FileReader(constants.getAccountInformationFileName()));
            JsonParser parser = new JsonParser();
            JsonArray jsonArray = parser.parse(br).getAsJsonArray();
            accountArray = new Account[jsonArray.size()];

            for (int i = 0; i < jsonArray.size(); i++) {
                JsonObject jsonObject = jsonArray.get(i).getAsJsonObject();
                accountArray[i] = createAccountFromJsonObject(jsonObject);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return accountArray;
    }

    private Account createAccountFromJsonObject(JsonObject jsonObject) {
        JsonObject publicKeyJson = jsonObject.get("publicKey").getAsJsonObject();
        IKey publicKey = createKeyFromJsonObject(publicKeyJson);

        JsonObject privateKeyJson = jsonObject.get("privateKey").getAsJsonObject();
        IKey privateKey = createKeyFromJsonObject(privateKeyJson);

        return new Account(jsonObject.get("name").getAsString(), publicKey, privateKey);
    }

    private IKey createKeyFromJsonObject(JsonObject jsonObject) {
        if(jsonObject.get("type").getAsString().equals("RSA")) {
            return new RSAKey(jsonObject.get("modulus").getAsBigInteger(),
                    jsonObject.get("exponent").getAsBigInteger());
        }

        throw new NoSuchElementException("The key type " + jsonObject.get("type").getAsString() +
            "does not exist");
    }
}
