package com.nervousfish.nervousfish.activities;

import android.app.Activity;
import android.content.Intent;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.nervousfish.nervousfish.R;
import com.nervousfish.nervousfish.modules.database.IDatabase;
import com.nervousfish.nervousfish.service_locator.IServiceLocator;
import com.nervousfish.nervousfish.service_locator.NervousFish;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Objects;

/**
 * An {@link Activity} that draws the screen that is used to login by entering a password.
 */
public final class LoginActivity extends AppCompatActivity {

    private static final Logger LOGGER = LoggerFactory.getLogger("LoginActivity");
    private IServiceLocator serviceLocator;

    private KeyboardView keyboardView;

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.login);

        this.serviceLocator = NervousFish.getServiceLocator();

        final Keyboard keyboard = new Keyboard(this, R.xml.qwerty);
        this.keyboardView = (KeyboardView) this.findViewById(R.id.keyboardview);
        this.keyboardView.setKeyboard(keyboard);
        this.keyboardView.setPreviewEnabled(false);
        this.keyboardView.setOnKeyboardActionListener(new OnCustomKeyboardActionListener(this));

        final EditText editPassword = (EditText) this.findViewById(R.id.login_password_input);

        editPassword.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                this.showCustomKeyboard(v);
            } else {
                this.hideCustomKeyboard();
            }
        });
        editPassword.setOnClickListener(this::showCustomKeyboard);
        editPassword.setCustomSelectionActionModeCallback(new LoginActivity.EditPasswordSelectionCallback());

        // Disable the default keyboard
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        LOGGER.info("LoginActivity created");
    }

    private void hideCustomKeyboard() {
        this.keyboardView.setVisibility(View.GONE);
        this.keyboardView.setEnabled(false);
    }

    private void showCustomKeyboard(final View view) {
        this.keyboardView.setVisibility(View.VISIBLE);
        this.keyboardView.setEnabled(true);
        ((InputMethodManager) this.getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private boolean isCustomKeyboardVisible() {
        return this.keyboardView.getVisibility() == View.VISIBLE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onBackPressed() {
        if (this.isCustomKeyboardVisible()) {
            this.hideCustomKeyboard();
        } else {
            final Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            this.startActivity(intent);
        }
    }

    /**
     * Validate a login attempt.
     *
     * @param view The submit button that was clicked
     */
    public void validateLoginAttempt(final View view) {
        LOGGER.info("Submit button clicked");

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
        final Intent intent = new Intent(this, MainActivity.class);
        this.startActivity(intent);
    }

    /**
     * Lets all its methods return false so that the copy/paste menu won't appear / work
     */
    private static final class EditPasswordSelectionCallback implements ActionMode.Callback {
        @Override
        public boolean onCreateActionMode(final ActionMode mode, final Menu menu) {
            return false;
        }

        @Override
        public boolean onPrepareActionMode(final ActionMode mode, final Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(final ActionMode mode, final MenuItem item) {
            return false;
        }

        @Override
        public void onDestroyActionMode(final ActionMode mode) {
            // Unused
        }
    }

    private static final class OnCustomKeyboardActionListener implements KeyboardView.OnKeyboardActionListener {
        private final Activity activity;

        OnCustomKeyboardActionListener(final Activity activity) {
            this.activity = activity;
        }

        @Override
        public void onPress(final int primaryCode) {
            // Unused
        }

        @Override
        public void onRelease(final int primaryCode) {
            // Unused
        }

        @Override
        public void onKey(final int primaryCode, final int[] keyCodes) {
            final View focusCurrent = this.activity.getWindow().getCurrentFocus();
            if (focusCurrent == null) {
                return;
            }
            final Class<?> focusClass = focusCurrent.getClass();
            if (!Objects.equals(focusClass, EditText.class) && !Objects.equals(focusCurrent.getClass(), AppCompatEditText.class)) {
                return;
            }

            final EditText edittext = (EditText) focusCurrent;
            final Editable editable = edittext.getText();
            final int start = edittext.getSelectionStart();
            if (primaryCode == Keyboard.KEYCODE_CANCEL) {
                final int length = editable.length();
                if (length > 0) {
                    editable.delete(length - 1, length);
                }
            } else {
                editable.insert(start, Character.toString((char) primaryCode));
            }
        }

        @Override
        public void onText(final CharSequence text) {
            // Unused
        }

        @Override
        public void swipeLeft() {
            // Unused
        }

        @Override
        public void swipeRight() {
            // Unused
        }

        @Override
        public void swipeDown() {
            // Unused
        }

        @Override
        public void swipeUp() {
            // Unused
        }
    }
}
