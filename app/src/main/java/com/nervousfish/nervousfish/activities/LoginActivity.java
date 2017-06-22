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

import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * An {@link Activity} that draws the screen that is used to login by entering a password.
 */
public final class LoginActivity extends AppCompatActivity {

    private static final Logger LOGGER = LoggerFactory.getLogger("LoginActivity");
    private IServiceLocator serviceLocator;
    private CustomKeyboardHelper customKeyboard;

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_login);
        this.serviceLocator = NervousFish.getServiceLocator();

        // Use a custom keyboard
        this.customKeyboard = new CustomKeyboardHelper(this);
        this.customKeyboard.addInput((EditText) this.findViewById(R.id.login_password_input));

        LOGGER.info("LoginActivity created");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onBackPressed() {
        if (this.customKeyboard.isVisible()) {
            this.customKeyboard.hide();
        } else {
            final Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            this.startActivity(intent);
        }
    }

    /**
     * Validate a activity_login attempt.
     *
     * @param view The submit button that was clicked
     */
    public void validateLoginAttempt(final View view) {
        LOGGER.info("Submit button clicked");
        Validate.notNull(view);

        final View mError = this.findViewById(R.id.error_message_login);
        final EditText passwordInput = (EditText) this.findViewById(R.id.login_password_input);


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

    /**
     * Go to the next activity from the {@link LoginActivity}.
     */
    private void toMainActivity() {
        this.clearPasswordInput(); // Clear the input for security
        final Intent intent = new Intent(this, MainActivity.class);
        this.startActivity(intent);
    }

    /**
     * Clear the password input.
     */
    private void clearPasswordInput() {
        final EditText passwordInput = (EditText) this.findViewById(R.id.login_password_input);
        passwordInput.setText("");
    }

}
