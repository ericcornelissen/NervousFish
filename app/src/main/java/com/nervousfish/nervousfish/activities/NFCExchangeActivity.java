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
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.nervousfish.nervousfish.ConstantKeywords;
import com.nervousfish.nervousfish.R;
import com.nervousfish.nervousfish.data_objects.Contact;
import com.nervousfish.nervousfish.data_objects.Ed25519KeyPair;
import com.nervousfish.nervousfish.data_objects.Profile;
import com.nervousfish.nervousfish.data_objects.RSAKeyPair;
import com.nervousfish.nervousfish.modules.database.IDatabase;
import com.nervousfish.nervousfish.modules.pairing.INfcHandler;
import com.nervousfish.nervousfish.modules.pairing.events.NewDataReceivedEvent;
import com.nervousfish.nervousfish.service_locator.IServiceLocator;
import com.nervousfish.nervousfish.service_locator.NervousFish;

import org.apache.commons.lang3.Validate;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static android.nfc.NdefRecord.createMime;

/**
 * An {@link Activity} that beams NDEF Messages to Other Devices.
 */
public final class NFCExchangeActivity extends Activity implements NfcAdapter.CreateNdefMessageCallback {

    private static final Logger LOGGER = LoggerFactory.getLogger("NFCExchangeActivity");
    private IServiceLocator serviceLocator;
    private byte[] bytes;
    private NfcAdapter nfcAdapter;

    /**
     * {@inheritDoc}
     */
    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_nfc);
        this.serviceLocator = NervousFish.getServiceLocator();

        // Check for available NFC Adapter
        this.nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        LOGGER.info("Start creating an NDEF message to beam");
        Glide.with(this).load(R.drawable.s_contact_animado).into((ImageView) this.findViewById(R.id.nfc_gif));

        final IDatabase database = this.serviceLocator.getDatabase();
        try {
            final Profile profile = database.getProfile();

            LOGGER.info("Sending my profile with name: {}", profile.getName());
            final INfcHandler nfcHandler = this.serviceLocator.getNFCHandler();
            this.bytes = nfcHandler.objectToBytes(profile.getContact());
        } catch (final IOException e) {
            LOGGER.error("Could not serialize my contact to other device ", e);
        }

        // Register callback
        this.nfcAdapter.setNdefPushMessageCallback(this, this);
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
        LOGGER.info("Internal android ndef msg making");
        return new NdefMessage(
                new NdefRecord[]{createMime(
                        "text/plain", this.bytes)
                });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onResume() {
        super.onResume();
        LOGGER.info("NFC onResume");

        final PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, this.getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        this.nfcAdapter.enableForegroundDispatch(this, pendingIntent, null, null);
      
        // Check to see that the Activity started due to an Android Beam
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(this.getIntent().getAction())) {
            this.processIntent(this.getIntent());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onNewIntent(final Intent intent) {
        // onResume gets called after this to handle the intent
        this.processIntent(intent);
    }

    /**
     * Parses the NDEF Message from the intent and prints to the TextView
     * @param intent intent received by this {@link Activity}
     */
    void processIntent(final Intent intent) {
        LOGGER.info("Started processing intent");
        final Parcelable[] rawMsgs = intent.getParcelableArrayExtra(
                NfcAdapter.EXTRA_NDEF_MESSAGES);
        // only one message sent during the beam
        final NdefMessage msg = (NdefMessage) rawMsgs[0];
        // record 0 contains the MIME type, record 1 is the AAR, if present
        this.serviceLocator.getNFCHandler().dataReceived(msg.getRecords()[0].getPayload());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onStart() {
        super.onStart();
        this.serviceLocator.registerToEventBus(this);

        LOGGER.info("Activity started");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onStop() {
        this.serviceLocator.unregisterFromEventBus(this);
        LOGGER.info("Stopped NFCExchangeActivity");
        super.onStop();
    }

    /**
     * Called when a new data is received.
     *
     * @param event Contains additional data about the event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNewDataReceivedEvent(final NewDataReceivedEvent event) {
        LOGGER.info("onNewDataReceivedEvent called");
        Validate.notNull(event);
        if (event.getClazz().equals(Contact.class)) {
            final Contact contact = (Contact) event.getData();
            this.goToMainActivity(contact);
        }
    }

    /**
     * Evaluate the data received for Bluetooth.
     */
    private void goToMainActivity(final Contact contact) {
        LOGGER.info("Going to MainActivity");
        final Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(ConstantKeywords.CONTACT, contact);
        this.startActivity(intent);
    }

}