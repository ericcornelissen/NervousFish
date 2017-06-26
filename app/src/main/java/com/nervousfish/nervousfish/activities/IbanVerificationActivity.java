package com.nervousfish.nervousfish.activities;

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
import com.nervousfish.nervousfish.data_objects.Profile;
import com.nervousfish.nervousfish.exceptions.DatabaseException;
import com.nervousfish.nervousfish.service_locator.BlockchainWrapper;
import com.nervousfish.nervousfish.service_locator.IServiceLocator;
import com.nervousfish.nervousfish.service_locator.NervousFish;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.security.InvalidKeyException;

import cn.pedant.SweetAlert.SweetAlertDialog;
import nl.tudelft.ewi.ds.bankver.BankVer;

/**
 * An {@link Activity} that beams NDEF Messages to Other Devices.
 */
public final class IbanVerificationActivity extends Activity {

    private static final Logger LOGGER = LoggerFactory.getLogger("IbanVerificationActivity");
    private Contact contact;
    private BankVer bankVer;
    private String challenge;

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

        final Profile profile;
        try {
            profile = serviceLocator.getDatabase().getProfile();
        } catch (final IOException e) {
            throw new DatabaseException(e);
        }

        ListviewActivityHelper.setText(this, this.contact.getName(), R.id.verification_page_name);
        ListviewActivityHelper.setText(this, this.contact.getIbanAsString(), R.id.verification_page_iban);

        final ImageButton backButton = (ImageButton) this.findViewById(R.id.back_button_iban_verification);
        backButton.setOnClickListener(v -> this.finish());

        final BlockchainWrapper blockchainWrapper = new BlockchainWrapper(profile);
        this.bankVer = new BankVer(this, blockchainWrapper);
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
        this.challenge = "hoi";//this.bankVer.createManualChallenge(this.contact.getIban());
        this.informAcceptChallengeButton();
        this.showManualVerificationCodes();
    }

    /**
     * Gets triggered when the bunq verification button is pressed.
     *
     * @param view The button clicked
     */
    public void onBunqVerificationClick(final View view) {
        LOGGER.info("Bunq verification button was pressed");
        this.bankVer.createOnlineChallenge(this.contact.getIban());
        this.informAcceptChallengeButton();
    }

    public void showIban(final View view) {
        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(R.string.iban_contact);
        alert.setMessage(this.contact.getIbanAsString());
        alert.setPositiveButton(this.getString(R.string.dialog_ok), null);
        alert.show();
    }

    public void copyIban(final View view) {
        final ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        final ClipData clip = ClipData.newPlainText("", this.contact.getIbanAsString());
        clipboard.setPrimaryClip(clip);

        Toast.makeText(this, "Copied the IBAN to your clipboard", Toast.LENGTH_LONG).show();
    }

    public void showChallenge(final View view) {
        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(R.string.challenge_contact);
        alert.setMessage(this.challenge);
        alert.setPositiveButton(this.getString(R.string.dialog_ok), null);
        alert.show();
    }

    public void copyChallenge(final View view) {
        final ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        final ClipData clip = ClipData.newPlainText("", this.challenge);
        clipboard.setPrimaryClip(clip);

        Toast.makeText(this, "Copied the challenge to your clipboard", Toast.LENGTH_LONG).show();
    }

    private void showManualVerificationCodes() {
        final AlertDialog.Builder alert = new AlertDialog.Builder(this);

        final LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.show_manual_verification_codes, null);
        alert.setTitle(this.getString(R.string.iban_manual_verification));
        alert.setMessage(this.getString(R.string.manual_verification_explanation));
        alert.setView(dialogView);

        alert.setPositiveButton(this.getString(R.string.dialog_ok), null);
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