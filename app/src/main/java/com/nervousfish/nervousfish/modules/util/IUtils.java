package com.nervousfish.nervousfish.modules.util;

import com.nervousfish.nervousfish.modules.IModule;
import java.io.InvalidClassException;

/**
 * Defines the interface that contains all util functions
 */
public interface IUtils extends IModule {

    /**
     * Encodes the publicKey as a QR code.
     *
     * @param publicKey The public key that's about to be converted to QR code
     * @return The object being the QR code. The exact implementation depends on the platform-specific implementation
     */
    Object encodeQR(String publicKey);

    /**
     * Decodes the QRcode to a public key string
     *
     * @param QRCode The QR code. The implementation required depends on the platform-specific implementation
     * @return The decoded public key.
     */
    String decodeQR(final Object QRCode) throws InvalidClassException;
}
