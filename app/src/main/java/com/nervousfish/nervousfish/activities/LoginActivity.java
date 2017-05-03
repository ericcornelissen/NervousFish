package com.nervousfish.nervousfish.activities;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.nervousfish.nervousfish.R;

/**
 * Demo Login activity class. All warnings are suppressed because this is a demo class that can be deleted
 * after we're comfortable writing Cucumber tests.
 */
@SuppressWarnings("PMD")
public final class LoginActivity extends Activity {

    private final String dummyPass = "12345";

    private EditText mPassword;
    private View mError;

    /**
     * Prevent instantiation from non-subclasses from outside the package.
     */
    protected LoginActivity() {
        super();
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.login);

        mPassword = (EditText) findViewById(R.id.password);

        final View submitButton = findViewById(R.id.submit);
        submitButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(final View v) {
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
            mError.setVisibility(View.GONE);
            final Intent k = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(k);
        } else {
            final boolean wrongPassword = !mPassword.getText().toString().equals(dummyPass);
            if (wrongPassword) {
                mError.setVisibility(View.VISIBLE);
            } else {
                mError.setVisibility(View.GONE);
                final Intent k = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(k);
            }
        }
    }
}
