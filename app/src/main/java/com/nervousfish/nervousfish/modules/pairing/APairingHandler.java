package com.nervousfish.nervousfish.modules.pairing;

import com.nervousfish.nervousfish.annotations.DesignedForExtension;
import com.nervousfish.nervousfish.exceptions.SerializationException;
import com.nervousfish.nervousfish.modules.pairing.events.NewDataReceivedEvent;
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
import javax.crypto.NoSuchPaddingException;
import java.io.StreamCorruptedException;
import java.security.InvalidAlgorithmParameterException;
import java.util.Arrays;

import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Contains common methods shared by all pairing modules to reduce code duplication.
 */
@SuppressWarnings({"checkstyle:classdataabstractioncoupling", "checkstyle:ClassFanOutComplexity"})
// 1 / 2) Suppressed the code base if cluttered if we split this class that's already quite small in even smaller classes
abstract class APairingHandler implements IPairingHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger("APairingHandler");

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
        return new PairingWrapper<>(bytes -> {
            final byte[] srcBytes = trim(bytes);
            LOGGER.info("Byte array received = " + Arrays.toString(bytes));

            final byte[] newBuffer = new byte[this.readBuffer.length + srcBytes.length];
            System.arraycopy(this.readBuffer, 0, newBuffer, 0, this.readBuffer.length);
            System.arraycopy(srcBytes, 0, newBuffer, this.readBuffer.length, srcBytes.length);

            try (ByteArrayInputStream bis = new ByteArrayInputStream(newBuffer);
                 ObjectInputStream ois = new ObjectInputStream(bis)) {

                final DataWrapper object = (DataWrapper) ois.readObject();
                LOGGER.info("Read object in data received");
                this.serviceLocator.postOnEventBus(new NewDataReceivedEvent(object.getData(), object.getClazz()));

                this.readBuffer = new byte[0];
            } catch (final ClassNotFoundException | IOException e) {
                if (e.getClass().equals(EOFException.class) || e.getClass().equals(StreamCorruptedException.class)) {
                    this.readBuffer = newBuffer;
                    LOGGER.info("Put in readbuffer");
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
        Validate.notNull(object);
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
        Validate.notNull(object);
        this.send(this.objectToBytes(object));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void send(final Serializable object, final Long key) throws BadPaddingException, IllegalBlockSizeException {
        LOGGER.info("Begin writing object encoded with key: {}", key);
        Validate.notNull(object);
        Validate.isTrue(key >= 0);

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

        try {
            final ByteBuffer buffer = ByteBuffer.allocate(2 * Long.SIZE / Byte.SIZE);
            buffer.putLong(key);
            final Key aesKey = new SecretKeySpec(buffer.array(), "AES");
            final Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            final IvParameterSpec ivParameterSpec = new IvParameterSpec(aesKey.getEncoded());

            // encrypt the text
            cipher.init(Cipher.ENCRYPT_MODE, aesKey, ivParameterSpec);
            final byte[] encrypted = cipher.doFinal(bytes);

            final ByteWrapper byteWrapper = new ByteWrapper(encrypted);
            LOGGER.info("Sending data encrypted");
            this.send(this.objectToBytes(byteWrapper));
        } catch (NoSuchAlgorithmException | NoSuchPaddingException
                | InvalidKeyException | InvalidAlgorithmParameterException e) {
            LOGGER.info("An error occured encrypting the data", e);
        }
    }

    /**
     * @return The service locator
     */
    IServiceLocator getServiceLocator() {
        return this.serviceLocator;
    }

}
