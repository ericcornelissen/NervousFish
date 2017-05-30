package com.nervousfish.nervousfish.activities;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.nervousfish.nervousfish.ConstantKeywords;
import com.nervousfish.nervousfish.R;
import com.nervousfish.nervousfish.data_objects.Contact;
import com.nervousfish.nervousfish.data_objects.tap.SingleTap;
import com.nervousfish.nervousfish.modules.pairing.events.NewDataReceivedEvent;
import com.nervousfish.nervousfish.service_locator.IServiceLocator;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Used to let the Bluetooth-initiating user know that he should wait for his partner
 * to complete the pairing session.
 */
public final class WaitActivity extends Activity {
    private static final Logger LOGGER = LoggerFactory.getLogger("WaitActivity");
    private IServiceLocator serviceLocator;
    private Contact dataReceived;
    private ArrayList<SingleTap> tapCombination;

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wait);

        final Intent intent = getIntent();
        this.serviceLocator = (IServiceLocator) intent.getSerializableExtra(ConstantKeywords.SERVICE_LOCATOR);

        this.dataReceived = (Contact) intent.getSerializableExtra(ConstantKeywords.DATA_RECEIVED);
        this.tapCombination = (ArrayList<SingleTap>) intent.getSerializableExtra(ConstantKeywords.TAP_DATA);

        if(dataReceived != null && tapCombination != null) {
            evaluateData();
        }

        final String message = (String) intent.getSerializableExtra(ConstantKeywords.WAIT_MESSAGE);
        final TextView waitingMessage = (TextView) findViewById(R.id.waiting_message);
        waitingMessage.setText(message);

        LOGGER.info("WaitActivity created");
    }

    private void evaluateData() {
        //TODO: check if when we decrypt the dataReceived with the tapCombination that we get a normal contact
        setResult(ConstantKeywords.DONE_RESULT_CODE);
        finish();
    }

    /**
     * Can be called by a button to cancel the pairing
     * @param view The view that called this method
     */
    public void cancelWaiting(final View view) {
        setResult(ConstantKeywords.DONE_RESULT_CODE);
        finish();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == ConstantKeywords.DONE_RESULT_CODE) {
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
        LOGGER.info("onNewDataReceivedEvent called, type is " + event.getClazz());
        if (event.getClazz().equals(String.class)) {
            final String verificationMessage = (String) event.getData();

            if (verificationMessage.equals("rhythm")) {
                //Go to RhythmActivity
                final Intent intent = new Intent(this, RhythmCreateActivity.class);
                intent.putExtra(ConstantKeywords.SERVICE_LOCATOR, serviceLocator);
                startActivity(intent);
            } else if (verificationMessage.equals("visual")) {
                //Go to VisualVerificationActivity
                final Intent intent = new Intent(this, VisualVerificationActivity.class);
                intent.putExtra(ConstantKeywords.SERVICE_LOCATOR, serviceLocator);
                startActivity(intent);
            }
        } else if (event.getClazz().equals(Contact.class)) {
            final Contact contact = (Contact) event.getData();
            try {
                LOGGER.info("Adding contact to database...");
                this.serviceLocator.getDatabase().addContact(contact);
                evaluateData();
            } catch (IOException e) {
                LOGGER.error("Couldn't get contacts from database", e);
            }
        }
    }
}
