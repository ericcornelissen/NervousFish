package com.nervousfish.nervousfish.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.Image;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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

import cn.pedant.SweetAlert.SweetAlertDialog;


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
        final KeyPair pair = keyGenerator.generateRSAKeyPair("test");
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

        final ImageButton backButton = (ImageButton) findViewById(R.id.backButtonQRExchange);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        final IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanResult != null) {
            final String result = scanResult.getContents();
            addNewContact(result);

        } else {
            LOGGER.error("No scan result in QR Scanner");
        }


    }


    /**
     * Adds new contact with the scanned key and opens change
     *
     * @param QRMessage
     */
    private void addNewContact(final String QRMessage) {
        //TODO: Add recognizer for contact name to avoid saving the same key twice (add your personal name to QR code)
        final IKey key = QRGenerator.deconstructToKey(QRMessage);
        final EditText editName = new EditText(this);
        editName.setInputType(InputType.TYPE_CLASS_TEXT);
        AlertDialog.Builder builder = new AlertDialog.Builder(this).
                setTitle(getString(R.string.contact_set_name)).
                setView(editName).
                setPositiveButton(getString(R.string.popup_done), new EditNameClickListener(editName, key));
        builder.show();

    }

    /**
     * Shows the QR Code in an alert dialog popup screen
     *
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
                setPositiveButton(getString(R.string.popup_done), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builder.create().show();

    }


    private final class EditNameClickListener implements DialogInterface.OnClickListener {

        final EditText editName;
        final IKey key;

        /**
         * Constructor for editname click listener.
         * @param editName The textinput from which we show and get the name from.
         * @param key The key made from the QR code.
         */
        public EditNameClickListener(EditText editName, IKey key){
            this.editName = editName;
            this.key = key;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void onClick(DialogInterface dialog, int which) {
            try {
                String name = editName.getText().toString();

                final Contact contact = new Contact(name, key);
                final IDatabase database = serviceLocator.getDatabase();
                database.addContact(contact);
                dialog.dismiss();
                final Intent intent = new Intent(QRExchangeKeyActivity.this, ContactActivity.class);
                intent.putExtra(ConstantKeywords.SERVICE_LOCATOR, serviceLocator);
                intent.putExtra(ConstantKeywords.CONTACT, contact);
                QRExchangeKeyActivity.this.startActivity(intent);
            }catch (final IOException e) {
                LOGGER.error(e.getMessage());
            } catch (final IllegalArgumentException e) {
                LOGGER.error(e.getMessage());
                new SweetAlertDialog(QRExchangeKeyActivity.this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText(getString(R.string.contact_exists))
                        .setContentText(getString(R.string.contact_exists_message))
                        .setConfirmText(getString(R.string.dialog_ok))
                        .show();
            } catch (final NullPointerException e) {
                LOGGER.error("Wrong input for scanner");
            }
        }
    }


}
