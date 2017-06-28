package com.nervousfish.nervousfish.data_objects;

import android.util.Log;

import com.google.gson.stream.JsonWriter;
import com.nervousfish.nervousfish.ConstantKeywords;

import net.i2p.crypto.eddsa.EdDSAPrivateKey;
import net.i2p.crypto.eddsa.EdDSAPublicKey;
import net.i2p.crypto.eddsa.Utils;

import org.apache.commons.lang3.Validate;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Map;

import nl.tudelft.ewi.ds.bankver.cryptography.ED25519;

/**
 * Ed25519 variant of {@link IKey}. This is an example implementation of the {@link IKey} interface.
 * <p>
 * For more info about Ed25519 see: https://ed25519.cr.yp.to/
 */
@SuppressWarnings({"checkstyle:CyclomaticComplexity", "PMD.NullAssignment"})
public final class Ed25519Key implements IKey {

    private static final long serialVersionUID = -3865050366412869804L;

    private static final String KEYWORD_NAME = "name";
    private static final String KEYWORD_PUBLIC_KEY = "publicKey";
    private static final String KEYWORD_PRIVATE_KEY = "privateKey";
    private static final String KEYWORD_NULL = "NULL";

    private final String name;
    private final EdDSAPublicKey publicKey;
    private final EdDSAPrivateKey privateKey;

    /**
     * Constructor for a Ed25519-based key.
     *
     * @param name      The name for the key.
     * @param publicKey The public key
     */
    public Ed25519Key(final String name, final EdDSAPublicKey publicKey) {
        Validate.notBlank(name);
        Validate.notNull(publicKey);

        this.name = name;
        this.publicKey = publicKey;
        this.privateKey = null;
    }

    /**
     * Constructor for a Ed25519-based key.
     *
     * @param name       The name for the key.
     * @param privateKey The private key
     */
    public Ed25519Key(final String name, final EdDSAPrivateKey privateKey) {
        Validate.notBlank(name);
        Validate.notNull(privateKey);

        this.name = name;
        this.publicKey = null;
        this.privateKey = privateKey;
    }

    /**
     * Constructor for a Ed25519-based key.
     *
     * @param name       The name for the key.
     * @param publicKey  The public key
     * @param privateKey The private key
     */
    public Ed25519Key(final String name, final EdDSAPublicKey publicKey, final EdDSAPrivateKey privateKey) {
        Validate.notBlank(name);
        Validate.notNull(publicKey);
        Validate.notNull(privateKey);

        this.name = name;
        this.publicKey = publicKey;
        this.privateKey = privateKey;
    }

    /**
     * Constructor for a RSA key given a {@link Map} of its values.
     *
     * @param map A {@link Map} mapping {@link RSAKey} attribute names to values.
     */
    public Ed25519Key(final Map<String, String> map) {
        this.name = map.get(Ed25519Key.KEYWORD_NAME);
        final String tmpPublicKey = map.get(Ed25519Key.KEYWORD_PUBLIC_KEY);
        final String tmpPrivateKey = map.get(Ed25519Key.KEYWORD_PRIVATE_KEY);
        Validate.notBlank(this.name);
        Validate.notBlank(tmpPublicKey);
        Validate.notBlank(tmpPrivateKey);
        this.publicKey = tmpPublicKey.equals(KEYWORD_NULL) ? null : ED25519.getPublicKey(tmpPublicKey);
        this.privateKey = tmpPrivateKey.equals(KEYWORD_NULL) ? null : ED25519.getPrivateKey(tmpPrivateKey);
    }

    /**
     * Get the {@link EdDSAPublicKey} given a {@link Ed25519Key}.
     *
     * @return The {@link EdDSAPublicKey}.
     */
    public EdDSAPublicKey getPublicKey() {
        return this.publicKey;
    }

    /**
     * Get the {@link EdDSAPrivateKey} given a {@link Ed25519Key}.
     *
     * @return The {@link EdDSAPrivateKey}.
     */
    public EdDSAPrivateKey getPrivateKey() {
        return this.privateKey;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getKey() {
        return Utils.bytesToHex(this.privateKey == null ? this.publicKey.getAbyte() : this.privateKey.getAbyte());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getFormattedKey() {
        return Utils.bytesToHex(this.privateKey == null ? this.publicKey.getAbyte() : this.privateKey.getAbyte());
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
        writer.name(KEYWORD_PUBLIC_KEY).value(this.publicKey == null ? KEYWORD_NULL : Utils.bytesToHex(this.publicKey.getAbyte()));
        writer.name(KEYWORD_PRIVATE_KEY).value(this.privateKey == null ? KEYWORD_NULL : Utils.bytesToHex(this.privateKey.getAbyte()));
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings({"checkstyle:OperatorWrap", "checkstyle:LineLength"})
    @Override
    public boolean equals(final Object o) {
        if (o == null || this.getClass() != o.getClass()) {
            return false;x
        }

        final Ed25519Key that = (Ed25519Key) o;
        return this.name.equals(that.name)
                && (this.privateKey != null && that.privateKey != null &&
                Utils.bytesToHex(this.privateKey.getAbyte()).equals(Utils.bytesToHex(that.privateKey.getAbyte())) || this.privateKey == that.privateKey)
                && (this.publicKey != null && that.publicKey != null &&
                Utils.bytesToHex(this.publicKey.getAbyte()).equals(Utils.bytesToHex(that.publicKey.getAbyte())) || this.publicKey == that.publicKey);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        Log.d("ASDF", this.name);
        return this.name.hashCode() + ((this.privateKey == null)
                ? 0
                : Utils.bytesToHex(this.privateKey.getAbyte()).hashCode())
                + (this.publicKey == null
                ? 0
                : Utils.bytesToHex(this.publicKey.getAbyte()).hashCode());
    }


    /**
     * Serialize the created proxy instead of this instance.
     */
    private Object writeReplace() {
        return new Ed25519Key.SerializationProxy(this);
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

        private final String name;
        private final String publicKey;
        private final String privateKey;

        /**
         * Constructs a new SerializationProxy
         *
         * @param key The current instance of the proxy
         */
        SerializationProxy(final Ed25519Key key) {
            this.name = key.name;
            this.publicKey = (key.publicKey == null) ? null : Utils.bytesToHex(key.publicKey.getAbyte());
            this.privateKey = (key.privateKey == null) ? null : Utils.bytesToHex(key.privateKey.getAbyte());
        }

        /**
         * Not to be called by the user - resolves a new object of this proxy
         *
         * @return The object resolved by this proxy
         */
        private Object readResolve() {
            if (this.privateKey == null) {
                return new Ed25519Key(this.name, ED25519.getPublicKey(this.publicKey));
            }
            if (this.publicKey == null) {
                return new Ed25519Key(this.name, ED25519.getPrivateKey(this.privateKey));
            }
            return new Ed25519Key(this.name, ED25519.getPublicKey(this.publicKey), ED25519.getPrivateKey(this.privateKey));
        }

    }

}
