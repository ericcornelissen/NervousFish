package com.nervousfish.nervousfish.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.RadioButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;

import com.nervousfish.nervousfish.R;
import com.nervousfish.nervousfish.data_objects.IKey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Helps {@link CreateProfileActivity} by providing functions to let the user input his own custom keypair
 * instead of generating a new one.
 */
final class CreateProfileCustomKeyHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger("CreateProfileCustomKeyHelper");
    private final DialogInterface.OnClickListener confirmKeyTypeListener;
    private final DialogInterface.OnClickListener confirmPublicKeyListener;
    private final DialogInterface.OnClickListener confirmPrivateKeyListener;
    private EditText publicKeyInput;
    private EditText privateKeyInput;
    private RadioButton rsaRadioButton;
    private RadioButton ed25519RadioButton;
    private IKey.Types customKeyType;
    private String customPublicKey = "";
    private String customPrivateKey = "";
    private final Activity activity;
    private final CreateProfileActivity.CustomKeySetter customKeySetter;

    /**
     * Constructs a new helper class that provides functions to let the user input his own custom keypair
     * instead of generating a new one.
     *
     * @param activity The activity responsible for displaying alerts
     * @param customKeySetter Used to set the final private and public key
     */
    CreateProfileCustomKeyHelper(final Activity activity, final CreateProfileActivity.CustomKeySetter customKeySetter) {
        this.activity = activity;
        this.customKeySetter = customKeySetter;

        this.confirmKeyTypeListener = (dialog, which) -> {
            LOGGER.info("Confirmation button key type clicked");
            final boolean rsaTypeEnabled = this.rsaRadioButton.isChecked();
            final boolean ed25519TypeEnabled = this.ed25519RadioButton.isChecked();
            assert rsaTypeEnabled != ed25519TypeEnabled;
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("Key type clicked is: {}", rsaTypeEnabled ? "RSA" : ed25519TypeEnabled ? "ED25519" : "<undefined>");
            }
            this.customKeyType = rsaTypeEnabled ? IKey.Types.RSA : IKey.Types.Ed25519;

            if (this.customKeyType == IKey.Types.RSA) {
                this.askForPublicKey(false);
            } else {
                new SweetAlertDialog(this.activity, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText(this.activity.getString(R.string.not_yet_implemented))
                        .setContentText(this.activity.getString(R.string.custom_ed25519_key_not_yet_implemented))
                        .setConfirmText(this.activity.getString(R.string.dialog_ok))
                        .show();
            }
        };

        this.confirmPublicKeyListener = (dialog, which) -> {
            LOGGER.info("Confirmation button public key clicked");
            this.customPublicKey = this.publicKeyInput.getText().toString();
            LOGGER.info("Public key entered is: {}", this.customPublicKey);
            if (this.customPublicKey.isEmpty()) {
                LOGGER.warn("Public key was too short. Length was: {}", this.customPublicKey.length());
                this.askForPublicKey(true);
            } else {
                this.askForPrivateKey(false);
            }
        };

        this.confirmPrivateKeyListener = (dialog, which) -> {
            LOGGER.info("Confirmation button private key clicked");
            this.customPrivateKey = this.privateKeyInput.getText().toString();
            LOGGER.info("Private key entered is: {}", this.customPrivateKey);
            if (this.customPrivateKey.isEmpty()) {
                LOGGER.warn("Private key was too short. Length was: {}", this.customPrivateKey.length());
                this.askForPrivateKey(true);
            } else {
                this.customKeySetter.setKeyType(this.customKeyType);
                this.customKeySetter.setPublicKey(this.customPublicKey);
                this.customKeySetter.setPrivateKey(this.customPrivateKey);
            }
        };
    }

    /**
     * First asks for the custom key type, then for the public key and as last for the private key.
     */
    void askForCustomKeypair() {
        this.askForKeyType();
    }

    /**
     * Ask the user for the type of his own key
     */
    private void askForKeyType() {
        final AlertDialog.Builder alert = new AlertDialog.Builder(this.activity);
        final RadioGroup keyTypeRadioButtonesWrapper = new RadioGroup(this.activity);
        this.rsaRadioButton = new RadioButton(this.activity);
        this.ed25519RadioButton = new RadioButton(this.activity);
        this.rsaRadioButton.setChecked(true);
        this.rsaRadioButton.setText(this.activity.getResources().getString(R.string.rsa_key));
        this.ed25519RadioButton.setText(this.activity.getResources().getString(R.string.ed25519_key));
        this.rsaRadioButton.setId(0);
        //noinspection ResourceType Because the ID is only used because the radiogroup needs them and is not used elsewhere
        this.ed25519RadioButton.setId(1);
        keyTypeRadioButtonesWrapper.addView(this.rsaRadioButton);
        keyTypeRadioButtonesWrapper.addView(this.ed25519RadioButton);
        keyTypeRadioButtonesWrapper.setOrientation(LinearLayout.VERTICAL);

        alert.setTitle(this.activity.getString(R.string.use_existing_keypair));
        alert.setMessage(this.activity.getString(R.string.choose_your_keytype));
        alert.setView(keyTypeRadioButtonesWrapper);
        alert.setCancelable(true);

        alert.setPositiveButton(this.activity.getString(R.string.confirm), this.confirmKeyTypeListener);
        alert.show();
    }

    /**
     * Ask the user for his public key
     *
     * @param warnTooShort True if the dialog should warn the user that the key should have more than 0 characters
     */
    private void askForPublicKey(final boolean warnTooShort) {
        final AlertDialog.Builder alert = new AlertDialog.Builder(this.activity);
        this.publicKeyInput = new EditText(this.activity);
        this.publicKeyInput.setText(this.customPublicKey);

        alert.setTitle(this.activity.getString(R.string.use_existing_keypair));
        if (warnTooShort) {
            alert.setMessage(this.activity.getString(R.string.enter_public_key_too_short));
        } else {
            alert.setMessage(this.activity.getString(R.string.enter_public_key));
        }
        alert.setView(this.publicKeyInput);
        alert.setCancelable(true);

        alert.setPositiveButton(this.activity.getString(R.string.confirm), this.confirmPublicKeyListener);
        alert.show();
    }

    /**
     * Ask the user for his private key
     *
     * @param warnTooShort True if the dialog should warn the user that the key should have more than 0 characters
     */
    private void askForPrivateKey(final boolean warnTooShort) {
        final AlertDialog.Builder alert = new AlertDialog.Builder(this.activity);
        this.privateKeyInput = new EditText(this.activity);
        this.privateKeyInput.setText(this.customPrivateKey);

        alert.setTitle(this.activity.getString(R.string.use_existing_keypair));
        if (warnTooShort) {
            alert.setMessage(this.activity.getString(R.string.enter_private_key_too_short));
        } else {
            alert.setMessage(this.activity.getString(R.string.enter_private_key));
        }
        alert.setView(this.privateKeyInput);
        alert.setCancelable(true);

        alert.setPositiveButton(this.activity.getString(R.string.confirm), this.confirmPrivateKeyListener);
        alert.show();
    }
}
