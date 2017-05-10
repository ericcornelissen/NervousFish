package com.nervousfish.nervousfish.service_locator;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.nervousfish.nervousfish.ConstantKeywords;
import com.nervousfish.nervousfish.activities.LoginActivity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The main activity class that shows a list of all people with their public keys
 */
@SuppressWarnings("PMD.AtLeastOneConstructor")
public final class EntryActivity extends Activity {
    private static final Logger LOGGER = LoggerFactory.getLogger("EntryActivity");

    /**
     * Creates the new activity, should only be called by Android
     *
     * @param savedInstanceState Don't touch this
     */
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LOGGER.info("EntryActivity entered");
        final IServiceLocator serviceLocator = ServiceLocatorCreator.createInstance(
                getFilesDir().getPath());

        final Intent intent = new Intent(this, LoginActivity.class);
        intent.putExtra(ConstantKeywords.SERVICE_LOCATOR, serviceLocator);
        startActivity(intent);
    }
}
