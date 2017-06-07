package com.nervousfish.nervousfish.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.nervousfish.nervousfish.R;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The main activity class that shows a list of all people with their public keys.
 */
public final class FirstUseActivity extends AppCompatActivity {

    private static final Logger LOGGER = LoggerFactory.getLogger("FirstUseActivity");

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_first_use);
        LOGGER.info("activity created");
    }

    /**
     * Gets triggered when the GetStarted button is clicked.
     *
     * @param view The {@link View} being clicked.
     */
    public void onGetStartedClick(final View view) {
        final Intent intent = new Intent(this, CreateProfileActivity.class);
        this.startActivity(intent);
    }

}
