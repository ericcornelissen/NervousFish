package com.nervousfish.nervousfish.test;

import android.graphics.Bitmap;
import android.support.test.runner.AndroidJUnit4;

import com.nervousfish.nervousfish.modules.qr.QRGenerator;

import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class QRGeneratorTest {

    private static final int QRCODE_IMAGE_HEIGHT = 400;
    private static final int QRCODE_IMAGE_WIDTH = 400;
    private final String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCqGKukO1De7zhZj6+H0qtjTkVxwTCpvKe4eCZ0" +
            "FPqri0cb2JZfXJ/DgYSF6vUpwmJG8wVQZKjeGcjDOL5UlsuusFncCzWBQ7RKNUSesmQRMSGkVb1/" +
            "3j+skZ6UtW+5u09lHNsj6tQ51s1SPrCBkedbNf0Tp0GbMJDyR4e9T04ZZwIDAQAB";

    @Test
    public void testEncodeAsExpected() throws Exception {
        final Bitmap QRcode = QRGenerator.encode(publicKey);
        final String result = QRGenerator.decode(QRcode);
        assertEquals(publicKey, result);
    }

    @Test
    public void testWidthIsAsExpected() throws Exception {
        final Bitmap QRcode = QRGenerator.encode(publicKey);
        assertEquals(QRCODE_IMAGE_WIDTH, QRcode.getWidth());
    }

    @Test
    public void testHeightIsAsExpected() throws Exception {
        final Bitmap QRcode = QRGenerator.encode(publicKey);
        assertEquals(QRCODE_IMAGE_HEIGHT, QRcode.getHeight());
    }

}