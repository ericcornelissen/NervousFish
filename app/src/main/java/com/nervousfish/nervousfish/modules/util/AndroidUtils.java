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
import com.nervousfish.nervousfish.service_locator.ModuleWrapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InvalidClassException;
import java.util.Hashtable;

/**
 * Contains both the Android-specific util function as inherited cross-platform utils functions.
 */
public final class AndroidUtils extends AbstractUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger("EncryptorAdapter");
    private final int qrcodeImageWidth;
    private final int qrcodeImageHeight;

    /**
     * Prevents construction from outside the class.
     *
     * @param serviceLocator Can be used to get access to other modules
     */
    @SuppressWarnings("PMD.UnusedFormalParameter")
    // This servicelocator will be used later on probably
    private AndroidUtils(final IServiceLocator serviceLocator) {
        super();
        final IConstants constants = serviceLocator.getConstants();
        this.qrcodeImageWidth = constants.getQRCodeWidth();
        this.qrcodeImageHeight = constants.getQRCodeHeight();
        LOGGER.info("Initialized");
    }

    /**
     * Creates a new instance of itself and wraps it in a {@link ModuleWrapper} so that only an
     * {@link IServiceLocator}
     *
     * @param serviceLocator The new service locator
     * @return A wrapper around a newly created instance of this class
     */
    public static ModuleWrapper<AndroidUtils> newInstance(final IServiceLocator serviceLocator) {
        return new ModuleWrapper<>(new AndroidUtils(serviceLocator));
    }

    /**
     * {@inheritDoc}
     */
    public Object encodeQR(final String publicKey) {
        final QRCodeWriter qrWriter = new QRCodeWriter();
        Bitmap bitmap = null;
        try {
            final BitMatrix matrix = qrWriter.encode(publicKey, BarcodeFormat.QR_CODE, this.qrcodeImageWidth, this.qrcodeImageHeight);
            bitmap = Bitmap.createBitmap(this.qrcodeImageWidth, this.qrcodeImageHeight, Bitmap.Config.RGB_565);
            for (int x = 0; x < this.qrcodeImageWidth; x++) {
                for (int y = 0; y < this.qrcodeImageHeight; y++) {
                    final int color = (matrix.get(x, y) ? Color.BLACK : Color.WHITE);
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
    public String decodeQR(final Object QRCode) throws InvalidClassException {
        if (!(QRCode instanceof Bitmap)) {
            throw new InvalidClassException("AndroidUtils was requested to decode a non-recognized object. A Bitmap is expected");
        }
        final Bitmap bitmap = (Bitmap) QRCode;

        final Reader qrReader = new MultiFormatReader();

        final int[] intArray = new int[bitmap.getWidth() * bitmap.getHeight()];
        //copy pixel data from the Bitmap into the 'intArray' array
        bitmap.getPixels(intArray, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());

        final LuminanceSource source = new RGBLuminanceSource(bitmap.getWidth(), bitmap.getHeight(), intArray);
        final BinaryBitmap newBitmap = new BinaryBitmap(new HybridBinarizer(source));
        String publicKey = "";

        try {
            final Hashtable<DecodeHintType, Object> decodeHints = new Hashtable<>();
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
