package com.nervousfish.nervousfish.activities;

import android.app.Activity;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.os.Bundle;
import android.os.Parcelable;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.nervousfish.nervousfish.ConstantKeywords;
import com.nervousfish.nervousfish.R;
import com.nervousfish.nervousfish.data_objects.Contact;
import com.nervousfish.nervousfish.data_objects.Profile;
import com.nervousfish.nervousfish.modules.database.IDatabase;
import com.nervousfish.nervousfish.modules.pairing.INfcHandler;
import com.nervousfish.nervousfish.service_locator.IServiceLocator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;

import static android.nfc.NdefRecord.createExternal;

/**
 * An {@link Activity} that beams NDEF Messages to Other Devices.
 */
public final class NFCActivity extends Activity implements NfcAdapter.CreateNdefMessageCallback {

    private static final Logger LOGGER = LoggerFactory.getLogger("NFCActivity");
    private TextView descriptionText;
    private IServiceLocator serviceLocator;
    private byte[] bytes;

    /**
     * {@inheritDoc}
     */
    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_nfc);

        final Intent intent = this.getIntent();
        this.serviceLocator = (IServiceLocator) intent.getSerializableExtra(ConstantKeywords.SERVICE_LOCATOR);
        final IDatabase database = this.serviceLocator.getDatabase();

        this.descriptionText = (TextView) this.findViewById(R.id.nfc_instruction);
        // Check for available NFC Adapter
        final NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        LOGGER.info("Start creating an NDEF message to beam");
        Glide.with(this).load(R.drawable.s_contact_animado).into((ImageView) this.findViewById(R.id.nfc_gif));
        try {
            final Profile myProfile = database.getProfiles().get(0);
            LOGGER.info("Sending my profile with name: {} , public key: {} ", myProfile.getName(),
                    myProfile.getPublicKey());
            final Contact contact = new Contact(myProfile.getName(), myProfile.getPublicKey());
            final INfcHandler nfcHandler = this.serviceLocator.getNFCHandler();
            this.bytes = nfcHandler.objectToBytes(contact);
        } catch (IOException e) {
            LOGGER.error("Could not serialize my contact to other device ", e);
        }
        // Register callback
        nfcAdapter.setNdefPushMessageCallback(this, this);
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
     * {@inheritDoc}
     */
    @Override
    public NdefMessage createNdefMessage(final NfcEvent event) {

        return new NdefMessage(
                new NdefRecord[]{createExternal(
                        "com.nervousfish.nervousfish", "contact", this.bytes)
                });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onResume() {
        super.onResume();
        // Check to see that the Activity started due to an Android Beam
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(this.getIntent().getAction())) {
            this.processIntent(this.getIntent());
        }
    }

    /**
     * {@inheritDoc}
     * @param intent NFC connection intent
     */
    @Override
    public void onNewIntent(final Intent intent) {
        // onResume gets called after this to handle the intent
        this.setIntent(intent);
    }

    /**
     * Parses the NDEF Message from the intent and prints to the TextView
     * @param intent intent received by this {@link Activity}
     */
    void processIntent(final Intent intent) {
        LOGGER.info("Started processing intent");
        this.descriptionText = (TextView) this.findViewById(R.id.textView);
        final Parcelable[] rawMsgs = intent.getParcelableArrayExtra(
                NfcAdapter.EXTRA_NDEF_MESSAGES);
        // only one message sent during the beam
        final NdefMessage msg = (NdefMessage) rawMsgs[0];
        // record 0 contains the MIME type, record 1 is the AAR, if present
        this.serviceLocator.getNFCHandler().dataReceived(msg.getRecords()[0].getPayload());
        this.descriptionText.setText(Arrays.toString(msg.getRecords()[0].getPayload()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onStop() {
        super.onStop();

        this.serviceLocator.unregisterFromEventBus(this);
        LOGGER.info("Stopped NFCActivity");
    }

}