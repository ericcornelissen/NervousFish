package com.nervousfish.nervousfish.modules.pairing;

import com.nervousfish.nervousfish.annotations.DesignedForExtension;
import com.nervousfish.nervousfish.exceptions.SerializationException;
import com.nervousfish.nervousfish.modules.constants.IConstants;
import com.nervousfish.nervousfish.modules.cryptography.IEncryptor;
import com.nervousfish.nervousfish.modules.pairing.events.NewDataReceivedEvent;
import com.nervousfish.nervousfish.modules.pairing.events.NewEncryptedBytesReceivedEvent;
import com.nervousfish.nervousfish.service_locator.IServiceLocator;

import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.SecretKey;
import java.io.StreamCorruptedException;
import java.util.Arrays;

/**
 * Contains common methods shared by all pairing modules to reduce code duplication.
 */
@SuppressWarnings({"checkstyle:classdataabstractioncoupling", "checkstyle:ClassFanOutComplexity"})
// 1 / 2) Suppressed the code base if cluttered if we split this class that's already quite small in even smaller classes
abstract class APairingHandler implements IPairingHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger("APairingHandler");
    private static final long serialVersionUID = 1656974573024980860L;

    private final IServiceLocator serviceLocator;
    private final IConstants constants;
    private final IEncryptor encryptor;
    private byte[] readBuffer = new byte[0];

    /**
     * Prevent instantiation by other classes outside it's package
     *
     * @param serviceLocator the serviceLocator which will provide means to access other modules
     */
    APairingHandler(final IServiceLocator serviceLocator) {
        this.serviceLocator = serviceLocator;
        this.constants = this.serviceLocator.getConstants();
        this.encryptor = this.serviceLocator.getEncryptor();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @DesignedForExtension
    public PairingWrapper<IDataReceiver> getDataReceiver() {
        return new PairingWrapper<>(bytes -> {
                final byte[] srcBytes = trim(bytes);
                
                final byte[] newBuffer = new byte[this.readBuffer.length + srcBytes.length];
                System.arraycopy(this.readBuffer, 0, newBuffer, 0, this.readBuffer.length);
                System.arraycopy(srcBytes, 0, newBuffer, this.readBuffer.length, srcBytes.length);

                try (ByteArrayInputStream bis = new ByteArrayInputStream(newBuffer);
                     ObjectInputStream ois = new ObjectInputStream(bis)) {

                    final DataWrapper object = (DataWrapper) ois.readObject();
                    if (object.getClazz().equals(ByteWrapper.class)) {
                        this.serviceLocator.postOnEventBus(new NewEncryptedBytesReceivedEvent(((ByteWrapper) object.getData()).getBytes()));
                    } else {
                        this.serviceLocator.postOnEventBus(new NewDataReceivedEvent(object.getData(), object.getClazz()));
                    }

                    this.readBuffer = new byte[0];
                } catch (final ClassNotFoundException | IOException e) {
                    if (e.getClass().equals(EOFException.class) || e.getClass().equals(StreamCorruptedException.class)) {
                        this.readBuffer = newBuffer;
                    } else {
                        LOGGER.error(" Couldn't start deserialization!", e);
                    }
                }
        });
    }

    /**
     * Trims a byte array to not contain any 0's at the end.
     *
     * @param bytes The byte[] to be trimmed
     * @return the newly trimmed byte[]
     */
    private static byte[] trim(final byte[] bytes) {
        int i = bytes.length - 1;
        while (i >= 0 && bytes[i] == 0) {
            --i;
        }

        return Arrays.copyOf(bytes, i + 1);
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
    public final void send(final Serializable object, final long key) throws BadPaddingException, IllegalBlockSizeException {
        LOGGER.info("Begin writing object encoded with key: {}", key);
        final byte[] bytes;
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(bos)) {
            oos.writeObject(new DataWrapper(object));
            oos.flush();
            bytes = bos.toByteArray();
        } catch (final IOException e) {
            LOGGER.error("Couldn't serialize the object", e);
            throw new SerializationException(e);
        }
        final SecretKey password = this.encryptor.makeKeyFromPassword(Long.toString(key));
        final String encryptedMessage = this.encryptor.encryptWithPassword(new String(bytes, this.constants.getCharset()), password);
        final ByteWrapper byteWrapper = new ByteWrapper(encryptedMessage.getBytes(this.constants.getCharset()));
        this.send(this.objectToBytes(byteWrapper));
    }

    protected final IServiceLocator getServiceLocator() {
        return this.serviceLocator;
    }
}
