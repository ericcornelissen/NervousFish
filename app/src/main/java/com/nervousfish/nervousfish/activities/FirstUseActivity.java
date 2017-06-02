package com.nervousfish.nervousfish.activities;

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
 * The main activity class that shows a list of all people with their public keys.
 */
public final class FirstUseActivity extends AppCompatActivity {

    private static final Logger LOGGER = LoggerFactory.getLogger("FirstUseActivity");

    private IServiceLocator serviceLocator;

    /**
     * Creates the new activity, should only be called by Android.
     *
     * @param savedInstanceState The saved state of the previous instance.
     */
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_first_use);

        final Intent intent = this.getIntent();
        this.serviceLocator = (IServiceLocator) intent.getSerializableExtra(ConstantKeywords.SERVICE_LOCATOR);

        LOGGER.info("FirstUseActivity created");
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
