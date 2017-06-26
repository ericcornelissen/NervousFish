package com.nervousfish.nervousfish.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.nervousfish.nervousfish.ConstantKeywords;
import com.nervousfish.nervousfish.R;
import com.nervousfish.nervousfish.data_objects.Contact;
import com.nervousfish.nervousfish.data_objects.Profile;
import com.nervousfish.nervousfish.service_locator.BlockchainWrapper;
import com.nervousfish.nervousfish.service_locator.IServiceLocator;
import com.nervousfish.nervousfish.service_locator.NervousFish;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.security.InvalidKeyException;

import nl.tudelft.ewi.ds.bankver.BankVer;

/**
 * An {@link Activity} that beams NDEF Messages to Other Devices.
 */
@SuppressWarnings("PMD.SingularField")
public final class IbanVerificationActivity extends Activity {

    private static final Logger LOGGER = LoggerFactory.getLogger("IbanVerificationActivity");
    private IServiceLocator serviceLocator;
    private Contact contact;
    private Profile profile;
    private BankVer bankVer;

    /**
     * {@inheritDoc}
     */
    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_iban_verification);

        this.serviceLocator = NervousFish.getServiceLocator();
        final Intent intent = this.getIntent();
        this.contact = (Contact) intent.getSerializableExtra(ConstantKeywords.CONTACT);

        try {
            this.profile = this.serviceLocator.getDatabase().getProfile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        ListviewActivityHelper.setText(this, this.contact.getName(), R.id.verification_page_name);
        ListviewActivityHelper.setText(this, this.contact.getIbanAsString(), R.id.verification_page_iban);

        final ImageButton backButton = (ImageButton) this.findViewById(R.id.back_button_change);
        backButton.setOnClickListener(v -> this.finish());

        final BlockchainWrapper blockchainWrapper = new BlockchainWrapper(this.profile);
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
        this.bankVer.createManualChallenge(contact.getIban());
    }

    /**
     * Gets triggered when the bunq verification button is pressed.
     *
     * @param view The button clicked
     */
    public void onBunqVerificationClick(final View view) {
        LOGGER.info("Bunq verification button was pressed");
        this.bankVer.createOnlineChallenge(contact.getIban());
    }
}