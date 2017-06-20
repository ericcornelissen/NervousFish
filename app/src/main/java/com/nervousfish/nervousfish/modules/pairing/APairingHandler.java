package com.nervousfish.nervousfish.modules.pairing;

import com.nervousfish.nervousfish.exceptions.SerializationException;
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
    public final PairingWrapper<IDataReceiver> getDataReceiver() {
        return new PairingWrapper<>(bytes -> {
            try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
                 ObjectInputStream ois = new ObjectInputStream(bis)) {
                final DataWrapper object = (DataWrapper) ois.readObject();
                this.serviceLocator.postOnEventBus(new NewDataReceivedEvent(object.getData(), object.getClazz()));
            } catch (final ClassNotFoundException | IOException e) {
                LOGGER.error(" Couldn't start deserialization!", e);
                throw new DeserializationException(e);
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final byte[] objectToBytes(final Serializable object) {
        LOGGER.info("Begin serializing object: {}", object);
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(bos)) {
            oos.writeObject(new DataWrapper(object));
            oos.flush();
            return bos.toByteArray();
        } catch (final IOException e) {
            LOGGER.error("Couldn't serialize object", e);
            throw new SerializationException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void send(final Serializable object) {
        LOGGER.info("Begin writing object: {}", object);
        this.send(this.objectToBytes(object));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void send(final Serializable object, final long key) {
        LOGGER.info("Begin writing object encoded with key: {}", key);
        this.send(this.objectToBytes(object));
    }

    protected final IServiceLocator getServiceLocator() {
        return this.serviceLocator;
    }
}
