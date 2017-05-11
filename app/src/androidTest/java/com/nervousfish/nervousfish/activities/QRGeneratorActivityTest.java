package com.nervousfish.nervousfish.activities;

import android.graphics.Bitmap;
import android.support.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import android.support.test.runner.AndroidJUnitRunner;
import android.test.ActivityInstrumentationTestCase2;
import com.nervousfish.nervousfish.service_locator.EntryActivity;

import org.junit.Before;
import org.junit.Test;

import cucumber.api.CucumberOptions;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;


@SuppressWarnings("PMD")
@CucumberOptions(features = "features")
public class QRGeneratorActivityTest extends ActivityInstrumentationTestCase2<QRGeneratorActivity> {
    private Bitmap QRcode = null;
    private String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCqGKukO1De7zhZj6+H0qtjTkVxwT" +
            "CpvKe4eCZ0FPqri0cb2JZfXJ/DgYSF6vUpwmJG8wVQZKjeGcjDOL5UlsuusFncCzWBQ7RKNUSesmQRMSGkV" +
            "b1/3j+skZ6UtW+5u09lHNsj6tQ51s1SPrCBkedbNf0Tp0GbMJDyR4e9T04ZZwIDAQAB";
    private String result = "";

    public QRGeneratorActivityTest(Class<QRGeneratorActivity> activityClass) {
        super(activityClass);
    }

    @Given("^I encode a string to a QR code")
    public void iEncodeAString() {
        QRcode = QRGeneratorActivity.encode(publicKey);

    }

    @When("^I decode the QR code")
    public void iDecodeTheQRCode() {
        result = QRGeneratorActivity.decode(QRcode);
    }

    @Then("^I should receive the same string")
    public void iReceiveTheSameString() {
        assertEquals(result, publicKey);
    }




    /*
    public QRGeneratorActivityTest(String pkg, Class<QRGeneratorActivity> activityClass) {
        super(QRGeneratorActivity.class);
    }

    @Test
    public void encodedDataIsAsExpected() {
        String publicKeyTest = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCqGKukO1De7zhZj6+H0qtjTkVx" +
                "wTCpvKe4eCZ0FPqri0cb2JZfXJ/DgYSF6vUpwmJG8wVQZKjeGcjDOL5UlsuusFncCzWBQ7RKNUSesmQ" +
                "RMSGkVb1/3j+skZ6UtW+5u09lHNsj6tQ51s1SPrCBkedbNf0Tp0GbMJDyR4e9T04ZZwIDAQAB";
        Bitmap qrcode = QRGeneratorActivity.encode(publicKeyTest);
        String result = QRGeneratorActivity.decode(qrcode);
        assertEquals(publicKeyTest, 5);
    }*/

}