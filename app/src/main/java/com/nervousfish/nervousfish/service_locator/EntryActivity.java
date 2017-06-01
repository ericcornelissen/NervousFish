package com.nervousfish.nervousfish.service_locator;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.nervousfish.nervousfish.ConstantKeywords;
import com.nervousfish.nervousfish.activities.LoginActivity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The activity that's entered as soon as the app is started. This app initializes the basis components
 * of the app like the service locator and background services.
 */
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

        LOGGER.info("EntryActivity created");

        final String androidFileDir = getFilesDir().getPath();
        final IServiceLocator serviceLocator = new ServiceLocator(androidFileDir);

        ((INervousFish) getApplicationContext()).setOnBluetoothServiceBound(new Runnable() {
            @Override
            public void run() {
                ((NervousFish) getApplicationContext()).getBluetoothServiceWithinPackage().setServiceLocator(serviceLocator);

                final Intent intent = new Intent(EntryActivity.this, LoginActivity.class);
                intent.putExtra(ConstantKeywords.SERVICE_LOCATOR, serviceLocator);
                startActivity(intent);
            }
        });
    }
}
