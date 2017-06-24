package com.nervousfish.nervousfish.test;

import android.graphics.Bitmap;
import android.support.test.runner.AndroidJUnit4;

import com.nervousfish.nervousfish.data_objects.AKeyPair;
import com.nervousfish.nervousfish.data_objects.IKey;
import com.nervousfish.nervousfish.modules.cryptography.IKeyGenerator;
import com.nervousfish.nervousfish.modules.qr.QRGenerator;
import com.nervousfish.nervousfish.service_locator.NervousFish;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

@RunWith(AndroidJUnit4.class)
public class QRGeneratorTest {

    private static final int QRCODE_IMAGE_HEIGHT = 400;
    private static final int QRCODE_IMAGE_WIDTH = 400;
    private IKey publicKey;
    private String publicKeyString;

    @Before
    public void setUp() throws Exception {
        final IKeyGenerator keyGenerator = NervousFish.getServiceLocator().getKeyGenerator();
        final AKeyPair pair = keyGenerator.generateRSAKeyPair("test");
        publicKey = (IKey) pair.getPublicKey();
        final String space = " ";
        publicKeyString = publicKey.getType() + space + publicKey.getName()
                + space + publicKey.getKey();
    }
    @Test
    public void testEncodeNotNull() throws Exception {
        final Bitmap qrCode = QRGenerator.encode(publicKeyString);
        assertNotNull(qrCode);
    }

    @Test
    public void testDeconstructAsExpected() throws Exception {
        IKey keyTest = QRGenerator.deconstructToKey(publicKeyString);
        assertEquals(publicKey, keyTest);
    }
    @Test
    public void testWidthIsAsExpected() throws Exception{
        final Bitmap QRcode = QRGenerator.encode(publicKeyString);
        assertEquals(QRCODE_IMAGE_WIDTH, QRcode.getWidth());
    }

    @Test
    public void testHeightIsAsExpected() throws Exception{
        final Bitmap QRcode = QRGenerator.encode(publicKeyString);
        assertEquals(QRCODE_IMAGE_HEIGHT, QRcode.getHeight());
    }

}