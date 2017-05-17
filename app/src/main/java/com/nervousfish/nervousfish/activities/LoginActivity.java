package com.nervousfish.nervousfish.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.nervousfish.nervousfish.ConstantKeywords;
import com.nervousfish.nervousfish.R;
import com.nervousfish.nervousfish.service_locator.IServiceLocator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simple login activity class.
 */
public final class LoginActivity extends Activity {

    private static final Logger LOGGER = LoggerFactory.getLogger("LoginActivity");

    private IServiceLocator serviceLocator;

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.login);

        final Intent intent = getIntent();
        this.serviceLocator = (IServiceLocator) intent.getSerializableExtra(ConstantKeywords.SERVICE_LOCATOR);

        LOGGER.info("LoginActivity created");
    }

    /**
     * Validate a login attempt.
     */
    public void validateLoginAttempt(final View v) {
        LOGGER.info("Submit button clicked");

        final View mError = findViewById(R.id.error);
        final EditText passwordInput = (EditText) findViewById(R.id.login_password_input);
        if (passwordInput.getError() != null) {
            mError.setVisibility(View.VISIBLE);
        }

        final boolean skipPassword = passwordInput.getText().toString().isEmpty();
        if (skipPassword) {
            LOGGER.warn("Password skipped!");
            mError.setVisibility(View.GONE);

            this.nextActivity();
        } else {
            final String providedPassword = passwordInput.getText().toString();
            final boolean wrongPassword = !providedPassword.equals("12345");
            if (wrongPassword) {
                LOGGER.warn("Password incorrect!");
                mError.setVisibility(View.VISIBLE);
            } else {
                LOGGER.info("Password correct");
                mError.setVisibility(View.GONE);

                this.nextActivity();
            }
        }
    }

    /**
     * Go to the next activity from the {@link LoginActivity}.
     */
    private void nextActivity() {
        final Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.putExtra(ConstantKeywords.SERVICE_LOCATOR, this.serviceLocator);
        startActivity(intent);
    }

}
