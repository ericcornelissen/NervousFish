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
 * Demo Login activity class. All warnings are suppressed because this is a demo class that can be deleted
 * after we're comfortable writing Cucumber tests.
 */
@SuppressWarnings("PMD")
public final class LoginActivity extends Activity {
    private static final Logger LOGGER = LoggerFactory.getLogger("LoginActivity");
    private EditText mPassword;
    private View mError;
    private IServiceLocator serviceLocator;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        LOGGER.info("LoginActivity created");

        final Intent intent = getIntent();
        this.serviceLocator = (IServiceLocator) intent.getSerializableExtra(ConstantKeywords.SERVICE_LOCATOR);

        mPassword = (EditText) findViewById(R.id.password);

        final View submitButton = findViewById(R.id.submit);
        submitButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(final View v) {
                LOGGER.info("Submit button clicked");
                if (mPassword.getError() == null) {
                    validateAccount();
                }
            }
        });
    }

    private void validateAccount() {
        if (mError == null) {
            mError = findViewById(R.id.error);
        }

        // TODO: have an error when empty, don't accept it
        final boolean skipPassword = mPassword.getText().toString().isEmpty();
        if (skipPassword) {
            LOGGER.warn("Password skipped!");
            mError.setVisibility(View.GONE);
            final Intent intent = new Intent(LoginActivity.this, VisualVerificationActivity.class);
            intent.putExtra(ConstantKeywords.SERVICE_LOCATOR, this.serviceLocator);
            startActivity(intent);
        } else {
            final String dummyPass = "12345";
            final boolean wrongPassword = !mPassword.getText().toString().equals(dummyPass);
            if (wrongPassword) {
                LOGGER.warn("Password incorrect!");
                mError.setVisibility(View.VISIBLE);
            } else {
                LOGGER.info("Password correct");
                mError.setVisibility(View.GONE);
                final Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.putExtra(ConstantKeywords.SERVICE_LOCATOR, this.serviceLocator);
                startActivity(intent);
            }
        }
    }
}
