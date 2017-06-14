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
import com.nervousfish.nervousfish.data_objects.Ed25519Key;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;


/**
 * Class that can be used to generate QR codes.
 */
public final class QRGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger("QRGenerator");
    private static final int QRCODE_IMAGE_HEIGHT = 400;
    private static final int QRCODE_IMAGE_WIDTH = 400;
    private static final int COMPONENT_KEYTYPE = 0;
    private static final int COMPONENT_KEYNAME = 1;
    private static final int COMPONENT_KEY = 2;

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
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }


    /**
     * Deconstructs a decrypted qrmessage to a key.
     * @param qrMessage The decrypted QRCode in a string.
     * @return The key it corresponds to.
     */
    public static IKey deconstructToKey(final String qrMessage) throws NullPointerException, IllegalArgumentException {
        final String spaceBar = " ";
        final String[] messageComponents = qrMessage.split(", ");
        System.out.println(Arrays.toString(messageComponents));
        switch (messageComponents[COMPONENT_KEYTYPE]) {
            case ConstantKeywords.RSA_KEY:
                return new RSAKey(messageComponents[COMPONENT_KEYNAME], messageComponents[COMPONENT_KEY].split(spaceBar)[0],
                        messageComponents[COMPONENT_KEY].split(spaceBar)[1]);
            case ConstantKeywords.ED25519_KEY:
                return new Ed25519Key(messageComponents[COMPONENT_KEYNAME], messageComponents[COMPONENT_KEY]);
            default:
                throw new IllegalArgumentException("Key Type Not Found in deconstructKey");
        }

    }

}
