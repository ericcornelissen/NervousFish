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
 * The main activity class that shows a list of all people with their public keys
 */
public final class CreateProfileActivity extends AppCompatActivity {

    private static final Logger LOGGER = LoggerFactory.getLogger("CreateProfileActivity");

    private IServiceLocator serviceLocator;

    /**
     * Creates the new activity, should only be called by Android
     *
     * @param savedInstanceState Don't touch this
     */
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Intent intent = getIntent();
        this.serviceLocator = (IServiceLocator) intent.getSerializableExtra(ConstantKeywords.SERVICE_LOCATOR);
        this.setContentView(R.layout.activity_create_profile);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }


        LOGGER.info("FirstUseActivity created");
    }

    /**
     * Gets triggered when the Submit button is clicked.
     *
     * @param v - the {@link View} clicked
     */
    public void onSubmitClick(final View v) {
        final Intent intent = new Intent(this, LoginActivity.class);
        intent.putExtra(ConstantKeywords.SERVICE_LOCATOR, serviceLocator);
        this.startActivity(intent);
    }
}
