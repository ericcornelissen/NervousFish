package com.nervousfish.nervousfish.activities;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.nervousfish.nervousfish.ConstantKeywords;
import com.nervousfish.nervousfish.R;
import com.nervousfish.nervousfish.data_objects.Contact;
import com.nervousfish.nervousfish.data_objects.KeyPair;
import com.nervousfish.nervousfish.data_objects.Profile;
import com.nervousfish.nervousfish.modules.database.IDatabase;
import com.nervousfish.nervousfish.modules.pairing.INfcHandler;
import com.nervousfish.nervousfish.modules.pairing.events.NewDataReceivedEvent;
import com.nervousfish.nervousfish.service_locator.IServiceLocator;
import com.nervousfish.nervousfish.service_locator.NervousFish;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import nl.tudelft.ewi.ds.bankver.BankVer;

import static android.nfc.NdefRecord.createMime;

/**
 * An {@link Activity} that beams NDEF Messages to Other Devices.
 */
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
    public void onManualVerificationClick(final View view) {
        LOGGER.info("Manual verification button was pressed");
    }

    /**
     * Gets triggered when the bunq verification button is pressed.
     *
     * @param view The button clicked
     */
    public void onBunqVerificationClick(final View view) {
        LOGGER.info("Bunq verification button was pressed");
    }
}