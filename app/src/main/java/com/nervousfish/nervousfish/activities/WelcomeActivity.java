package com.nervousfish.nervousfish.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.nervousfish.nervousfish.ConstantKeywords;
import com.nervousfish.nervousfish.R;
import com.nervousfish.nervousfish.service_locator.IServiceLocator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link Activity} that will show up when the app is first launched (i.e. there is no user
 * account available).
 */
public final class WelcomeActivity extends AppCompatActivity {

    private static final Logger LOGGER = LoggerFactory.getLogger("WelcomeActivity");

    private IServiceLocator serviceLocator;

    /**
     * Creates the new activity, should only be called by Android.
     *
     * @param savedInstanceState A previous state of this {@link Activity}.
     */
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_welcome);

        final Intent intent = this.getIntent();
        this.serviceLocator = (IServiceLocator) intent.getSerializableExtra(ConstantKeywords.SERVICE_LOCATOR);

        LOGGER.info("activity created");
    }

    /**
     * Gets triggered when the GetStarted button is clicked.
     *
     * @param view The {@link View} being clicked.
     */
    public void onGetStartedClick(final View view) {
        final Intent intent = new Intent(this, CreateProfileActivity.class);
        intent.putExtra(ConstantKeywords.SERVICE_LOCATOR, this.serviceLocator);
        this.startActivity(intent);
    }

}
