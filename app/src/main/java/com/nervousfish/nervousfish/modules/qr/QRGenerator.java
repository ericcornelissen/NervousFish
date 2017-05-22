package com.nervousfish.nervousfish.modules.qr;

import android.graphics.Bitmap;
import android.graphics.Color;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.DecodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Reader;
import com.google.zxing.Result;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

/**
 * Class that can be used to generate QR codes.
 */
public final class QRGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger("QRGenerator");
    private static final int QRCODE_IMAGE_HEIGHT = 400;
    private static final int QRCODE_IMAGE_WIDTH = 400;

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

        return bitmap;
    }

    /**
     * Decodes the QRcode to a public key string.
     *
     * @param qrCode The QR code as a bitmap
     * @return The decoded public key.
     * @throws IOException If the QR code could not be decoded.
     */
    public static String decode(final Bitmap qrCode) throws IOException {
        final Reader qrReader = new MultiFormatReader();
        final int[] intArray = new int[qrCode.getWidth() * qrCode.getHeight()];

        //copy pixel data from the Bitmap into the 'intArray' array
        qrCode.getPixels(intArray, 0, qrCode.getWidth(), 0, 0, qrCode.getWidth(), qrCode.getHeight());

        final LuminanceSource source = new RGBLuminanceSource(qrCode.getWidth(), qrCode.getHeight(), intArray);
        final BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

        try {
            final Map<DecodeHintType, Object> decodeHints = new EnumMap<>(DecodeHintType.class);
            decodeHints.put(DecodeHintType.PURE_BARCODE, Boolean.TRUE);
            final Result decoded = qrReader.decode(bitmap, decodeHints);
            return decoded.getText();
        } catch (final NotFoundException e) {
            LOGGER.error("Not Found Exception", e);
        } catch (final ChecksumException e) {
            LOGGER.error("Checksum Exception", e);
        } catch (final FormatException e) {
            LOGGER.error("Format Exception", e);
        }

        throw new IOException();
    }

}
