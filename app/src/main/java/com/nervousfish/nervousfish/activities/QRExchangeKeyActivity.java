package com.nervousfish.nervousfish.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;

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
                Intent intent = new Intent("com.google.zxing.client.android.SCAN");
                intent.putExtra("com.google.zxing.client.android.SCAN.SCAN_MODE", "QR_CODE_MODE");
                startActivityForResult(intent, 0);
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
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {

                String contents = intent.getStringExtra("SCAN_RESULT");
                // Handle successful scan
                try {
                    addNewContact(contents);
                } catch (IOException e) {
                    LOGGER.error("IOException when adding new contact");
                }
            } else if (resultCode == RESULT_CANCELED) {
                // Handle cancel
            }
        }
    }


    /**
     * Adds new contact with the scanned key and opens change
     * @param QRMessage
     */
    private void addNewContact(String QRMessage) throws IOException {
        //TODO: Add recognizer for contact name to avoid saving the same key twice (add your personal name to QR code)

        Contact contact = new Contact("<new contact>", QRGenerator.deconstructToKey(QRMessage));
        final IDatabase database = this.serviceLocator.getDatabase();
        database.addContact(contact);

        Intent intent = new Intent(this, ContactActivity.class);
        intent.putExtra(ConstantKeywords.SERVICE_LOCATOR, this.serviceLocator);
        intent.putExtra(ConstantKeywords.CONTACT, contact);
        this.startActivity(intent);
        
    }

    private void showQRCode(final Bitmap QRCode) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        final AlertDialog dialog = builder.create();
        LayoutInflater inflater = getLayoutInflater();
        View dialogLayout = inflater.inflate(R.layout.qrcode, null);
        dialog.setView(dialogLayout);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        dialog.show();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface d) {
                ImageView imageView = (ImageView) dialog.findViewById(R.id.QRCodeImage);
                imageView.setImageBitmap(QRCode);
            }
        });
    }



}
