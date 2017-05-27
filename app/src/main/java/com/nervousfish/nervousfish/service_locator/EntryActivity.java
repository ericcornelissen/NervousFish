package com.nervousfish.nervousfish.service_locator;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.nervousfish.nervousfish.ConstantKeywords;
import com.nervousfish.nervousfish.activities.FirstUseActivity;
import com.nervousfish.nervousfish.activities.LoginActivity;
import com.nervousfish.nervousfish.data_objects.Profile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * The main activity class that shows a list of all people with their public keys
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

        final String androidFileDir = this.getFilesDir().getPath();
        final IServiceLocator serviceLocator = new ServiceLocator(androidFileDir);

        LOGGER.info("EntryActivity created");

        List<Profile> profiles = new ArrayList<>();
        try {
            profiles = serviceLocator.getDatabase().getProfiles();
        } catch (IOException e) {
            LOGGER.error("IOException while getting profiles", e);
        }
        if (profiles.isEmpty()) {
            final Intent intent = new Intent(this, FirstUseActivity.class);
            intent.putExtra(ConstantKeywords.SERVICE_LOCATOR, serviceLocator);
            this.startActivity(intent);
        } else {
            final Intent intent = new Intent(this, LoginActivity.class);
            intent.putExtra(ConstantKeywords.SERVICE_LOCATOR, serviceLocator);
            this.startActivity(intent);
        }
    }
}
