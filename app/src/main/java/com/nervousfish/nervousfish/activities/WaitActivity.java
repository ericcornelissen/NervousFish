package com.nervousfish.nervousfish.activities;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.nervousfish.nervousfish.ConstantKeywords;
import com.nervousfish.nervousfish.R;
import com.nervousfish.nervousfish.data_objects.Contact;
import com.nervousfish.nervousfish.modules.pairing.events.NewDataReceivedEvent;
import com.nervousfish.nervousfish.service_locator.IServiceLocator;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Used to let the Bluetooth-initiating user know that he should wait for his partner
 * to complete the pairing session.
 */
public final class WaitActivity extends Activity {
    private static final Logger LOGGER = LoggerFactory.getLogger("WaitActivity");
    private IServiceLocator serviceLocator;
    private Object dataReceived;
    private Object tapCombination;

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_wait);

        final Intent intent = getIntent();
        this.serviceLocator = (IServiceLocator) intent.getSerializableExtra(ConstantKeywords.SERVICE_LOCATOR);

        this.serviceLocator.registerToEventBus(this);

        this.dataReceived = intent.getSerializableExtra(ConstantKeywords.DATA_RECEIVED);
        tapCombination = intent.getSerializableExtra(ConstantKeywords.TAP_DATA);

        LOGGER.info("dataReceived is not null: " + (this.dataReceived != null)
                + " tapCombination is not null: " + (tapCombination != null));

        final String message = (String) intent.getSerializableExtra(ConstantKeywords.WAIT_MESSAGE);
        final TextView waitingMessage = (TextView) findViewById(R.id.waiting_message);
        waitingMessage.setText(message);

        LOGGER.info("WaitActivity created");
    }

    private void evaluateData() {
        //TODO: check if when we decrypt the dataReceived with the tapCombination that we get a normal contact
        LOGGER.info("Evaluating data");
        final Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(ConstantKeywords.SERVICE_LOCATOR, serviceLocator);
        intent.putExtra(ConstantKeywords.SUCCESSFUL_BLUETOOTH, true);
        this.startActivity(intent);
        //setResult(ConstantKeywords.DONE_PAIRING_RESULT_CODE);
        //finish();

    }

    /**
     * Can be called by a button to cancel the pairing
     * @param view The view that called this method
     */
    public void cancelWaiting(final View view) {
        setResult(ConstantKeywords.CANCEL_PAIRING_RESULT_CODE);
        finish();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == ConstantKeywords.DONE_PAIRING_RESULT_CODE) {
            this.setResult(ConstantKeywords.DONE_PAIRING_RESULT_CODE);
            finish();
        } else if (resultCode == ConstantKeywords.CANCEL_PAIRING_RESULT_CODE) {
            this.setResult(ConstantKeywords.CANCEL_PAIRING_RESULT_CODE);
            finish();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onStart() {
        super.onStart();
        if (this.dataReceived != null && tapCombination != null) {
            evaluateData();
        }
    }

    /**
     * {@inheritDoc}
     */
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
        LOGGER.info("onNewDataReceivedEvent called, type is " + event.getClazz());
        if (event.getClazz().equals(String.class)) {
            final String verificationMessage = (String) event.getData();

            if ("rhythm".equals(verificationMessage)) {
                //Go to RhythmActivity
                final Intent intent = new Intent(this, RhythmCreateActivity.class);
                intent.putExtra(ConstantKeywords.SERVICE_LOCATOR, serviceLocator);
                this.startActivityForResult(intent, 0);
            } else if ("visual".equals(verificationMessage)) {
                //Go to VisualVerificationActivity
                final Intent intent = new Intent(this, VisualVerificationActivity.class);
                intent.putExtra(ConstantKeywords.SERVICE_LOCATOR, serviceLocator);
                this.startActivityForResult(intent, 0);
            }
        } else if (event.getClazz().equals(Contact.class)) {
            final Contact contact = (Contact) event.getData();
            try {
                LOGGER.info("Adding contact to database...");
                this.serviceLocator.getDatabase().addContact(contact);
            } catch (IOException | IllegalArgumentException e) {
                LOGGER.error("Couldn't get contacts from database", e);
            }

            //This needs to be outside of the try catch block
            this.dataReceived = contact;
            evaluateData();
        }
    }
}
