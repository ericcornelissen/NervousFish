package com.nervousfish.nervousfish.service_locator;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.nervousfish.nervousfish.activities.LoginActivity;
import com.nervousfish.nervousfish.activities.WelcomeActivity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simple initial {@link Activity}. The {@link NervousFish} application will decide on what actual
 * {@link Activity} will be presented to the user.
 */
public final class EntryActivity extends Activity {

    private static final Logger LOGGER = LoggerFactory.getLogger("EntryActivity");

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LOGGER.info("activity created");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onResume() {
        super.onResume();

        final IServiceLocator serviceLocator = NervousFish.getServiceLocator();
        final Intent intent;

        if (serviceLocator.getDatabase().checkFirstUse()) {
            intent = new Intent(this, LoginActivity.class);
        } else {
            intent = new Intent(this, WelcomeActivity.class);
        }

        this.startActivity(intent);
    }

}
