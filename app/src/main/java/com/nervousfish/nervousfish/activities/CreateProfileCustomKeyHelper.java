package com.nervousfish.nervousfish.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.nervousfish.nervousfish.R;
import com.nervousfish.nervousfish.data_objects.IKey;
import com.nervousfish.nervousfish.data_objects.RSAKey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Helps {@link CreateProfileActivity} by providing functions to let the user input his own custom keypair
 * instead of generating a new one.
 */
final class CreateProfileCustomKeyHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger("CreateProfileCustomKeyHelper");
    private static final String CUSTOM_PUBLIC_KEY = "Custom public key";
    private static final String CUSTOM_PRIVATE_KEY = "Custom private key";
    private static final int KEY_LINES = 5;
    private final DialogInterface.OnClickListener confirmKeyTypeListener;
    private final DialogInterface.OnClickListener confirmPublicKeyListener;
    private final DialogInterface.OnClickListener confirmPrivateKeyListener;
    private final Activity activity;
    private final CreateProfileActivity.CustomKeyPairSetter customKeyPairSetter;
    private EditText publicKeyModulusInput;
    private EditText publicKeyExponentInput;
    private EditText privateKeyModulusInput;
    private EditText privateKeyExponentInput;
    private RadioButton rsaRadioButton;
    private RadioButton ed25519RadioButton;
    private IKey.Types customKeyType;
    private String customPublicKeyModulus = "";
    private String customPublicKeyExponent = "";
    private String customPrivateKeyModulus = "";
    private String customPrivateKeyExponent = "";

    /**
     * Constructs a new helper class that provides functions to let the user input his own custom keypair
     * instead of generating a new one.
     *
     * @param activity            The activity responsible for displaying alerts
     * @param customKeyPairSetter Used to set the final private and public key
     */
    CreateProfileCustomKeyHelper(final Activity activity, final CreateProfileActivity.CustomKeyPairSetter customKeyPairSetter) {
        this.activity = activity;
        this.customKeyPairSetter = customKeyPairSetter;

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
            this.customPublicKeyModulus = this.publicKeyModulusInput.getText().toString();
            this.customPublicKeyExponent = this.publicKeyExponentInput.getText().toString();
            if (this.customPublicKeyModulus.isEmpty() || this.customPublicKeyExponent.isEmpty()) {
                LOGGER.warn("Public key was too short. Modulus length was: {}, exponent length was: {}",
                        this.customPublicKeyModulus.length(), this.customPublicKeyExponent.length());
                this.askForPublicKey(true);
            } else {
                this.askForPrivateKey(false);
            }
        };

        this.confirmPrivateKeyListener = (dialog, which) -> {
            LOGGER.info("Confirmation button private key clicked");
            this.customPrivateKeyModulus = this.privateKeyModulusInput.getText().toString();
            if (this.customPrivateKeyModulus.isEmpty()) {
                LOGGER.warn("Private key was too short. Modulus length was: {}, exponent length was: {}",
                        this.customPrivateKeyModulus.length(), this.customPrivateKeyExponent.length());
                this.askForPrivateKey(true);
            } else {
                final RSAKey publicKey = new RSAKey(CUSTOM_PUBLIC_KEY, this.customPublicKeyModulus, this.customPrivateKeyExponent);
                final RSAKey privateKey = new RSAKey(CUSTOM_PRIVATE_KEY, this.customPrivateKeyModulus, this.customPrivateKeyExponent);
                this.customKeyPairSetter.setRSAKeyPair(publicKey, privateKey);
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

        alert.setPositiveButton(this.activity.getString(R.string.confirm), this.confirmKeyTypeListener);
        alert.setNegativeButton(this.activity.getString(R.string.cancel), null);
        alert.show();
    }

    /**
     * Ask the user for his public key
     *
     * @param warnTooShort True if the dialog should warn the user that the key should have more than 0 characters
     */
    @SuppressLint("InflateParams")
    // 1) Suppressed because this view has no root because it is a dialog
    private void askForPublicKey(final boolean warnTooShort) {
        final AlertDialog.Builder alert = new AlertDialog.Builder(this.activity);

        final LayoutInflater inflater = this.activity.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.enter_rsa_key, null);
        alert.setView(dialogView);

        this.publicKeyModulusInput = (EditText) dialogView.findViewById(R.id.edit_custom_rsa_key_modulus);
        this.publicKeyModulusInput.setText(this.customPublicKeyModulus);
        this.publicKeyExponentInput = (EditText) dialogView.findViewById(R.id.edit_custom_rsa_key_exponent);
        this.publicKeyExponentInput.setText(this.customPublicKeyExponent);

        alert.setTitle(this.activity.getString(R.string.use_existing_keypair));
        if (warnTooShort) {
            alert.setMessage(this.activity.getString(R.string.enter_public_key_too_short));
        } else {
            alert.setMessage(this.activity.getString(R.string.enter_public_key));
        }

        alert.setPositiveButton(this.activity.getString(R.string.confirm), this.confirmPublicKeyListener);
        alert.setNegativeButton(this.activity.getString(R.string.cancel), null);
        alert.show();
    }

    /**
     * Ask the user for his private key
     *
     * @param warnTooShort True if the dialog should warn the user that the key should have more than 0 characters
     */
    @SuppressLint("InflateParams")
    // 1) Suppressed because this view has no root because it is a dialog
    private void askForPrivateKey(final boolean warnTooShort) {
        final AlertDialog.Builder alert = new AlertDialog.Builder(this.activity);

        final LayoutInflater inflater = this.activity.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.enter_rsa_key, null);
        alert.setView(dialogView);

        this.privateKeyModulusInput = (EditText) dialogView.findViewById(R.id.edit_custom_rsa_key_modulus);
        this.privateKeyModulusInput.setText(this.customPrivateKeyModulus);
        this.privateKeyExponentInput = (EditText) dialogView.findViewById(R.id.edit_custom_rsa_key_exponent);
        this.privateKeyExponentInput.setText(this.customPrivateKeyExponent);

        alert.setTitle(this.activity.getString(R.string.use_existing_keypair));
        if (warnTooShort) {
            alert.setMessage(this.activity.getString(R.string.enter_private_key_too_short));
        } else {
            alert.setMessage(this.activity.getString(R.string.enter_private_key));
        }

        alert.setPositiveButton(this.activity.getString(R.string.confirm), this.confirmPrivateKeyListener);
        alert.setNegativeButton(this.activity.getString(R.string.cancel), null);
        alert.show();
    }
}
