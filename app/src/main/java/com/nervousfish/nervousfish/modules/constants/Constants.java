package com.nervousfish.nervousfish.modules.constants;

import com.nervousfish.nervousfish.events.SLReadyEvent;
import com.nervousfish.nervousfish.service_locator.IServiceLocator;
import com.nervousfish.nervousfish.service_locator.IServiceLocatorCreator;
import com.nervousfish.nervousfish.service_locator.ModuleWrapper;

import org.greenrobot.eventbus.Subscribe;

/**
 * Class implementing the main constants for the application.
 */
public final class Constants implements IConstants {

    private final static String DB_USERDATA_PATH = "/accountInformation.json";
    private final static String DB_CONTACTS_PATH = "/contacts.json";
    private String fileDir;

    @SuppressWarnings("PMD.SingularField")
    private final IServiceLocatorCreator serviceLocatorCreator;

    /**
     * Prevents construction from outside the class.
     *
     * @param serviceLocatorCreator The object responsible for creating the service locator
     */
    private Constants(final IServiceLocatorCreator serviceLocatorCreator) {
        this.serviceLocatorCreator = serviceLocatorCreator;
        this.serviceLocatorCreator.registerToEventBus(this);
    }

    /**
     * {@inheritDoc}
     */
    @Subscribe
    @Override
    public void onSLReadyEvent(final SLReadyEvent event) {
        // Here you can get modules from the service locator
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
