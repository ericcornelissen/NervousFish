package com.nervousfish.nervousfish.modules.pairing;

import com.nervousfish.nervousfish.service_locator.IServiceLocator;
import com.nervousfish.nervousfish.service_locator.ModuleWrapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;

/**
 * An handler doing nothing.
 */
public final class DummyQRHandler extends APairingHandler implements IQRHandler {
    private static final long serialVersionUID = -1164062335787406761L;
    private static final Logger LOGGER = LoggerFactory.getLogger("DummyQRHandler");

    /**
     * Prevents construction from outside the class.
     *
     * @param serviceLocator Can be used to get access to other modules
     */
    private DummyQRHandler(final IServiceLocator serviceLocator) {
        super(serviceLocator);
        LOGGER.info("Initialized");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void write(final byte[] buffer) {
        //dummy
    }

    /**
     * Creates a new instance of itself and wraps it in a {@link ModuleWrapper} so that only an
     * {@link IServiceLocator}
     *
     * @param serviceLocator The new service locator
     * @return A wrapper around a newly created instance of this class
     */
    public static ModuleWrapper<DummyQRHandler> newInstance(final IServiceLocator serviceLocator) {
        return new ModuleWrapper<>(new DummyQRHandler(serviceLocator));
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
     * Represents the logical state of {@link DummyQRHandler} and copies the data from that class without
     * any consistency checking or defensive copying.
     * Used for the Serialization Proxy Pattern.
     * We suppress here the AccessorClassGeneration warning because the only alternative to this pattern -
     * ordinary serialization - is far more dangerous
     */
    @SuppressWarnings("PMD.AccessorClassGeneration")
    private static final class SerializationProxy implements Serializable {
        private static final long serialVersionUID = -1164062335787406761L;
        private final IServiceLocator serviceLocator;

        SerializationProxy(final DummyQRHandler handler) {
            this.serviceLocator = handler.getServiceLocator();
        }

        private Object readResolve() {
            return new DummyQRHandler(this.serviceLocator);
        }
    }
}
