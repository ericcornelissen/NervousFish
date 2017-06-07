package com.nervousfish.nervousfish.modules.pairing;

import com.nervousfish.nervousfish.modules.pairing.events.NewDataReceivedEvent;
import com.nervousfish.nervousfish.service_locator.IServiceLocator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Contains common methods shared by all pairing modules to reduce code duplication.
 */
@SuppressWarnings("checkstyle:classdataabstractioncoupling")
abstract class APairingHandler implements IPairingHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger("APairingHandler");
    private static final long serialVersionUID = 1656974573024980860L;

    private final IServiceLocator serviceLocator;

    /**
     * Prevent instantiation by other classes outside it's package
     *
     * @param serviceLocator the serviceLocator which will provide means to access other modules
     */
    APairingHandler(final IServiceLocator serviceLocator) {
        this.serviceLocator = serviceLocator;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PairingWrapper<IDataReceiver> getDataReceiver() {
        return new PairingWrapper<IDataReceiver>(new IDataReceiver() {
            @Override
            public void dataReceived(final byte[] bytes) {
                final DataWrapper object;
                try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
                     ObjectInputStream ois = new ObjectInputStream(bis)) {
                    object = (DataWrapper) ois.readObject();
                    serviceLocator.postOnEventBus(new NewDataReceivedEvent(object.getData(), object.getClazz()));
                } catch (final ClassNotFoundException | IOException e) {
                    LOGGER.error(" Couldn't start deserialization!", e);
                }
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] objectToBytes(final Serializable object) throws IOException {
        LOGGER.info("Begin serializing object:" + object);
        final byte[] bytes;
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(bos)) {
            oos.writeObject(new DataWrapper(object));
            oos.flush();
            bytes = bos.toByteArray();
        }
        return bytes;
    }

    protected IServiceLocator getServiceLocator() {
        return this.serviceLocator;
    }
}
