package com.nervousfish.nervousfish.modules.qr;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.nervousfish.nervousfish.ConstantKeywords;
import com.nervousfish.nervousfish.data_objects.IKey;
import com.nervousfish.nervousfish.data_objects.RSAKey;
import com.nervousfish.nervousfish.data_objects.SimpleKey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Class that can be used to generate QR codes.
 */
public final class QRGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger("QRGenerator");
    private static final int QRCODE_IMAGE_HEIGHT = 400;
    private static final int QRCODE_IMAGE_WIDTH = 400;
    private static final int COMPONENT_KEYTYPE = 0;
    private static final int COMPONENT_KEYNAME = 1;
    private static final int COMPONENT_SIMPLE_KEY = 2;
    private static final int COMPONENT_RSA_MODULUS = 2;
    private static final int COMPONENT_RSA_EXPONENT = 3;

    private static final int RESIZE_QR_CODE = 4;


    /**
     * Unused constructor for utility class.
     */
    private QRGenerator() {
        // Prevent instantiations of QRGenerator
    }

    /**
     * Encodes the publicKey as a QR code.
     *
     * @param publicKey The public key that's about to be converted to QR code.
     * @return The bitmap being the QR code.
     */
    public static Bitmap encode(final String publicKey) {
        final QRCodeWriter qrWriter = new QRCodeWriter();
        final Bitmap bitmap = Bitmap.createBitmap(QRCODE_IMAGE_WIDTH, QRCODE_IMAGE_HEIGHT, Bitmap.Config.RGB_565);

        try {
            final BitMatrix matrix = qrWriter.encode(publicKey, BarcodeFormat.QR_CODE, QRCODE_IMAGE_WIDTH, QRCODE_IMAGE_HEIGHT);
            for (int x = 0; x < QRCODE_IMAGE_WIDTH; x++) {
                for (int y = 0; y < QRCODE_IMAGE_HEIGHT; y++) {
                    final int color = matrix.get(x, y) ? Color.BLACK : Color.WHITE;
                    bitmap.setPixel(x, y, color);
                }
            }
        } catch (final WriterException e) {
            LOGGER.error("Failed to encode bitmap", e);
        }

        final Matrix matrix = new Matrix();

        // resize the bit map
        matrix.postScale(RESIZE_QR_CODE, RESIZE_QR_CODE);
        final Bitmap largerCode = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

        return largerCode;
    }


    /**
     * Deconstructs a decrypted qrmessage to a key.
     * @param qrMessage The decrypted QRCode in a string.
     * @return The key it corresponds to.
     */
    public static IKey deconstructToKey(final String qrMessage) throws NullPointerException {
        final String[] messageComponents = qrMessage.split(" ");
        switch (messageComponents[COMPONENT_KEYTYPE]) {
            case ConstantKeywords.RSA_KEY :
                return new RSAKey(messageComponents[COMPONENT_KEYNAME], messageComponents[COMPONENT_RSA_MODULUS],
                        messageComponents[COMPONENT_RSA_EXPONENT]);
            case "simple" :
                return new SimpleKey(messageComponents[COMPONENT_KEYNAME], messageComponents[COMPONENT_SIMPLE_KEY]);
            default :
                LOGGER.error("Key Type Not Found");
                return null;
        }

    }

}
