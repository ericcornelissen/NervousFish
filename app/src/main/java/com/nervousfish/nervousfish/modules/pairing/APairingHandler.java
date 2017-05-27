package com.nervousfish.nervousfish.modules.pairing;

import com.nervousfish.nervousfish.data_objects.DataWrapper;
import com.nervousfish.nervousfish.events.NewDataReceivedEvent;
import com.nervousfish.nervousfish.exceptions.DeserializationException;
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
 * Contains common methods shared by all pairing modules.
 */
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
     * Serializes an object and sends it by using the subclass specific method
     *
     * @param object The object to serialize
     * @throws IOException When deserialization goes wrong
     */
    void send(final Serializable object) throws IOException {
        LOGGER.info("Begin writing object");
        final byte[] bytes;
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(bos)) {
            oos.writeObject(object);
            oos.flush();
            bytes = bos.toByteArray();
        }
        send(bytes);
    }

    protected IServiceLocator getServiceLocator() {
        return this.serviceLocator;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ReceiverWrapper getDataReceiver() {
        return new ReceiverWrapper(new IDataReceiver() {
            @Override
            public void dataReceived(final byte[] bytes) {
                final DataWrapper object;
                try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
                     ObjectInputStream ois = new ObjectInputStream(bis)) {

                    object = (DataWrapper) ois.readObject();
                } catch (final ClassNotFoundException | IOException e) {
                    LOGGER.error(" Couldn't start deserialization!", e);
                    throw new DeserializationException(e);
                }
                serviceLocator.postOnEventBus(new NewDataReceivedEvent(object.getData(), object.getClazz()));
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public abstract void send(byte[] buffer);
}
