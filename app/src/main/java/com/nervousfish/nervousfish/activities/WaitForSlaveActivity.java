package com.nervousfish.nervousfish.activities;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.nervousfish.nervousfish.ConstantKeywords;
import com.nervousfish.nervousfish.R;
import com.nervousfish.nervousfish.service_locator.IServiceLocator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Used to let the Bluetooth-initiating user know that he should wait for his partner
 * to complete the pairing session.
 */
public final class WaitForSlaveActivity extends Activity {
    private static final Logger LOGGER = LoggerFactory.getLogger("LoginActivity");
    private IServiceLocator serviceLocator;

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Intent intent = getIntent();
        this.serviceLocator = (IServiceLocator) intent.getSerializableExtra(ConstantKeywords.SERVICE_LOCATOR);
        setContentView(R.layout.wait_for_slave);
        LOGGER.info("WaitForSlaveActivity created");
    }

    public void cancelWaitingForSlave(final View view) {
        final Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(ConstantKeywords.SERVICE_LOCATOR, serviceLocator);
        startActivity(intent);
    }
}
