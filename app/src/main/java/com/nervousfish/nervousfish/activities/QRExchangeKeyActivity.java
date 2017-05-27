package com.nervousfish.nervousfish.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.Image;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.nervousfish.nervousfish.ConstantKeywords;
import com.nervousfish.nervousfish.R;
import com.nervousfish.nervousfish.data_objects.Contact;
import com.nervousfish.nervousfish.data_objects.IKey;
import com.nervousfish.nervousfish.data_objects.KeyPair;
import com.nervousfish.nervousfish.modules.cryptography.IKeyGenerator;
import com.nervousfish.nervousfish.modules.database.IDatabase;
import com.nervousfish.nervousfish.modules.qr.QRGenerator;
import com.nervousfish.nervousfish.service_locator.IServiceLocator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;


/**
 * The activity that exchanges public keys through QR codes.
 */
public class QRExchangeKeyActivity extends AppCompatActivity {

    private static final Logger LOGGER = LoggerFactory.getLogger("QRExchangeKeyActivity");

    private IServiceLocator serviceLocator;
    private IKey publicKey;

    /**
     * Creates the new activity, should only be called by Android
     *
     * @param savedInstanceState Don't touch this
     */
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_qrexchange);

        final Intent intent = getIntent();
        this.serviceLocator = (IServiceLocator) intent.getSerializableExtra(ConstantKeywords.SERVICE_LOCATOR);


        //TODO: Get the user's generated public key from the database
        final IKeyGenerator keyGenerator = serviceLocator.getKeyGenerator();
        final KeyPair pair = keyGenerator.generateRSAKeyPair("me");
        publicKey = pair.getPublicKey();


        final Button scanButton = (Button) findViewById(R.id.scanbutton);
        scanButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                LOGGER.info("Started scanning QR code");
                IntentIntegrator integrator = new IntentIntegrator(QRExchangeKeyActivity.this);
                integrator.initiateScan();

            }
        });

        final Button generateButton = (Button) findViewById(R.id.generateQRbutton);
        generateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LOGGER.info("Started generating QR code");
                Bitmap qrCode = QRGenerator.encode(publicKey.getType() + " " + publicKey.getName() +
                        " " + publicKey.getKey());
                showQRCode(qrCode);
            }
        });

    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if(scanResult != null) {
            String result = scanResult.getContents();
            try {
                addNewContact(result);
            } catch (IOException e) {
                LOGGER.error("IOException when adding new contact");
            }
        } else {
            LOGGER.error("No scan result in QR Scanner");
        }


    }


    /**
     * Adds new contact with the scanned key and opens change
     * @param QRMessage
     */
    private void addNewContact(String QRMessage) throws IOException {
        //TODO: Add recognizer for contact name to avoid saving the same key twice (add your personal name to QR code)
        try{
            IKey key = QRGenerator.deconstructToKey(QRMessage);
            Contact contact = new Contact("<new contact>", key);
            final IDatabase database = this.serviceLocator.getDatabase();
            database.addContact(contact);

            Intent intent = new Intent(this, ContactActivity.class);
            intent.putExtra(ConstantKeywords.SERVICE_LOCATOR, this.serviceLocator);
            intent.putExtra(ConstantKeywords.CONTACT, contact);
            this.startActivity(intent);
        } catch (NullPointerException e) {
            LOGGER.error("Wrong input for scanner");
        }

        
    }

    /**
     * Shows the QR Code in an alert dialog popup screen
     * @param QRCode The QR code to be shown.
     */
    private void showQRCode(final Bitmap QRCode) {

        Matrix matrix = new Matrix();

        // resize the bit map
        matrix.postScale(4, 4);
        Bitmap largerCode = Bitmap.createBitmap(QRCode, 0, 0, QRCode.getWidth(), QRCode.getHeight(), matrix, true);

        ImageView imageView = new ImageView(this);
        imageView.setImageBitmap(largerCode);
        AlertDialog.Builder builder = new AlertDialog.Builder(this).
                setView(imageView).
                setPositiveButton("Done", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builder.create().show();

    }



}
