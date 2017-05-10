package com.nervousfish.nervousfish.modules.constants;

import com.nervousfish.nervousfish.service_locator.IServiceLocator;
import com.nervousfish.nervousfish.service_locator.ModuleWrapper;

/**
 * Class implementing the main constants for the application.
 */
public final class Constants implements IConstants {

    private final static String DB_USERDATA_PATH = "/accountInformation.json";
    private final static String DB_CONTACTS_PATH = "/contacts.json";
    private String fileDir;

    /**
     * Prevents construction from outside the class.
     *
     * @param serviceLocator Can be used to get access to other modules
     */
    @SuppressWarnings("PMD.UnusedFormalParameter") // This servicelocator will be used later on probably
    private Constants(final IServiceLocator serviceLocator) {
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
        return getFileDir() + Constants.DB_CONTACTS_PATH;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDatabaseUserdataPath() {
        return getFileDir() + Constants.DB_USERDATA_PATH;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getFileDir() {
        return this.fileDir;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setFileDir(final String FileDir) {
        this.fileDir = FileDir;
    }

}
