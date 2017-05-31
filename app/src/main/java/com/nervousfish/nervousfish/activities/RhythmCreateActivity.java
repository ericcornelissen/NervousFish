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
import com.nervousfish.nervousfish.data_objects.SimpleKey;
import com.nervousfish.nervousfish.data_objects.tap.SingleTap;
import com.nervousfish.nervousfish.modules.pairing.events.NewDataReceivedEvent;
import com.nervousfish.nervousfish.service_locator.IServiceLocator;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RhythmCreateActivity extends AppCompatActivity {
    private static final Logger LOGGER = LoggerFactory.getLogger("RhythmCreateActivity");
    private ArrayList<SingleTap> tapCombination;
    private IServiceLocator serviceLocator;
    //TODO: change contact into an encrypted string of bytes
    private Contact dataReceived = null;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rhythm_create);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_create_rhythm);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        final Intent intent = this.getIntent();
        this.serviceLocator = (IServiceLocator) intent.getSerializableExtra(ConstantKeywords.SERVICE_LOCATOR);
    }

    /**
     * Gets triggered when the tap screen is clicked.
     *
     * @param v - the {@link View} clicked
     */
    public void onTapClick(final View v) {
        LOGGER.info("Tapped");
        final Button startButton = (Button) this.findViewById(R.id.start_recording_button);
        if (tapCombination != null && startButton.getVisibility() == View.GONE) {
            tapCombination.add(new SingleTap(new Timestamp(System.currentTimeMillis())));
        }
    }

    /**
     * Gets triggered when the done button is clicked.
     *
     * @param v - the {@link View} clicked
     */
    public void onDoneClick(final View v) {
        LOGGER.info("Done tapping button clicked");
        try {
            this.serviceLocator.getBluetoothHandler().send(new Contact("Max", new SimpleKey("testkey", "456um4h692406u2p")));
        } catch (IOException e) {
            LOGGER.error("Could not send my contact to other device " + e.getMessage());
        }
        final Intent intent = new Intent(this, WaitActivity.class);
        intent.putExtra(ConstantKeywords.SERVICE_LOCATOR, serviceLocator);
        intent.putExtra(ConstantKeywords.WAIT_MESSAGE, this.getString(R.string.wait_message_partner_rhythm_tapping));
        intent.putExtra(ConstantKeywords.DATA_RECEIVED, dataReceived);
        intent.putExtra(ConstantKeywords.TAP_DATA, tapCombination);
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

        final Button startButton = (Button) this.findViewById(R.id.start_recording_button);
        startButton.setVisibility(View.GONE);

        final Button stopButton = (Button) this.findViewById(R.id.stop_recording_button);
        stopButton.setVisibility(View.VISIBLE);

        final Button doneButton = (Button) this.findViewById(R.id.done_tapping_button);
        doneButton.setVisibility(View.GONE);
    }

    /**
     * Gets triggered when the stop recording button is clicked.
     *
     * @param v - the {@link View} clicked
     */
    public void onStopRecordingClick(final View v) {
        LOGGER.info("Stop Recording clicked");
        final Button startButton = (Button) this.findViewById(R.id.start_recording_button);
        startButton.setVisibility(View.VISIBLE);

        final Button stopButton = (Button) this.findViewById(R.id.stop_recording_button);
        stopButton.setVisibility(View.GONE);

        final Button doneButton = (Button) this.findViewById(R.id.done_tapping_button);
        doneButton.setVisibility(View.VISIBLE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == ConstantKeywords.DONE_RESULT_CODE) {
            setResult(ConstantKeywords.DONE_RESULT_CODE);
            finish();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        this.serviceLocator.registerToEventBus(this);
    }

    @Override
    protected void onStop() {
        this.serviceLocator.unregisterFromEventBus(this);
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
        if (event.getClazz().equals(Contact.class)) {
            final Contact contact = (Contact) event.getData();
            try {
                LOGGER.info("Adding contact to database...");
                this.serviceLocator.getDatabase().addContact(contact);
                dataReceived = contact;
            } catch (IOException e) {
                LOGGER.error("Couldn't get contacts from database", e);
            }
        }
    }
}
