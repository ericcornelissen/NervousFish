package com.nervousfish.nervousfish.service_locator;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.nervousfish.nervousfish.ConstantKeywords;
import com.nervousfish.nervousfish.activities.MainActivity;

/**
 * The main activity class that shows a list of all people with their public keys
 */
public class EntryActivity extends Activity {
    /**
     * Creates the new activity, should only be called by Android
     *
     * @param savedInstanceState Don't touch this
     */
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final IServiceLocator serviceLocator = ServiceLocatorCreator.newInstance();
        final Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(ConstantKeywords.SERVICE_LOCATOR, serviceLocator);
        startActivity(intent);
    }
}
