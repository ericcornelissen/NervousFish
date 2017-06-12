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
import com.nervousfish.nervousfish.service_locator.NervousFish;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * An {@link Activity} that is used for pairing using QR codes
 */
@SuppressWarnings({"checkstyle:ClassFanOutComplexity", "PMD.AccessorClassGeneration", "PMD.ExcessiveImports"})
//  1)  This warning is because the class relies on too many external classes, which can't really be avoided
//  2)  This warning doesn't make sense since I can't instantiate the object in the constructor as I
//      need the qr message to create the editnameclicklistener in the addnewcontact method
//  3)  Uses many utility imports.
public class QRExchangeKeyActivity extends AppCompatActivity {

    private static final Logger LOGGER = LoggerFactory.getLogger("QRExchangeKeyActivity");

    private IServiceLocator serviceLocator;
    private AlertDialog lastDialog;
    private IKey publicKey;

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_qrexchange);
        this.serviceLocator = NervousFish.getServiceLocator();

        //TODO: Get the user's generated public key from the database
        final IKeyGenerator keyGenerator = this.serviceLocator.getKeyGenerator();
        final KeyPair pair = keyGenerator.generateRSAKeyPair("test");
        this.publicKey = pair.getPublicKey();

        LOGGER.info("Activity created");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent intent) {
        final IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanResult == null) {
            LOGGER.error("No scan result in QR Scanner");
        } else {
            final String result = scanResult.getContents();
            addNewContact(result);
        }
    }

    /**
     * Returns to the previous activity.
     * @param view - the imagebutton
     */
    public void onBackButtonClick(final View view) {
        LOGGER.info("Return to previous screen");
        finish();
    }

    /**
     * Shows the QR code of the person's public key on screen.
     * @param view - the imagebutton
     */
    public void onShowQRButtonClick(final View view) {
        LOGGER.info("Started generating QR code");
        final String space = " ";
        final Bitmap qrCode = QRGenerator.encode(publicKey.getType() + space + publicKey.getName()
                + space + publicKey.getKey());
        showQRCode(qrCode);
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
            final EditNameClickListener enClickListener = new EditNameClickListener(editName, key);
            final AlertDialog.Builder builder = new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.contact_set_name))
                    .setView(editName)
                    .setPositiveButton(getString(R.string.done), enClickListener);
            lastDialog = builder.create();
            lastDialog.show();
        } catch (IllegalArgumentException e) {
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
                .setPositiveButton(getString(R.string.done), new QRCloser());

        ((ViewGroup) imageView.getParent()).removeView(imageView);
        lastDialog = builder.create();
        lastDialog.show();
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

    private final class EditNameClickListener implements DialogInterface.OnClickListener {

        private final EditText editName;
        private final IKey key;

        /**
         * Constructor for editname click listener.
         * @param editName The textinput from which we show and get the name from.
         * @param key The key made from the QR code.
         */
        private EditNameClickListener(final EditText editName, final IKey key) {
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
                final String name = editName.getText().toString();

                final Contact contact = new Contact(name, key);
                final IDatabase database = serviceLocator.getDatabase();
                database.addContact(contact);
                dialog.dismiss();
                final Intent intent = new Intent(QRExchangeKeyActivity.this, ContactActivity.class);
                intent.putExtra(ConstantKeywords.CONTACT, contact);
                QRExchangeKeyActivity.this.startActivity(intent);
            } catch (final IOException e) {
                LOGGER.error("IOException while adding new contact", e);
            } catch (final IllegalArgumentException e) {
                LOGGER.error("IllegalArgumentException while adding new contact", e);
                new SweetAlertDialog(QRExchangeKeyActivity.this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText(getString(R.string.contact_exists_give_unique_name))
                        .setContentText(getString(R.string.contact_exists_message))
                        .setConfirmText(getString(R.string.dialog_ok))
                        .show();
            }
        }



    }


}
