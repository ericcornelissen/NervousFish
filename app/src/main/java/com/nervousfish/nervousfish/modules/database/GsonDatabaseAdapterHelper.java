package com.nervousfish.nervousfish.modules.database;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.nervousfish.nervousfish.data_objects.DatabasePass;
import com.nervousfish.nervousfish.data_objects.IKey;
import com.nervousfish.nervousfish.modules.cryptography.EncryptedSaver;
import com.nervousfish.nervousfish.modules.filesystem.IFileSystem;
import com.nervousfish.nervousfish.service_locator.IServiceLocator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Type;

/**
 * Helper method for the logical functionality of the {@link GsonDatabaseAdapter}.
 */
class GsonDatabaseAdapterHelper implements Serializable {

    private static final long serialVersionUID = -4101015873770268925L;

    private static final Type TYPE_DATABASE_PASS = new TypeToken<DatabasePass>() {
    }.getType();
    private final IFileSystem fileSystem;
    private final String databasePath;
    private final String passwordPath;


    /**
     * Helper class for GsonDatabaseAdapter.
     * @param serviceLocator Can be used to get access to other modules
     */
    GsonDatabaseAdapterHelper(final IServiceLocator serviceLocator) {
        this.fileSystem = serviceLocator.getFileSystem();
        this.databasePath = serviceLocator.getConstants().getDatabasePath();
        this.passwordPath = serviceLocator.getConstants().getPasswordPath();

    }

    /**
     * Reads the database to a string.
     * @return A String containing the json database
     */
    String readDatabaseToString() throws IOException {
        // Get the database from the database file
        final BufferedReader databaseReader = (BufferedReader) this.fileSystem.getReader(databasePath);

        final StringBuffer databaseFileStringBuffer = new StringBuffer();
        while (true) {
            final String line = databaseReader.readLine();
            if (line == null) {
                break;
            }
            databaseFileStringBuffer.append(line);
        }
        databaseReader.close();
        return databaseFileStringBuffer.toString();
    }

    /**
     * Checks the password, throws IOException if it's wrong.
     * @param password  -   The password to check.
     * @throws IOException Thrown when password is wrong.
     */
    void checkPassword(final String password) throws IOException {
        final GsonBuilder gsonBuilder = new GsonBuilder().registerTypeHierarchyAdapter(IKey.class, new GsonKeyAdapter());
        final Gson gsonParser = gsonBuilder.create();
        final BufferedReader passReader = (BufferedReader) this.fileSystem.getReader(passwordPath);
        final StringBuffer passwordFileStringBuffer = new StringBuffer();
        while (true) {
            final String line = passReader.readLine();
            if (line == null) {
                break;
            }
            passwordFileStringBuffer.append(line);
        }
        final String passwordFileString = passwordFileStringBuffer.toString();
        final DatabasePass databasePass = gsonParser.fromJson(passwordFileString, TYPE_DATABASE_PASS);
        if (!databasePass.getEncryptedPassword().equals(EncryptedSaver.hashWithoutSalt(password))) {
            throw new IOException("Password is wrong " + databasePass.getEncryptedPassword() + " " + EncryptedSaver.hashWithoutSalt(password));
        }
        passReader.close();
    }
}
