package com.nervousfish.nervousfish.data_objects;

import com.google.gson.stream.JsonWriter;
import com.nervousfish.nervousfish.ConstantKeywords;

import net.i2p.crypto.eddsa.EdDSAPrivateKey;

import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;

/**
 * Ed25519 variant of {@link IKey}. This is an example implementation of the {@link IKey} interface.
 *
 * For more info about Ed25519 see: https://ed25519.cr.yp.to/
 */
public final class Ed25519PrivateKeyWrapper implements IKey<EdDSAPrivateKey> {
    private static final Logger LOGGER = LoggerFactory.getLogger("Ed25519PrivateKeyWrapper");
    private static final long serialVersionUID = -3865050366412869804L;

    private static final String KEYWORD_NAME = "name";
    private static final String KEYWORD_KEY = "key";

    private final String name;
    private final EdDSAPrivateKey key;

    /**
     * Constructor for a Ed25519-based key.
     *
     * @param key The underlying key of this wrapper
     */
    public Ed25519PrivateKeyWrapper(final String name, final EdDSAPrivateKey key) {
        Validate.notBlank(name);
        Validate.notNull(key);
        this.name = name;
        this.key = key;
    }

    /**
     * Constructor for a RSA key given a {@link Map} of its values.
     *
     * @param map A {@link Map} mapping {@link RSAKeyWrapper} attribute names to values.
     */
    public Ed25519PrivateKeyWrapper(final Map<String, String> map) {
        this.name = map.get(Ed25519PrivateKeyWrapper.KEYWORD_NAME);
        final String keyEncoding = map.get(Ed25519PrivateKeyWrapper.KEYWORD_KEY);
        try (ByteArrayInputStream bis = new ByteArrayInputStream(keyEncoding.getBytes("ISO-8859-1"));
        ObjectInputStream ois = new ObjectInputStream(bis)) {
            this.key = (EdDSAPrivateKey) ois.readObject();
        } catch (final IOException | ClassNotFoundException e) {
            LOGGER.error("Reading EdDSAPrivateKey went wrong", e);
            throw new IllegalArgumentException("Could not find EdDSAPrivateKey", e);
        }

        if (this.name == null || this.key == null) {
            throw new IllegalArgumentException("Couldn't find the name, modulus or exponent in the map");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EdDSAPrivateKey getKey() {
        return this.key;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getFormattedKey() {
        return this.key.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return this.name;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getType() {
        return ConstantKeywords.ED25519_KEY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void toJson(final JsonWriter writer) throws IOException {
        writer.name(KEYWORD_NAME).value(this.name);
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(bos)) {
            oos.writeObject(this.key);
            oos.flush();
            writer.name(KEYWORD_KEY).value(new String(bos.toByteArray(), "ISO-8859-1"));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object o) {
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }

        final Ed25519PrivateKeyWrapper that = (Ed25519PrivateKeyWrapper) o;
        return this.name.equals(that.name)
                && this.key.equals(that.key);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return this.key.hashCode() + this.name.hashCode();
    }


    /**
     * Serialize the created proxy instead of this instance.
     */
    private Object writeReplace() {
        return new Ed25519PrivateKeyWrapper.SerializationProxy(this);
    }

    /**
     * Ensure that no instance of this class is created because it was present in the stream. A
     * correct stream should only contain instances of the proxy.
     */
    private void readObject(final ObjectInputStream stream) throws InvalidObjectException {
        throw new InvalidObjectException(ConstantKeywords.PROXY_REQUIRED);
    }

    /**
     * Represents the logical state of this class and copies the data from that class without
     * any consistency checking or defensive copying.
     */
    private static final class SerializationProxy implements Serializable {

        private static final long serialVersionUID = -3865050366412869804L;

        private final EdDSAPrivateKey key;
        private final String name;

        /**
         * Constructs a new SerializationProxy
         * @param key The current instance of the proxy
         */
        SerializationProxy(final Ed25519PrivateKeyWrapper key) {
            this.key = key.key;
            this.name = key.name;
        }

        /**
         * Not to be called by the user - resolves a new object of this proxy
         * @return The object resolved by this proxy
         */
        private Object readResolve() {
            return new Ed25519PrivateKeyWrapper(this.name, this.key);
        }

    }

}
