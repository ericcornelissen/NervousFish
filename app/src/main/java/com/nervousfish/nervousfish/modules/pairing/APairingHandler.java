package com.nervousfish.nervousfish.modules.pairing;

import com.nervousfish.nervousfish.annotations.DesignedForExtension;
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
    @DesignedForExtension
    public PairingWrapper<IDataReceiver> getDataReceiver() {
        return new PairingWrapper<IDataReceiver>(new IDataReceiver() {
            @Override
            public void dataReceived(final byte[] bytes) {
                try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
                     ObjectInputStream ois = new ObjectInputStream(bis)) {
                    final DataWrapper object = (DataWrapper) ois.readObject();
                    APairingHandler.this.serviceLocator.postOnEventBus(new NewDataReceivedEvent(object.getData(), object.getClazz()));
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
    public void send(final Serializable object) throws IOException {
        LOGGER.info("Begin writing object");
        final byte[] bytes;
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(bos)) {
            oos.writeObject(new DataWrapper(object));
            oos.flush();
            bytes = bos.toByteArray();
        }
        this.send(bytes);
    }

    protected final IServiceLocator getServiceLocator() {
        return this.serviceLocator;
    }
}
