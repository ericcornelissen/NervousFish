package com.nervousfish.nervousfish.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.nervousfish.nervousfish.R;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An {@link Activity} that is used to let the Bluetooth-initiating user know that he should wait for his partner
 * to complete the pairing session.
 */
public final class WaitForSlaveActivity extends Activity {

    private static final Logger LOGGER = LoggerFactory.getLogger("LoginActivity");

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.wait_for_slave);

        LOGGER.info("WaitForSlaveActivity created");
    }

    /**
     * Can be called by a button to cancel the pairing.
     *
     * @param view The view that called this method.
     */
    public void cancelWaitingForSlave(final View view) {
        final Intent intent = new Intent(this, MainActivity.class);
        this.startActivity(intent);
    }

}
