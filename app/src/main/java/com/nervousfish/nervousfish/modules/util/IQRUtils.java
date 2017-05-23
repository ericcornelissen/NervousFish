package com.nervousfish.nervousfish.modules.util;

import java.io.InvalidClassException;

/**
 * Defines the utility functions used for QR related functionality
 */
interface IQRUtils {

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
     * @param qrCode The QR code. The implementation required depends on the platform-specific implementation
     * @return The decoded public key.
     */
    String decodeQR(Object qrCode) throws InvalidClassException;
}
