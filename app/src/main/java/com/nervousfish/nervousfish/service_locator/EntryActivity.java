package com.nervousfish.nervousfish.service_locator;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;

import com.nervousfish.nervousfish.activities.LoginActivity;
import com.nervousfish.nervousfish.activities.WelcomeActivity;
import com.nervousfish.nervousfish.data_objects.Profile;
import com.nervousfish.nervousfish.modules.database.IDatabase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
        final List<Profile> profiles = new ArrayList<>();
        try {
            final IDatabase database = serviceLocator.getDatabase();
            profiles.addAll(database.getProfiles());
        } catch (IOException e) {
            LOGGER.error("IOException while getting profiles", e);
        }

        final Intent intent = new Intent();
        if (profiles.isEmpty()) {
            intent.setComponent(new ComponentName(this, WelcomeActivity.class));
        } else {
            intent.setComponent(new ComponentName(this, LoginActivity.class));
        }

        this.startActivity(intent);
    }

}
