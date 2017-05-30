package com.nervousfish.nervousfish.activities;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.nervousfish.nervousfish.ConstantKeywords;
import com.nervousfish.nervousfish.R;
import com.nervousfish.nervousfish.modules.pairing.events.NewDataReceivedEvent;
import com.nervousfish.nervousfish.service_locator.IServiceLocator;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Used to let the Bluetooth-initiating user know that he should wait for his partner
 * to complete the pairing session.
 */
public final class WaitActivity extends Activity {
    private static final Logger LOGGER = LoggerFactory.getLogger("WaitActivity");
    private IServiceLocator serviceLocator;

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wait);

        final Intent intent = getIntent();
        this.serviceLocator = (IServiceLocator) intent.getSerializableExtra(ConstantKeywords.SERVICE_LOCATOR);

        final String message = (String) intent.getSerializableExtra(ConstantKeywords.WAIT_MESSAGE);
        final TextView waitingMessage = (TextView) findViewById(R.id.waiting_message);
        waitingMessage.setText(message);

        LOGGER.info("WaitActivity created");
    }

    /**
     * Can be called by a button to cancel the pairing
     * @param view The view that called this method
     */
    public void cancelWaiting(final View view) {
        finish();
    }

    /**
     * Called when a new data is received.
     *
     * @param event Contains additional data about the event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNewDataReceivedEvent(final NewDataReceivedEvent event) {
        LOGGER.info("onNewDataReceivedEvent called");
        if (event.getClazz().equals(String.class)) {
            final String verificationMessage = (String) event.getData();

            if (verificationMessage.equals("rhythm")) {
                //Go to RhythmActivity
                final Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra(ConstantKeywords.SERVICE_LOCATOR, serviceLocator);
                startActivity(intent);
            } else if (verificationMessage.equals("visual")) {
                //Go to VisualVerificationActivity
                final Intent intent = new Intent(this, VisualVerificationActivity.class);
                intent.putExtra(ConstantKeywords.SERVICE_LOCATOR, serviceLocator);
                startActivity(intent);
            }
        }
    }
}
