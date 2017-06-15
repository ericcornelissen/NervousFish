package com.nervousfish.nervousfish.data_objects;

import com.nervousfish.nervousfish.ConstantKeywords;

import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;

/**
 * Carries the verification method to use.
 */
public class VerificationMethod implements Serializable {
    private static final long serialVersionUID = 6711854169751606007L;
    private final VerificationMethodEnum verificationMethodEnum;

    /**
     * Creates and initializes a VerificationMethod.
     *
     * @param verificationMethodEnum The verification method this object should represent.
     */
    public VerificationMethod(final VerificationMethodEnum verificationMethodEnum) {
        if (verificationMethodEnum == null) {
            throw new IllegalArgumentException("The verification method may not be null");
        }
        this.verificationMethodEnum = verificationMethodEnum;
    }

    /**
     * Returns the Verification method represent by this object.
     * @return The {@link VerificationMethodEnum}
     */
    public VerificationMethodEnum getVerificationMethod() {
        return verificationMethodEnum;
    }

    /**
     * Serialize the created proxy instead of this instance.
     */
    private Object writeReplace() {
        return new SerializationProxy(this);
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
        private static final long serialVersionUID = 6711854169751606007L;
        private final VerificationMethodEnum verificationMethod;

        /**
         * Constructs a new SerializationProxy
         * @param verificationMethod The current instance of the proxy
         */
        SerializationProxy(final VerificationMethod verificationMethod) {
            this.verificationMethod = verificationMethod.getVerificationMethod();
        }

        /**
         * Not to be called by the user - resolves a new object of this proxy
         * @return The object resolved by this proxy
         */
        private Object readResolve() {
            return new VerificationMethod(verificationMethod);
        }
    }
}
