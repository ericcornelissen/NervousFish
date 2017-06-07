package com.nervousfish.nervousfish.service_locator;

import android.app.Activity;
import android.os.Bundle;

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

}

