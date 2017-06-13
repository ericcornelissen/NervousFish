package com.nervousfish.nervousfish.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import com.nervousfish.nervousfish.R;
import com.nervousfish.nervousfish.modules.database.IDatabase;
import com.nervousfish.nervousfish.service_locator.IServiceLocator;
import com.nervousfish.nervousfish.service_locator.NervousFish;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * An {@link Activity} that draws the screen that is used to login by entering a password.
 */
public final class LoginActivity extends AppCompatActivity {

    private static final Logger LOGGER = LoggerFactory.getLogger("LoginActivity");
    private IServiceLocator serviceLocator;


    /**
     * {@inheritDoc}
     */
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.login);

        this.serviceLocator = NervousFish.getServiceLocator();

        LOGGER.info("LoginActivity created");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onBackPressed() {
        final Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        this.startActivity(intent);
    }

    /**
     * Validate a login attempt.
     *
     * @param view The submit button that was clicked
     */
    public void validateLoginAttempt(final View view) {
        LOGGER.info("Submit button clicked");

        final View mError = findViewById(R.id.error_message_login);
        final EditText passwordInput = (EditText) findViewById(R.id.login_password_input);

        final boolean skipPassword = passwordInput.getText().toString().isEmpty();
        if (skipPassword) {
            LOGGER.warn("Password skipped!");
            mError.setVisibility(View.GONE);
            this.toMainActivity();
        } else {
            final IDatabase database = this.serviceLocator.getDatabase();
            final String providedPassword = passwordInput.getText().toString();

            try {
                database.loadDatabase(providedPassword);
                mError.setVisibility(View.GONE);
                this.toMainActivity();
            } catch (IOException e) {
                LOGGER.error("Something went wrong when loading the database", e);
                mError.setVisibility(View.VISIBLE);
            }


        }
    }

    /**
     * Go to the next activity from the {@link LoginActivity}.
     */
    private void toMainActivity() {
        final Intent intent = new Intent(this, MainActivity.class);
        this.startActivity(intent);
    }
}
