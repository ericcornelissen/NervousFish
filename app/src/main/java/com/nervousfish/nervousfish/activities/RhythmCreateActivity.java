package com.nervousfish.nervousfish.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import com.nervousfish.nervousfish.ConstantKeywords;
import com.nervousfish.nervousfish.R;
import com.nervousfish.nervousfish.data_objects.Contact;
import com.nervousfish.nervousfish.data_objects.KeyPair;
import com.nervousfish.nervousfish.data_objects.Profile;
import com.nervousfish.nervousfish.data_objects.Ed25519Key;
import com.nervousfish.nervousfish.data_objects.tap.SingleTap;
import com.nervousfish.nervousfish.modules.pairing.events.NewDataReceivedEvent;
import com.nervousfish.nervousfish.service_locator.IServiceLocator;
import com.nervousfish.nervousfish.service_locator.NervousFish;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * The RhythmCreateActivity is an Activity where you can tap a sequence.
 */
@SuppressFBWarnings(value = "BC_BAD_CAST_TO_CONCRETE_COLLECTION")
//List is cast to an ArrayList, but that is needed to put in an intent.
@SuppressWarnings("PMD.LooseCoupling")
//We don't want to use 'List' but the implementation 'ArrayList' to prevent errors.
public final class RhythmCreateActivity extends AppCompatActivity {

    private static final Logger LOGGER = LoggerFactory.getLogger("RhythmCreateActivity");

    private Button startButton;
    private Button stopButton;
    private Button doneButton;
    private ArrayList<SingleTap> tapCombination;
    private IServiceLocator serviceLocator;
    private Contact dataReceived;

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_rhythm_create);
        this.serviceLocator = NervousFish.getServiceLocator();

        final Toolbar toolbar = (Toolbar) this.findViewById(R.id.toolbar_create_rhythm);
        this.setSupportActionBar(toolbar);

        this.startButton = (Button) this.findViewById(R.id.start_recording_button);
        this.stopButton = (Button) this.findViewById(R.id.stop_recording_button);
        this.doneButton = (Button) this.findViewById(R.id.done_tapping_button);

        LOGGER.info("Activity created");
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
    protected void onStop() {
        super.onStop();
        this.serviceLocator.unregisterFromEventBus(this);

        LOGGER.info("Activity stopped");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == ConstantKeywords.DONE_PAIRING_RESULT_CODE) {
            this.setResult(ConstantKeywords.DONE_PAIRING_RESULT_CODE);
            this.finish();
        } else if (resultCode == ConstantKeywords.CANCEL_PAIRING_RESULT_CODE) {
            this.setResult(ConstantKeywords.CANCEL_PAIRING_RESULT_CODE);
            this.finish();
        }
    }

    /**
     * Gets triggered when the tap screen is clicked.
     *
     * @param v - the {@link View} clicked
     */
    public void onTapClick(final View v) {
        LOGGER.info("Tapped");
        if (tapCombination != null && startButton.getVisibility() == View.GONE) {
            tapCombination.add(new SingleTap(new Timestamp(System.currentTimeMillis())));
        }
    }

    /**
     * Gets triggered when the done button is clicked.
     *
     * @param v - the {@link View} clicked
     */
    public void onDoneCreatingRhythmClick(final View v) {
        LOGGER.info("Done tapping button clicked");
        try {
            final Profile profile = this.serviceLocator.getDatabase().getProfile();
            final KeyPair keyPair = profile.getKeyPairs().get(0);

            LOGGER.info("Sending my profile with name: " + profile.getName() + ", public key: "
                    + keyPair.getPublicKey().toString());

            final Contact myProfileAsContact = new Contact(profile.getName(), new Ed25519Key("Ed25519 key", "73890ien"));
            this.serviceLocator.getBluetoothHandler().send(myProfileAsContact);
        } catch (IOException e) {
            LOGGER.error("Could not send my contact to other device " + e.getMessage());
        }
        final Intent intent = new Intent(this, WaitActivity.class);
        intent.putExtra(ConstantKeywords.WAIT_MESSAGE, this.getString(R.string.wait_message_partner_rhythm_tapping));
        intent.putExtra(ConstantKeywords.DATA_RECEIVED, dataReceived);
        intent.putExtra(ConstantKeywords.TAP_DATA, (ArrayList) tapCombination);
        this.startActivityForResult(intent, ConstantKeywords.START_RHYTHM_REQUEST_CODE);
    }

    /**
     * Gets triggered when the start recording button is clicked.
     *
     * @param v - the {@link View} clicked
     */
    public void onStartRecordingClick(final View v) {
        LOGGER.info("Start Recording clicked");
        tapCombination = new ArrayList<>();
        startButton.setVisibility(View.GONE);
        stopButton.setVisibility(View.VISIBLE);
        doneButton.setVisibility(View.GONE);
    }

    /**
     * Gets triggered when the stop recording button is clicked.
     *
     * @param v - the {@link View} clicked
     */
    public void onStopRecordingClick(final View v) {
        LOGGER.info("Stop Recording clicked");
        startButton.setVisibility(View.VISIBLE);
        stopButton.setVisibility(View.GONE);
        doneButton.setVisibility(View.VISIBLE);
    }

    /**
     * Called when a new data is received.
     *
     * @param event Contains additional data about the event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNewDataReceivedEvent(final NewDataReceivedEvent event) {
        LOGGER.info("onNewDataReceivedEvent called");
        if (event.getClazz().equals(Contact.class)) {
            final Contact contact = (Contact) event.getData();
            try {
                LOGGER.info("Adding contact to database...");
                this.serviceLocator.getDatabase().addContact(contact);
            } catch (IOException | IllegalArgumentException e) {
                LOGGER.error("Couldn't get contacts from database", e);
            }

            //This needs to be outside of the try catch block
            dataReceived = contact;
        }
    }

}
