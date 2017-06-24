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
import com.nervousfish.nervousfish.data_objects.AKeyPair;
import com.nervousfish.nervousfish.data_objects.Contact;
import com.nervousfish.nervousfish.data_objects.Ed25519PublicKeyWrapper;
import com.nervousfish.nervousfish.data_objects.IKey;
import com.nervousfish.nervousfish.data_objects.Profile;
import com.nervousfish.nervousfish.data_objects.RSAKeyWrapper;
import com.nervousfish.nervousfish.modules.database.IDatabase;
import com.nervousfish.nervousfish.modules.qr.QRGenerator;
import com.nervousfish.nervousfish.service_locator.IServiceLocator;
import com.nervousfish.nervousfish.service_locator.NervousFish;

import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.regex.Pattern;

import nl.tudelft.ewi.ds.bankver.IBAN;

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
    private static final Pattern COMPILE_SEMI_COLON = Pattern.compile(";");

    private IServiceLocator serviceLocator;
    private IDatabase database;
    private Profile profile;

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_qrexchange);
        this.serviceLocator = NervousFish.getServiceLocator();
        this.database = this.serviceLocator.getDatabase();

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
        Validate.notNull(view);
        this.finish();
    }

    /**
     * Starts the scan qr feature.
     *
     * @param view - the imagebutton
     */
    public void onScanButtonClick(final View view) {
        LOGGER.info("Started scanning QR code");
        Validate.notNull(view);
        final IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.initiateScan();
    }

    /**
     * Shows the QR Code in the activity.
     */
    @SuppressLint("InflateParams")
    private void drawQRCode() {
        final AKeyPair<?, ?> keyPair = this.profile.getKeyPairs().get(0);
        final IKey<?> publicKey = keyPair.getPublicKey();
        final Bitmap qrCode = QRGenerator.encode(String.format("%s ; %s ; %s, %s, %s",
                this.profile.getName(), this.profile.getIbanAsString(), publicKey.getType(),
                publicKey.getName(), publicKey.getKey()));

        final ImageView imageView = (ImageView) this.findViewById(R.id.QR_code_image);
        imageView.setImageBitmap(qrCode);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        LOGGER.info("Activity resulted");
        final IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (scanResult == null) {
            LOGGER.error("No scan result in QR Scanner");
        } else if (resultCode == RESULT_OK) {
            final String result = scanResult.getContents();
            LOGGER.info("Adding new contact to database");
            final String[] split = COMPILE_SEMI_COLON.split(result);
            //Name is the first part
            final String name = split[0];
            //IBAN is the second part
            final String iban = split[1];
            //Key is the third part
            final Pair<IKey<?>, IKey.Types> key = QRGenerator.deconstructToKey(split[2]);

            final Contact.ContactBuilder builder = new Contact.ContactBuilder(name);
            switch (key.getRight()) {
                case RSA:
                    builder.addRSAKey((RSAKeyWrapper) key.getLeft());
                    break;
                case Ed25519:
                    builder.addEd25519Key((Ed25519PublicKeyWrapper) key.getLeft());
                    break;
            }
            if (!iban.isEmpty()) {
                builder.setIban(new IBAN(iban));
            }
            ContactReceivedHelper.newContactReceived(this.database, this, builder.build());
        }
    }

}
