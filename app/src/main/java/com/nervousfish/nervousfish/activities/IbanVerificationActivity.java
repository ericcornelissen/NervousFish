package com.nervousfish.nervousfish.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.nervousfish.nervousfish.ConstantKeywords;
import com.nervousfish.nervousfish.R;
import com.nervousfish.nervousfish.data_objects.Contact;
import com.nervousfish.nervousfish.data_objects.KeyPair;
import com.nervousfish.nervousfish.data_objects.Profile;
import com.nervousfish.nervousfish.exceptions.DatabaseException;
import com.nervousfish.nervousfish.service_locator.BlockchainWrapper;
import com.nervousfish.nervousfish.service_locator.IServiceLocator;
import com.nervousfish.nervousfish.service_locator.NervousFish;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.PublicKey;

import cn.pedant.SweetAlert.SweetAlertDialog;
import nl.tudelft.ewi.ds.bankver.BankVer;
import nl.tudelft.ewi.ds.bankver.IBAN;

/**
 * An {@link Activity} that beams NDEF Messages to Other Devices.
 */
@SuppressWarnings({"PMD.TooManyMethods", "PMD.ExcessiveImports", "checkstyle:ClassFanOutComplexity"})
public final class IbanVerificationActivity extends Activity {

    public static final String ED25519_KEY = "ED25519 key";
    private static final Logger LOGGER = LoggerFactory.getLogger("IbanVerificationActivity");
    private Contact contact;
    private BankVer bankVer;
    private String challenge;
    private BlockchainWrapper blockchainWrapper;

    /**
     * {@inheritDoc}
     */
    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_iban_verification);

        final IServiceLocator serviceLocator = NervousFish.getServiceLocator();
        final Intent intent = this.getIntent();
        this.contact = (Contact) intent.getSerializableExtra(ConstantKeywords.CONTACT);

        final Profile profile = serviceLocator.getDatabase().getProfile();

        ListviewActivityHelper.setText(this, this.contact.getName(), R.id.verification_page_name);
        ListviewActivityHelper.setText(this, this.contact.getIbanAsString(), R.id.verification_page_iban);

        final ImageButton backButton = (ImageButton) this.findViewById(R.id.back_button_iban_verification);
        backButton.setOnClickListener(v -> this.finish());

        this.blockchainWrapper = new BlockchainWrapper(profile);
        this.bankVer = new BankVer(this, blockchainWrapper);
        this.bankVer.setProperty(BankVer.SettingProperty.BANK_TYPE, "Bunq");
        this.bankVer.setProperty(BankVer.SettingProperty.BUNQ_API_KEY, "55ee97968338182ba528595d05ad9ba3eaf6bcd6f8d1c6e805ba1b29c2d1ba7c");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onBackPressed() {
        LOGGER.info("Back button was pressed");
        this.finish();
    }

    /**
     * Gets triggered when the manual verification button is pressed.
     *
     * @param view The button clicked
     */
    public void onManualVerificationClick(final View view) throws InvalidKeyException {
        LOGGER.info("Manual verification button was pressed");
        try {
            this.challenge = this.bankVer.createManualChallenge(this.contact.getIban());
            this.showManualVerificationCodes();
        } catch (final IllegalStateException e) {
            LOGGER.warn("No ED25519 key yet", e);
            this.askUserForNewED25519();
        }
    }

    /**
     * Gets triggered when the bunq verification button is pressed.
     *
     * @param view The button clicked
     */
    public void onBunqVerificationClick(final View view) {
        LOGGER.info("Bunq verification button was pressed");
        new SweetAlertDialog(this, SweetAlertDialog.NORMAL_TYPE)
                .setTitleText(this.getString(R.string.iban_verification_activity_title))
                .setContentText(this.getString(R.string.allow_bunq_challenge))
                .setConfirmText(this.getString(R.string.yes))
                .setCancelText(this.getString(R.string.no))
                .showCancelButton(true)
                .setConfirmClickListener(sweetAlertDialog -> {
                    sweetAlertDialog.dismissWithAnimation();
                    this.bankVer.createOnlineChallenge(this.contact.getIban());
                    new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                            .setTitleText(this.getString(R.string.iban_verification_activity_title))
                            .setContentText(this.getString(R.string.successfully_initiated_challenge))
                            .setConfirmText(this.getString(R.string.dialog_ok))
                            .setConfirmClickListener(dialog -> {
                                dialog.dismissWithAnimation();
                                this.informAcceptChallengeButton();
                            })
                            .show();
                })
                .setCancelClickListener(SweetAlertDialog::dismissWithAnimation)
                .show();

    }

    /**
     * Called by the Show IBAN button
     *
     * @param view The button that called the function
     */
    public void showIban(final View view) {
        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(R.string.iban_contact);
        alert.setMessage(this.contact.getIbanAsString());
        alert.setPositiveButton(this.getString(R.string.dialog_ok), null);
        alert.show();
    }

    /**
     * Called by the Copy IBAN button
     *
     * @param view The button that called the function
     */
    public void copyIban(final View view) {
        final ClipboardManager clipboard = (ClipboardManager) this.getSystemService(Context.CLIPBOARD_SERVICE);
        final ClipData clip = ClipData.newPlainText("", this.contact.getIbanAsString());
        clipboard.setPrimaryClip(clip);

        Toast.makeText(this, "Copied the IBAN to your clipboard", Toast.LENGTH_SHORT).show();
    }

    /**
     * Called by the Show Challenge button
     *
     * @param view The button that called the function
     */
    public void showChallenge(final View view) {
        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(R.string.challenge_contact);
        alert.setMessage(this.challenge);
        alert.setPositiveButton(this.getString(R.string.dialog_ok), null);
        alert.show();
    }

    /**
     * Called by the Copy Challenge button
     *
     * @param view The button that called the function
     */
    public void copyChallenge(final View view) {
        final ClipboardManager clipboard = (ClipboardManager) this.getSystemService(Context.CLIPBOARD_SERVICE);
        final ClipData clip = ClipData.newPlainText("", this.challenge);
        clipboard.setPrimaryClip(clip);

        Toast.makeText(this, "Copied the challenge to your clipboard", Toast.LENGTH_SHORT).show();
    }

    /**
     * Called when the user clicks the button to verify the iban response from his partner
     *
     * @param view The button that was clicked
     */
    public void verifyIbanResponse(final View view) {
        /*final String response = ((EditText) this.findViewById(R.id.edit_iban_response)).getText().toString();
        final boolean responseValid = ChallengeResponse.isValidResponse(response, this.contact.getFirstEd25519Key());
        if (responseValid) {*/
        this.blockchainWrapper.setIbanVerified(contact.getFirstEd25519Key(), contact.getIban(), "");
        final Contact newContact = new Contact(contact.getName(), contact.getKeys(),
                contact.getIban(), true);
        new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText(this.getString(R.string.iban_verification_success))
                .setContentText(this.getString(R.string.iban_verification_success_explanation))
                .setConfirmClickListener(sweetAlertDialog -> {
                    sweetAlertDialog.dismissWithAnimation();
                    setResult(RESULT_FIRST_USER,
                            new Intent().putExtra(ConstantKeywords.CONTACT, newContact));
                    this.finish();
                })
                .show();
        /*} else {
            new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText(this.getString(R.string.iban_verification_failure))
                    .setContentText(this.getString(R.string.iban_verification_failure_explanation))
                    .setConfirmClickListener(SweetAlertDialog::dismissWithAnimation)
                    .show();
        }*/
    }

    private void askUserForNewED25519() {


        new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText(this.getString(R.string.no_edsa_key_yet))
                .setContentText(this.getString(R.string.edsa_key_needed_iban))
                .setCancelText(this.getString(R.string.no))
                .setConfirmText(this.getString(R.string.yes))
                .setConfirmClickListener(sweetAlertDialog -> {
                    sweetAlertDialog.dismissWithAnimation();

                    final KeyPair keyPair = NervousFish.getServiceLocator().getKeyGenerator().generateEd25519KeyPair(ED25519_KEY);
                    final Profile profile = NervousFish.getServiceLocator().getDatabase().getProfile();
                    profile.addKeyPair(keyPair);
                    final BlockchainWrapper blockchainWrapper = new BlockchainWrapper(profile);
                    this.bankVer = new BankVer(this, blockchainWrapper);
                    try {
                        NervousFish.getServiceLocator().getDatabase().updateProfile(profile);
                    } catch (final IOException e) {
                        LOGGER.error("Could not update the database with the new profile", e);
                        throw new DatabaseException(e);
                    }
                    try {
                        this.onManualVerificationClick(null);
                    } catch (InvalidKeyException e) {
                        LOGGER.error("This error cannot occur - manual verification button clicked", e);
                    }
                })
                .setCancelClickListener(SweetAlertDialog::dismissWithAnimation)
                .show();
    }

    private void showManualVerificationCodes() {
        final AlertDialog.Builder alert = new AlertDialog.Builder(this);

        final LayoutInflater inflater = this.getLayoutInflater();
        @SuppressLint("InflateParams")
        // 1) Suppressed because this dialog does not need a parent
        final View dialogView = inflater.inflate(R.layout.show_manual_verification_codes, null);
        alert.setTitle(this.getString(R.string.iban_manual_verification));
        alert.setMessage(this.getString(R.string.manual_verification_explanation));
        alert.setView(dialogView);

        alert.setPositiveButton(this.getString(R.string.dialog_ok), (dialog, which) -> this.informAcceptChallengeButton());
        alert.show();

        ((Button) dialogView.findViewById(R.id.contact_iban)).setText(this.contact.getIbanAsString());
        ((Button) dialogView.findViewById(R.id.contact_challenge)).setText(this.challenge);
    }

    private void informAcceptChallengeButton() {
        new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText(this.getString(R.string.please_note))
                .setContentText(this.getString(R.string.iban_button_in_main_screen))
                .show();
    }
}