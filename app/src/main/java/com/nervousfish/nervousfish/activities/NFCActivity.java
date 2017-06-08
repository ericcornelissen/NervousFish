package com.nervousfish.nervousfish.activities;

import android.app.Activity;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcAdapter.CreateNdefMessageCallback;
import android.nfc.NfcEvent;
import android.os.Bundle;
import android.os.Parcelable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.nervousfish.nervousfish.ConstantKeywords;
import com.nervousfish.nervousfish.R;
import com.nervousfish.nervousfish.data_objects.Contact;
import com.nervousfish.nervousfish.data_objects.Profile;
import com.nervousfish.nervousfish.data_objects.SimpleKey;
import com.nervousfish.nervousfish.service_locator.IServiceLocator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;

import static android.nfc.NdefRecord.createExternal;

/**
 * An {@link Activity} that beams NDEF Messages to Other Devices.
 */
public class NFCActivity extends Activity implements CreateNdefMessageCallback {

    private static final Logger LOGGER = LoggerFactory.getLogger("NFCActivity");
    private TextView textView;
    private IServiceLocator serviceLocator;
    private byte[] bytes;

    /**
     * {@inheritDoc}
     */
    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfc);

        final Intent intent = getIntent();
        this.serviceLocator = (IServiceLocator) intent.getSerializableExtra(ConstantKeywords.SERVICE_LOCATOR);

        textView = (TextView) findViewById(R.id.textView);
        // Check for available NFC Adapter
        final NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (nfcAdapter == null) {
            Toast.makeText(this, "NFC is not available", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        LOGGER.info("Start creating an NDEF message to beam");
        Glide.with(this).load(R.drawable.s_contact_animado).into((ImageView) findViewById(R.id.gif));
        try {
            final Profile myProfile = this.serviceLocator.getDatabase().getProfiles().get(0);
            LOGGER.info("Sending my profile with name: " + myProfile.getName() + ", public key: "
                    + myProfile.getPublicKey().toString());
            final Contact contact = new Contact(myProfile.getName(), new SimpleKey("simplekey", "73890ien"));
            bytes = this.serviceLocator.getNFCHandler().objectToBytes(contact);
        } catch (IOException e) {
            LOGGER.error("Could not serialize my contact to other device " + e.getMessage());
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

        final NdefMessage msg = new NdefMessage(
                new NdefRecord[]{createExternal(
                        "com.nervousfish", "contact", bytes)
                });
        return msg;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onResume() {
        super.onResume();
        // Check to see that the Activity started due to an Android Beam
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {
            processIntent(getIntent());
        }
    }

    /**
     * {@inheritDoc}
     * @param intent NFC connection intent
     */
    @Override
    public void onNewIntent(final Intent intent) {
        // onResume gets called after this to handle the intent
        setIntent(intent);
    }

    /**
     * Parses the NDEF Message from the intent and prints to the TextView
     * @param intent intent received by this {@link Activity}
     */
    final void processIntent(final Intent intent) {
        LOGGER.info("Started processing intent");
        textView = (TextView) findViewById(R.id.textView);
        final Parcelable[] rawMsgs = intent.getParcelableArrayExtra(
                NfcAdapter.EXTRA_NDEF_MESSAGES);
        // only one message sent during the beam
        final NdefMessage msg = (NdefMessage) rawMsgs[0];
        // record 0 contains the MIME type, record 1 is the AAR, if present
        this.serviceLocator.getNFCHandler().dataReceived(msg.getRecords()[0].getPayload());
        textView.setText(Arrays.toString(msg.getRecords()[0].getPayload()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onStop() {
        super.onStop();

        this.serviceLocator.unregisterFromEventBus(this);
        LOGGER.info("Stopped NFCActivity");

        super.onStop();
    }
}