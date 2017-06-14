package com.nervousfish.nervousfish.modules.pairing;

import com.nervousfish.nervousfish.annotations.DesignedForExtension;
import com.nervousfish.nervousfish.modules.pairing.events.NewDataReceivedEvent;
import com.nervousfish.nervousfish.service_locator.IServiceLocator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.StreamCorruptedException;
import java.util.Arrays;

/**
 * Contains common methods shared by all pairing modules to reduce code duplication.
 */
@SuppressWarnings("checkstyle:classdataabstractioncoupling")
abstract class APairingHandler implements IPairingHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger("APairingHandler");
    private static final long serialVersionUID = 1656974573024980860L;

    private final IServiceLocator serviceLocator;
    private byte[] readBuffer = new byte[0];

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
                byte[] newBuffer = trim(bytes);
                newBuffer = new byte[readBuffer.length + bytes.length];
                System.arraycopy(readBuffer, 0, newBuffer, 0, readBuffer.length);
                System.arraycopy(bytes, 0, newBuffer, readBuffer.length, bytes.length);

                System.out.println(newBuffer);
                System.out.println(newBuffer.length);
                try (ByteArrayInputStream bis = new ByteArrayInputStream(newBuffer);
                     ObjectInputStream ois = new ObjectInputStream(bis)) {

                    final DataWrapper object = (DataWrapper) ois.readObject();
                    APairingHandler.this.serviceLocator.postOnEventBus(new NewDataReceivedEvent(object.getData(), object.getClazz()));

                    readBuffer = new byte[0];
                } catch (final ClassNotFoundException | IOException e) {
                    if (e.getClass().equals(EOFException.class) || e.getClass().equals(StreamCorruptedException.class)) {
                        final byte[] newReadBuffer = new byte[readBuffer.length + bytes.length];
                        System.arraycopy(readBuffer, 0, newReadBuffer, 0, readBuffer.length);
                        System.arraycopy(bytes, 0, newReadBuffer, readBuffer.length, bytes.length);
                        readBuffer = newReadBuffer;
                    } else {
                        LOGGER.error(" Couldn't start deserialization!", e);
                    }
                }
            }
        });
    }

    static byte[] trim(byte[] bytes)
    {
        int i = bytes.length - 1;
        while (i >= 0 && bytes[i] == 0)
        {
            --i;
        }

        return Arrays.copyOf(bytes, i + 1);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final byte[] objectToBytes(final Serializable object) throws IOException {
        LOGGER.info("Begin serializing object: {}", object);
        final byte[] bytes;
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(bos)) {
            oos.writeObject(new DataWrapper(object));
            oos.flush();
            bytes = bos.toByteArray();
        }
        return bytes;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void send(final Serializable object) throws IOException {
        LOGGER.info("Begin writing object: {}", object);
        this.send(this.objectToBytes(object));
    }

    protected final IServiceLocator getServiceLocator() {
        return this.serviceLocator;
    }
}
