package com.nervousfish.nervousfish.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.nervousfish.nervousfish.R;
import com.nervousfish.nervousfish.data_objects.Contact;
import com.nervousfish.nervousfish.data_objects.IKey;
import com.nervousfish.nervousfish.data_objects.KeyPair;
import com.nervousfish.nervousfish.data_objects.Profile;
import com.nervousfish.nervousfish.modules.database.IDatabase;
import com.nervousfish.nervousfish.modules.qr.QRGenerator;
import com.nervousfish.nervousfish.service_locator.IServiceLocator;
import com.nervousfish.nervousfish.service_locator.NervousFish;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * An {@link Activity} that is used for pairing using QR codes
 */
@SuppressWarnings({"checkstyle:ClassFanOutComplexity", "PMD.AccessorClassGeneration", "PMD.ExcessiveImports"})
//  1)  This warning is because the class relies on too many external classes, which can't really be avoided
//  2)  This warning doesn't make sense since I can't instantiate the object in the constructor as I
//      need the qr message to create the editnameclicklistener in the addnewcontact method
//  3)  Uses many utility imports.
public final class QRExchangeActivity extends AppCompatActivity {

    private static final Logger LOGGER = LoggerFactory.getLogger("QRExchangeActivity");
    private static final String SEMI_COLON = " ; ";

    private IServiceLocator serviceLocator;
    private Profile profile;

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_qrexchange);
        this.serviceLocator = NervousFish.getServiceLocator();

        try {
            this.profile = this.serviceLocator.getDatabase().getProfile();
        } catch (final IOException e) {
            LOGGER.error("Loading the public key went wrong", e);
        }

        this.drawQRCode();
    }

    /**
     * Returns to the previous activity.
     *
     * @param view - the imagebutton
     */
    public void onBackButtonClick(final View view) {
        LOGGER.info("Return to previous screen");
        this.finish();
    }

    /**
     * Starts the scan qr feature.
     *
     * @param view - the imagebutton
     */
    public void onScanButtonClick(final View view) {
        LOGGER.info("Started scanning QR code");
        final IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.initiateScan();
    }

    /**
     * Shows the QR Code in the activity.
     */
    @SuppressLint("InflateParams")
    private void drawQRCode() {
        final KeyPair keyPair = this.profile.getKeyPairs().get(0);
        final IKey publicKey = keyPair.getPublicKey();
        final Bitmap qrCode = QRGenerator.encode(String.format("%s ; %s, %s, %s",
                this.profile.getName(), publicKey.getType(), publicKey.getName(), publicKey.getKey()));

        final ImageView imageView = (ImageView) this.findViewById(R.id.QR_code_image);
        imageView.setImageBitmap(qrCode);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent intent) {
        LOGGER.info("Activity resulted");
        final IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanResult == null) {
            LOGGER.error("No scan result in QR Scanner");
        } else if (resultCode == RESULT_OK) {
            final String result = scanResult.getContents();
            LOGGER.info("Adding new contact to database");
            //Name is the first part
            final String name = result.split(SEMI_COLON)[0];
            //Key is the second part
            final IKey key = QRGenerator.deconstructToKey(result.split(SEMI_COLON)[1]);

            final IDatabase database = this.serviceLocator.getDatabase();
            final Contact contact = new Contact(name, key);
            ContactReceivedHelper.newContactReceived(database, this, contact);
        }
    }

}
