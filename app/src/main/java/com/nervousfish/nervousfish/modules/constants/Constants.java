package com.nervousfish.nervousfish.modules.constants;

import com.nervousfish.nervousfish.service_locator.IServiceLocator;
import com.nervousfish.nervousfish.service_locator.ModuleWrapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;

/**
 * Class implementing the main constants for the application.
 */
public final class Constants implements IConstants {

    private static final long serialVersionUID = -2465684567600871939L;
    private static final Logger LOGGER = LoggerFactory.getLogger("Constants");
    private final static String DB_USERDATA_PATH = "/accountInformation.json";
    private final static String DB_CONTACTS_PATH = "/contacts.json";
    private final IServiceLocator serviceLocator;
    private transient final String androidFilesDir;

    /**
     * Prevents construction from outside the class.
     *
     * @param serviceLocator Can be used to get access to other modules
     */
    private Constants(final IServiceLocator serviceLocator) {
        this.serviceLocator = serviceLocator;
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
     * Serialize the created proxy instead of this instance.
     */
    private Object writeReplace() {
        return new SerializationProxy(this);
    }

    /**
     * Ensure that no instance of this class is created because it was present in the stream. A correct
     * stream should only contain instances of the proxy.
     */
    private void readObject(final ObjectInputStream stream) throws InvalidObjectException {
        throw new InvalidObjectException("Proxy required.");
    }

    /**
     * Represents the logical state of this class and copies the data from that class without
     * any consistency checking or defensive copying.
     * Used for the Serialization Proxy Pattern.
     * We suppress here the AccessorClassGeneration warning because the only alternative to this pattern -
     * ordinary serialization - is far more dangerous
     */
    @SuppressWarnings("PMD.AccessorClassGeneration")
    private static final class SerializationProxy implements Serializable {
        private static final long serialVersionUID = -2465684567600871939L;
        private final IServiceLocator serviceLocator;

        SerializationProxy(final Constants constants) {
            this.serviceLocator = constants.serviceLocator;
        }

        private Object readResolve() {
            return new Constants(this.serviceLocator);
        }
    }
}
