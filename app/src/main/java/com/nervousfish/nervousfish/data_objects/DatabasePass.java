package com.nervousfish.nervousfish.data_objects;

import com.nervousfish.nervousfish.ConstantKeywords;
import com.nervousfish.nervousfish.modules.cryptography.EncryptedSaver;

import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;


/**
 * A databasePass object to save the enccryption elements
 */
public class DatabasePass implements Serializable {
    private static final long serialVersionUID = -4715364587956219157L;
    private final String encryptedPassword;


    /**
     * Constructor for the {@link DatabasePass} POJO
     * @param password - The password of the database
     */
    public DatabasePass(final String password) {
        this.encryptedPassword = EncryptedSaver.hashWithoutSalt(password);
    }


    /**
     * Getter for the encrypted password
     * @return The encrypted password.
     */
    public String getEncryptedPassword() {
        return encryptedPassword;
    }

    /**
     * Serialize the created proxy instead of this instance.
     */
    private Object writeReplace() {
        return new DatabasePass.SerializationProxy(this);
    }

    /**
     * Ensure that no instance of this class is created because it was present in the stream. A correct
     * stream should only contain instances of the proxy.
     */
    private void readObject(final ObjectInputStream stream) throws InvalidObjectException {
        throw new InvalidObjectException(ConstantKeywords.PROXY_REQUIRED);
    }

    /**
     * Represents the logical state of this class and copies the data from that class without
     * any consistency checking or defensive copying.
     */
    private static final class SerializationProxy implements Serializable {
        private static final long serialVersionUID = -4715364587956219157L;
        private final String encryptedPassword;


        /**
         * Constructs a new SerializationProxy
         * @param databasePass - the databasePass
         */
        SerializationProxy(final DatabasePass databasePass) {
            this.encryptedPassword = databasePass.encryptedPassword;
        }

        /**
         * Not to be called by the user - resolves a new object of this proxy
         * @return The object resolved by this proxy
         */
        private Object readResolve() {
            return new DatabasePass(this.encryptedPassword);
        }
    }
}
