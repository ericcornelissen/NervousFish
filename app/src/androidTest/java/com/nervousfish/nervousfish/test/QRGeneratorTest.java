package com.nervousfish.nervousfish.test;

import android.graphics.Bitmap;
import android.support.test.runner.AndroidJUnit4;


import com.nervousfish.nervousfish.QRGenerator;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;


@RunWith(AndroidJUnit4.class)
@SuppressWarnings("PMD")
public class QRGeneratorTest {

    private final String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCqGKukO1De7zhZj6+H0qtjTkVxwTCpvKe4eCZ0" +
            "FPqri0cb2JZfXJ/DgYSF6vUpwmJG8wVQZKjeGcjDOL5UlsuusFncCzWBQ7RKNUSesmQRMSGkVb1/" +
            "3j+skZ6UtW+5u09lHNsj6tQ51s1SPrCBkedbNf0Tp0GbMJDyR4e9T04ZZwIDAQAB";

    private static final int QRCODE_IMAGE_HEIGHT = 400;
    private static final int QRCODE_IMAGE_WIDTH = 400;

    // Tests if the decodes returns the same result after encoding a string.
    @Test
    public void testEncodeAsExpected() throws Exception{

        final Bitmap QRcode = QRGenerator.encode(publicKey);


        final String result = QRGenerator.decode(QRcode);
        assertEquals(publicKey, result);
    }

    @Test
    public void testWidthIsAsExpected() throws Exception{
        final Bitmap QRcode = QRGenerator.encode(publicKey);
        assertEquals(QRCODE_IMAGE_WIDTH, QRcode.getWidth());
    }

    @Test
    public void testHeightIsAsExpected() throws Exception{
        final Bitmap QRcode = QRGenerator.encode(publicKey);
        assertEquals(QRCODE_IMAGE_HEIGHT, QRcode.getHeight());
    }



}