package com.nervousfish.nervousfish.modules.constants;

import com.nervousfish.nervousfish.service_locator.IServiceLocator;
import com.nervousfish.nervousfish.service_locator.ModuleWrapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

/**
 * Class containing global constants for the application. These constants are definied in this file, because
 * they're used by multiple files and don't logically belong to any one of them.
 */
public final class Constants implements IConstants {

    private static final Logger LOGGER = LoggerFactory.getLogger("Constants");
    private static final String DB_USERDATA_PATH = "accountInformation.json";
    private static final String DB_CONTACTS_PATH = "contacts.json";
    private static final String DB_DATABASE_PATH = "database.txt";
    private static final String DB_PASSWORD_PATH = "databaseKey.txt";
    // Unique secure uuid for this application
    private static final UUID MY_UUID = UUID.fromString("2d7c6682-3b84-4d00-9e61-717bac0b2643");
    // Name for the SDP record when creating server socket
    private static final String NAME_SDP_RECORD = "BluetoothChatSecure";
    private static final Charset CHARSET = StandardCharsets.UTF_8;
    private final String androidFilesDir;
    private final String databaseContactsPath;
    private final String databaseUserPath;

    /**
     * Prevents construction from outside the class.
     *
     * @param serviceLocator Can be used to get access to other modules
     */
    private Constants(final IServiceLocator serviceLocator) {
        this.androidFilesDir = serviceLocator.getAndroidFilesDir();
        //noinspection StringConcatenationMissingWhitespace because this is a file path
        this.databaseContactsPath = this.androidFilesDir + Constants.DB_CONTACTS_PATH;
        //noinspection StringConcatenationMissingWhitespace because this is a file path
        this.databaseUserPath = this.androidFilesDir + Constants.DB_USERDATA_PATH;
        LOGGER.info("Initialized");
    }

    /**
     * Creates a new instance of itself and wraps it in a {@link ModuleWrapper} so that only an
     * {@link IServiceLocator}
     *
     * @param serviceLocator The new service locator
     * @return A wrapper around a newly created instance of this class
     */
    public static ModuleWrapper<Constants> newInstance(final IServiceLocator serviceLocator) {
        return new ModuleWrapper<>(new Constants(serviceLocator));
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String getPasswordPath() {
        return this.androidFilesDir + Constants.DB_PASSWORD_PATH;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDatabasePath() {
        return this.androidFilesDir + Constants.DB_DATABASE_PATH;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDatabaseContactsPath() {
        return this.databaseContactsPath;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDatabaseUserdataPath() {
        return this.databaseUserPath;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UUID getUuid() {
        return MY_UUID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getSDPRecord() {
        return NAME_SDP_RECORD;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Charset getCharset() {
        return CHARSET;
    }

    /**
     * The general result codes for inputfield checks.
     */
    public enum InputFieldResultCodes {
        CORRECT_FIELD, EMPTY_FIELD, TOO_SHORT_FIELD
    }

    /**
     * The more explicit result codes for inputfield checks.
     */
    public enum ExplicitFieldResultCodes {
        INPUT_CORRECT, PASSWORD_TOO_SHORT, PASSWORD_EMPTY, NAME_EMPTY,
        PASSWORDS_NOT_EQUAL, ALL_FIELDS_EMPTY
    }

}
