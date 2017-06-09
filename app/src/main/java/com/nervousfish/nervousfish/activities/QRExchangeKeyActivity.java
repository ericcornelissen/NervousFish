package com.nervousfish.nervousfish.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

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
 * An {@link Activity} that is used for pairing using QR codes
 */
@SuppressWarnings({"checkstyle:ClassFanOutComplexity", "PMD.AccessorClassGeneration"})
//  1)  This warning is because the class relies on too many external classes, which can't really be avoided
//  2)  This warning doesn't make sense since I can't instantiate the object in the constructor as I
//      need the qr message to create the editnameclicklistener in the addnewcontact method
public final class QRExchangeKeyActivity extends AppCompatActivity {

    private static final Logger LOGGER = LoggerFactory.getLogger("QRExchangeKeyActivity");

    private IServiceLocator serviceLocator;
    private IKeyGenerator keyGenerator;
    private AlertDialog lastDialog;
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

        final Intent intent = this.getIntent();
        this.serviceLocator = (IServiceLocator) intent.getSerializableExtra(ConstantKeywords.SERVICE_LOCATOR);
        this.keyGenerator = this.serviceLocator.getKeyGenerator();


        //TODO: Get the user's generated public key from the database
        final KeyPair pair = this.keyGenerator.generateRSAKeyPair("test");
        this.publicKey = pair.getPublicKey();

    }

    /**
     * Returns to the previous activity.
     * @param view - the imagebutton
     */
    public void onBackButtonClick(final View view) {
        LOGGER.info("Return to previous screen");
        this.finish();
    }

    /**
     * Shows the QR code of the person's public key on screen.
     * @param view - the imagebutton
     */
    public void onShowQRButtonClick(final View view) {
        LOGGER.info("Started generating QR code");
        final Bitmap qrCode = QRGenerator.encode(String.format("%s %s %s", this.publicKey.getType(), this.publicKey.getName(), this.publicKey.getKey()));
        this.showQRCode(qrCode);
    }

    /**
     * Starts the scan qr feature.
     * @param view - the imagebutton
     */
    public void onScanButtonClick(final View view) {
        LOGGER.info("Started scanning QR code");
        final IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.initiateScan();
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
        } else {
            final String result = scanResult.getContents();
            this.addNewContact(result);
        }


    }


    /**
     * Adds new contact with the scanned key and opens change
     *
     * @param qrMessage The information we got from the QR code.
     */
    private void addNewContact(final String qrMessage) {
        try {
            LOGGER.info("Adding new contact to database");
            //TODO: Add recognizer for contact name to avoid saving the same key twice (add your personal name to QR code)
            final IKey key = QRGenerator.deconstructToKey(qrMessage);
            final EditText editName = new EditText(this);
            editName.setInputType(InputType.TYPE_CLASS_TEXT);
            final QRExchangeKeyActivity.EditNameClickListener enClickListener = new QRExchangeKeyActivity.EditNameClickListener(this, this.serviceLocator, editName, key);
            final AlertDialog.Builder builder = new AlertDialog.Builder(this)
                    .setTitle(this.getString(R.string.contact_set_name))
                    .setView(editName)
                    .setPositiveButton(this.getString(R.string.done), enClickListener);
            this.lastDialog = builder.create();
            this.lastDialog.show();
        } catch (final IllegalArgumentException e) {
            LOGGER.error("Illegal argument exception in addNewContact", e);
        }
    }




    /**
     * Shows the QR Code in an alert dialog popup screen
     *
     * @param qrCode The QR code to be shown.
     */
    @SuppressLint("InflateParams")
    private void showQRCode(final Bitmap qrCode) {

        final LayoutInflater li = LayoutInflater.from(this);
        final View myView = li.inflate(R.layout.qrcode, null);

        final ImageView imageView = (ImageView) myView.findViewById(R.id.QR_code_image);
        imageView.setImageBitmap(qrCode);
        final AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setView(imageView)
                .setPositiveButton(this.getString(R.string.done), new QRExchangeKeyActivity.QRCloser());

        ((ViewGroup) imageView.getParent()).removeView(imageView);
        this.lastDialog = builder.create();
        this.lastDialog.show();
    }


    private static final class QRCloser implements DialogInterface.OnClickListener {
        /**
         * {@inheritDoc}
         */
        @Override
        public void onClick(final DialogInterface dialog, final int which) {
            dialog.dismiss();
        }
    }

    private static final class EditNameClickListener implements DialogInterface.OnClickListener {
        private final Activity activity;
        private final IServiceLocator serviceLocator;
        private final IDatabase database;
        private final EditText editName;
        private final IKey key;

        /**
         * Constructor for editname click listener.
         * @param editName The textinput from which we show and get the name from.
         * @param key The key made from the QR code.
         */
        private EditNameClickListener(final Activity activity, final IServiceLocator serviceLocator, final EditText editName, final IKey key) {
            this.activity = activity;
            this.serviceLocator = serviceLocator;
            this.database = serviceLocator.getDatabase();
            this.editName = editName;
            this.key = key;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void onClick(final DialogInterface dialog, final int which) {
            LOGGER.info("Adding new contact with name input");
            try {
                final String name = this.editName.getText().toString();

                final Contact contact = new Contact(name, this.key);
                this.database.addContact(contact);
                dialog.dismiss();
                final Intent intent = new Intent(this.activity, ContactActivity.class);
                intent.putExtra(ConstantKeywords.SERVICE_LOCATOR, this.serviceLocator);
                intent.putExtra(ConstantKeywords.CONTACT, contact);
                this.activity.startActivity(intent);
            } catch (final IOException e) {
                LOGGER.error("IOException while adding new contact", e);
            } catch (final IllegalArgumentException e) {
                LOGGER.error("IllegalArgumentException while adding new contact", e);
                new SweetAlertDialog(this.activity, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText(this.activity.getString(R.string.contact_already_exists))
                        .setContentText(this.activity.getString(R.string.contact_exists_message))
                        .setConfirmText(this.activity.getString(R.string.dialog_ok))
                        .show();
            }
        }
    }
}
