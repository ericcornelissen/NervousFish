package com.nervousfish.nervousfish.modules.constants;

import com.nervousfish.nervousfish.service_locator.IServiceLocator;
import com.nervousfish.nervousfish.service_locator.ModuleWrapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class implementing the main constants for the application.
 */
public final class Constants implements IConstants {

    private static final Logger LOGGER = LoggerFactory.getLogger("Constants");
    private final static String DB_USERDATA_PATH = "accountInformation.json";
    private final static String DB_CONTACTS_PATH = "contacts.json";
    private static final int QR_CODE_WIDTH = 400;
    private static final int QR_CODE_HEIGHT = 400;
    private final String androidFilesDir;

    /**
     * Prevents construction from outside the class.
     *
     * @param serviceLocator Can be used to get access to other modules
     */
    private Constants(final IServiceLocator serviceLocator) {
        this.androidFilesDir = serviceLocator.getAndroidFilesDir();
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
    public String getDatabaseContactsPath() {
        return this.androidFilesDir + Constants.DB_CONTACTS_PATH;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDatabaseUserdataPath() {
        return this.androidFilesDir + Constants.DB_USERDATA_PATH;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getQRCodeWidth() {
        return Constants.QR_CODE_WIDTH;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getQRCodeHeight() {
        return Constants.QR_CODE_HEIGHT;
    }

}
