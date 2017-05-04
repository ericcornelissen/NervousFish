package com.nervousfish.nervousfish.modules.constants;

import com.nervousfish.nervousfish.service_locator.IServiceLocator;
import com.nervousfish.nervousfish.service_locator.IServiceLocatorCreator;
import com.nervousfish.nervousfish.service_locator.ModuleWrapper;

/**
 * Class implementing the main constants for the application.
 */
public final class Constants implements IConstants {

    private final static String DB_USERDATA_PATH = "accountInformation.json";
    private final static String DB_CONTACTS_PATH = "contacts.json";

    /**
     * Prevents construction from outside the class.
     * @param serviceLocatorCreator The object responsible for creating the service locator
     */
    @SuppressWarnings("PMD.UnusedFormalParameter")
    private Constants(final IServiceLocatorCreator serviceLocatorCreator) {
        // final IServiceLocator serviceLocator = serviceLocatorCreator.getServiceLocator();
    }

    /**
     * Creates a new instance of itself and wraps it in a {@link ModuleWrapper} so that only an {@link IServiceLocatorCreator}
     * can access the new module to create the new {@link IServiceLocator}.
     *
     * @param serviceLocatorCreator The service locator bridge that creates the new service locator
     * @return A wrapper around a newly created instance of this class
     */
    public static ModuleWrapper<Constants> newInstance(final IServiceLocatorCreator serviceLocatorCreator) {
        return new ModuleWrapper<>(new Constants(serviceLocatorCreator));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDatabaseContactsPath() {
        return Constants.DB_CONTACTS_PATH;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDatabaseUserdataPath() {
        return Constants.DB_USERDATA_PATH;
    }

}
