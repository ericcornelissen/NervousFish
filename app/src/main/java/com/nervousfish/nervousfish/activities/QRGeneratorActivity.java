package com.nervousfish.nervousfish.activities;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;

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
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.datamatrix.DataMatrixReader;
import com.google.zxing.qrcode.QRCodeReader;
import com.google.zxing.qrcode.QRCodeWriter;

import java.util.Hashtable;


/**
 * Generates a QR code
 */

public class QRGeneratorActivity extends Activity {
    private static final int QRCODE_IMAGE_HEIGHT = 400;
    private static final int QRCODE_IMAGE_WIDTH = 400;

    /**
     * Creates the new activity, should only be called by Android
     *
     * @param savedInstanceState Don't touch this
     */
    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    /**
     *
     * Encodes the publicKey as a QR code.
     * @param publicKey The public key that's about to be converted to QR code
     * @return The bitmap being the QR code
     */
    public static Bitmap encode(String publicKey) {
        QRCodeWriter qrWriter = new QRCodeWriter();
        Bitmap bitmap = null;
        try {
            BitMatrix matrix = qrWriter.encode(publicKey, BarcodeFormat.QR_CODE, QRCODE_IMAGE_WIDTH, QRCODE_IMAGE_HEIGHT);
            bitmap = Bitmap.createBitmap(QRCODE_IMAGE_WIDTH, QRCODE_IMAGE_HEIGHT, Bitmap.Config.RGB_565);
            for(int x = 0; x< QRCODE_IMAGE_WIDTH; x++) {
                for(int y = 0; y<QRCODE_IMAGE_HEIGHT; y++) {
                    int color = Color.WHITE;
                    if(matrix.get(x, y)) {
                        color = Color.BLACK;
                    }
                    bitmap.setPixel(x, y, color);
                }
            }
        } catch (WriterException e) {
            e.printStackTrace();
        }
        return bitmap;

    }

    /**
     * Decodes the QRcode to a public key string
     * @param QRCode The QR code as a bitmap
     * @return The decoded public key.
     */
    public static String decode(Bitmap QRCode) {

        Reader qrReader = new MultiFormatReader();

        int[] intArray = new int[QRCode.getWidth()*QRCode.getHeight()];
        //copy pixel data from the Bitmap into the 'intArray' array
        QRCode.getPixels(intArray, 0, QRCode.getWidth(), 0, 0, QRCode.getWidth(), QRCode.getHeight());

        LuminanceSource source = new RGBLuminanceSource(QRCode.getWidth(), QRCode.getHeight(), intArray);
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
        String publicKey = "";

        try {
            Hashtable<DecodeHintType, Object> decodeHints = new Hashtable<DecodeHintType, Object>();
            decodeHints.put(DecodeHintType.PURE_BARCODE, Boolean.TRUE);
            Result decoded = qrReader.decode(bitmap, decodeHints);
            publicKey = decoded.getText();
        } catch (NotFoundException e) {
            e.printStackTrace();
        } catch (ChecksumException e) {
            e.printStackTrace();
        } catch (FormatException e) {
            e.printStackTrace();
        }
        return publicKey;
    }

}
