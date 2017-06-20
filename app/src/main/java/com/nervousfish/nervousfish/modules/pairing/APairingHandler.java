package com.nervousfish.nervousfish.modules.pairing;

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
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.SecretKey;

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
    public final PairingWrapper<IDataReceiver> getDataReceiver() {
        return new PairingWrapper<>(bytes -> {
            try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
                 ObjectInputStream ois = new ObjectInputStream(bis)) {
                final DataWrapper object = (DataWrapper) ois.readObject();
                if (object.getClazz().equals(ByteWrapper.class)) {
                    this.serviceLocator.postOnEventBus(new NewEncryptedBytesReceivedEvent(((ByteWrapper) object.getData()).getBytes()));
                } else {
                    this.serviceLocator.postOnEventBus(new NewDataReceivedEvent(object.getData(), object.getClazz()));
                }
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

    /**
     * Deserialize the instance using readObject to ensure invariants and security.
     *
     * @param stream The serialized object to be deserialized
     */
    private void readObject(final ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.ensureClassInvariant();
    }

    /**
     * Used to improve performance / efficiency
     *
     * @param stream The stream to which this object should be serialized to
     */
    private void writeObject(final ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
    }

    /**
     * Ensure that the instance meets its class invariant
     */
    private void ensureClassInvariant() {
        Validate.notNull(this.serviceLocator);
    }
}
