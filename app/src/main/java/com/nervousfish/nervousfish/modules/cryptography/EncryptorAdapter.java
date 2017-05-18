package com.nervousfish.nervousfish.modules.cryptography;

import com.nervousfish.nervousfish.service_locator.IServiceLocator;
import com.nervousfish.nervousfish.service_locator.ModuleWrapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;

/**
 * An adapter to the default Java class for encrypting messages
 */
public final class EncryptorAdapter implements IEncryptor {
    private static final long serialVersionUID = 5930930748980177440L;
    private static final Logger LOGGER = LoggerFactory.getLogger("EncryptorAdapter");
    private final IServiceLocator serviceLocator;

    /**
     * Prevents construction from outside the class.
     *
     * @param serviceLocator Can be used to get access to other modules
     */
    @SuppressWarnings("PMD.UnusedFormalParameter")
    // This servicelocator will be used later on probably
    private EncryptorAdapter(final IServiceLocator serviceLocator) {
        LOGGER.info("Initialized");
        this.serviceLocator = serviceLocator;
    }

    /**
     * Creates a new instance of itself and wraps it in a {@link ModuleWrapper} so that only an
     * {@link IServiceLocator}
     *
     * @param serviceLocator The new service locator
     * @return A wrapper around a newly created instance of this class
     */
    public static ModuleWrapper<EncryptorAdapter> newInstance(final IServiceLocator serviceLocator) {
        return new ModuleWrapper<>(new EncryptorAdapter(serviceLocator));
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
     */
    @SuppressWarnings("PMD.AccessorClassGeneration")
    private static final class SerializationProxy implements Serializable {
        private static final long serialVersionUID = 5930930748980177440L;
        private final IServiceLocator serviceLocator;

        SerializationProxy(final EncryptorAdapter encryptorAdapter) {
            this.serviceLocator = encryptorAdapter.serviceLocator;
        }

        private Object readResolve() {
            return new EncryptorAdapter(this.serviceLocator);
        }
    }
}
