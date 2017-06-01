package com.nervousfish.nervousfish.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import com.nervousfish.nervousfish.ConstantKeywords;
import com.nervousfish.nervousfish.R;
import com.nervousfish.nervousfish.modules.database.IDatabase;
import com.nervousfish.nervousfish.service_locator.IServiceLocator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Draws the screen is used to login by entering a password
 */
public final class LoginActivity extends AppCompatActivity {

    private static final Logger LOGGER = LoggerFactory.getLogger("LoginActivity");
    private String actualPassword;
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

        final IDatabase database = this.serviceLocator.getDatabase();
        try {
            this.actualPassword = database.getUserPassword();
        } catch (final IOException e) {
            LOGGER.error("Failed to retrieve password from database", e);
        }

        LOGGER.info("LoginActivity created");
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
            final String providedPassword = passwordInput.getText().toString();
            final boolean wrongPassword = !providedPassword.equals(this.actualPassword);
            if (wrongPassword) {
                LOGGER.warn("Password incorrect!");
                mError.setVisibility(View.VISIBLE);
            } else {
                LOGGER.info("Password correct");
                mError.setVisibility(View.GONE);
                this.toMainActivity();
            }
        }
    }

    @Override
    public void onBackPressed() {
        final Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    /**
     * Go to the next activity from the {@link LoginActivity}.
     */
    private void toMainActivity() {
        final Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(ConstantKeywords.SERVICE_LOCATOR, this.serviceLocator);
        startActivity(intent);
    }
}
