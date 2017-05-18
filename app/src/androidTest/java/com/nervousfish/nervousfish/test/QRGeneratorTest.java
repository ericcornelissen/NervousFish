package com.nervousfish.nervousfish.test;

import android.graphics.Bitmap;
import android.support.test.runner.AndroidJUnit4;


import com.nervousfish.nervousfish.modules.constants.IConstants;
import com.nervousfish.nervousfish.modules.util.AndroidUtils;
import com.nervousfish.nervousfish.modules.util.IUtils;
import com.nervousfish.nervousfish.service_locator.IServiceLocator;
import com.nervousfish.nervousfish.service_locator.ServiceLocator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


@RunWith(AndroidJUnit4.class)
@SuppressWarnings("PMD")
public class QRGeneratorTest {

    private final String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCqGKukO1De7zhZj6+H0qtjTkVxwTCpvKe4eCZ0" +
            "FPqri0cb2JZfXJ/DgYSF6vUpwmJG8wVQZKjeGcjDOL5UlsuusFncCzWBQ7RKNUSesmQRMSGkVb1/" +
            "3j+skZ6UtW+5u09lHNsj6tQ51s1SPrCBkedbNf0Tp0GbMJDyR4e9T04ZZwIDAQAB";

    private IServiceLocator serviceLocator;

    @Before
    public void init() {
        serviceLocator = (IServiceLocator) BaseTest.accessConstructor(ServiceLocator.class, "");
    }

    // Tests if the decodes returns the same result after encoding a string.
    @Test
    public void testEncodeAsExpected() throws Exception{

        final Bitmap QRcode = (Bitmap) serviceLocator.getUtils().encodeQR(publicKey);


        final String result = serviceLocator.getUtils().decodeQR(QRcode);
        assertEquals(publicKey, result);
    }

    @Test
    public void testWidthIsAsExpected() throws Exception{
        final Bitmap QRcode = (Bitmap) serviceLocator.getUtils().encodeQR(publicKey);
        assertEquals(serviceLocator.getConstants().getQRCodeWidth(), QRcode.getWidth());
    }

    @Test
    public void testHeightIsAsExpected() throws Exception{
        final Bitmap QRcode = (Bitmap) serviceLocator.getUtils().encodeQR(publicKey);
        assertEquals(serviceLocator.getConstants().getQRCodeHeight(), QRcode.getHeight());
    }



}