package com.nervousfish.nervousfish.modules.util;

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
import com.nervousfish.nervousfish.modules.constants.IConstants;
import com.nervousfish.nervousfish.service_locator.IServiceLocator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InvalidClassException;
import java.util.EnumMap;

/**
 * A class providing all QR related functionality using Google's ZXing library
 */
final class ZXingQRUtils implements IQRUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger("ZXingQRUtils");
    private final int qrcodeImageWidth;
    private final int qrcodeImageHeight;

    /**
     * Constructs a new instance of ZXingQRUtils
     *
     * @param serviceLocator The service locator used throughout the program
     */
    ZXingQRUtils(final IServiceLocator serviceLocator) {
        final IConstants constants = serviceLocator.getConstants();
        this.qrcodeImageWidth = constants.getQRCodeWidth();
        this.qrcodeImageHeight = constants.getQRCodeHeight();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object encodeQR(final String publicKey) {
        final QRCodeWriter qrWriter = new QRCodeWriter();
        Bitmap bitmap = null;
        try {
            final BitMatrix matrix = qrWriter.encode(publicKey, BarcodeFormat.QR_CODE, this.qrcodeImageWidth, this.qrcodeImageHeight);
            bitmap = Bitmap.createBitmap(this.qrcodeImageWidth, this.qrcodeImageHeight, Bitmap.Config.RGB_565);
            for (int x = 0; x < this.qrcodeImageWidth; x++) {
                for (int y = 0; y < this.qrcodeImageHeight; y++) {
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
     * {@inheritDoc}
     */
    @Override
    public String decodeQR(final Object qrCode) throws InvalidClassException {
        if (!(qrCode instanceof Bitmap)) {
            throw new InvalidClassException("AndroidUtils was requested to decode a non-recognized object. A Bitmap is expected");
        }
        final Bitmap bitmap = (Bitmap) qrCode;

        final Reader qrReader = new MultiFormatReader();

        final int[] intArray = new int[bitmap.getWidth() * bitmap.getHeight()];
        //copy pixel data from the Bitmap into the 'intArray' array
        bitmap.getPixels(intArray, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());

        final LuminanceSource source = new RGBLuminanceSource(bitmap.getWidth(), bitmap.getHeight(), intArray);
        final BinaryBitmap newBitmap = new BinaryBitmap(new HybridBinarizer(source));
        String publicKey = "";

        try {
            final EnumMap<DecodeHintType, Object> decodeHints = new EnumMap<>(DecodeHintType.class);
            decodeHints.put(DecodeHintType.PURE_BARCODE, Boolean.TRUE);
            final Result decoded = qrReader.decode(newBitmap, decodeHints);
            publicKey = decoded.getText();
        } catch (final NotFoundException e) {
            LOGGER.error("Not Found Exception", e);
        } catch (final ChecksumException e) {
            LOGGER.error("Checksum Exception", e);
        } catch (final FormatException e) {
            LOGGER.error("Format Exception", e);
        }
        return publicKey;
    }
}
