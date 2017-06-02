package com.nervousfish.nervousfish.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.RadioButton;

import com.nervousfish.nervousfish.ConstantKeywords;
import com.nervousfish.nervousfish.R;
import com.nervousfish.nervousfish.data_objects.KeyPair;
import com.nervousfish.nervousfish.data_objects.Profile;
import com.nervousfish.nervousfish.service_locator.IServiceLocator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * The {@link android.app.Activity} used to create a new profile.
 */
public final class CreateProfileActivity extends AppCompatActivity {

    private static final Logger LOGGER = LoggerFactory.getLogger("CreateProfileActivity");
    private static final int MIN_PASSWORD_LENGTH = 6;

    private IServiceLocator serviceLocator;
    private int red;

    /**
     * Creates the new activity, should only be called by Android.
     *
     * @param savedInstanceState The saved state of the previous instance.
     */
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_create_profile);

        final Intent intent = this.getIntent();
        this.serviceLocator = (IServiceLocator) intent.getSerializableExtra(ConstantKeywords.SERVICE_LOCATOR);

        final Window window = this.getWindow();
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        this.red = ResourcesCompat.getColor(this.getResources(), R.color.red_fail, null);

        LOGGER.info("CreateProfileActivity created");
    }

    /**
     * Gets triggered when the Submit button is clicked.
     *
     * @param v The {@link View} clicked
     */
    public void onSubmitClick(final View v) {
        if (this.validateInputFields()) {
            final EditText nameInputField = (EditText) this.findViewById(R.id.profile_enter_name);
            final String name = nameInputField.getText().toString();
            final KeyPair keyPair = this.generateKeyPair();

            try {
                this.serviceLocator.getDatabase().addProfile(new Profile(name, keyPair));
                this.showProfileCreatedDialog();
            } catch (IOException e) {
                this.showProfileNotCreatedDialog();
            }
        } else {
            this.showProfileNotCreatedDialog();
        }
    }

    /**
     * Progress to the next activity from the {@link CreateProfileActivity}.
     */
    private void nextActivity() {
        final Intent intent = new Intent(CreateProfileActivity.this, MainActivity.class);
        intent.putExtra(ConstantKeywords.SERVICE_LOCATOR, this.serviceLocator);
        this.startActivity(intent);
    }

    /**
     * Show the dialog for when the profile could be created.
     */
    private void showProfileCreatedDialog() {
        new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
            .setTitleText(this.getString(R.string.profile_created))
            .setContentText(this.getString(R.string.profile_created_explanation))
            .setConfirmText(this.getString(R.string.dialog_ok))
            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(final SweetAlertDialog dialog) {
                    dialog.dismiss();
                    nextActivity();
                }
            })
            .show();
    }

    /**
     * Show the dialog for when the profile couldn't be created.
     */
    private void showProfileNotCreatedDialog() {
        new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText(this.getString(R.string.could_not_create_profile))
                .setContentText(this.getString(R.string.could_not_create_profile_explanation))
                .setConfirmText(this.getString(R.string.dialog_ok))
                .show();
    }

    /**
     * Generates a KeyPair based on the type selected.
     *
     * @return a {@link KeyPair} with the key type selected
     */
    private KeyPair generateKeyPair() {
        final RadioButton rsaKeyButton = (RadioButton) this.findViewById(R.id.radio_rsa_key);
        if (rsaKeyButton.isChecked()) {
            return this.serviceLocator.getKeyGenerator().generateRSAKeyPair("NervousFish generated key");
        }

        throw new IllegalArgumentException("The selected key is not implemented");
    }

    /**
     * Validates if the input fields are not empty and if the input is valid.
     * This also means that the password and the repeat password should be the same,
     * and the password length is larger or equal to 6.
     *
     * @return a {@link boolean} which is true if all fields are valid
     */
    private boolean validateInputFields() {
        return this.validateInputFieldName() & this.validateInputFieldPassword()
                & this.validateInputFieldPasswordRepeat() & this.validateInputPasswordsSame();
    }

    /**
     * @return True when the name is valid
     */
    private boolean validateInputFieldName() {
        final EditText input = (EditText) this.findViewById(R.id.profile_enter_name);

        if (this.isValidName(input.getText().toString())) {
            input.setBackgroundColor(Color.TRANSPARENT);
        } else {
            input.setBackgroundColor(this.red);
            return false;
        }

        return true;
    }

    /**
     * @return True when the password is valid.
     */
    private boolean validateInputFieldPassword() {
        final EditText inputField = (EditText) this.findViewById(R.id.profile_enter_password);
        final String password = inputField.getText().toString();
        if (this.isValidPassword(password)) {
            inputField.setBackgroundColor(Color.TRANSPARENT);
        } else {
            inputField.setBackgroundColor(this.red);
            return false;
        }

        return true;
    }

    /**
     * @return True when the repeated password is valid.
     */
    private boolean validateInputFieldPasswordRepeat() {
        final EditText inputField = (EditText) this.findViewById(R.id.profile_repeat_password);
        final String password = inputField.getText().toString();
        if (this.isValidPassword(password)) {
            inputField.setBackgroundColor(Color.TRANSPARENT);
        } else {
            inputField.setBackgroundColor(this.red);
            return false;
        }

        return true;
    }

    /**
     * @return True when the password matches the repeated password.
     */
    private boolean validateInputPasswordsSame() {
        final EditText initialInput = (EditText) this.findViewById(R.id.profile_enter_password);
        final EditText repeatInput = (EditText) this.findViewById(R.id.profile_repeat_password);

        final String initialPassword = initialInput.getText().toString();
        final String repeatPassword = repeatInput.getText().toString();
        if (!initialPassword.equals(repeatPassword)) {
            initialInput.setBackgroundColor(this.red);
            repeatInput.setBackgroundColor(this.red);
            return false;
        }

        return true;
    }

    /**
     * Will return true if the name is valid. This means
     * that it has at least 1 ASCII character.
     *
     * @param name The name that has been entered.
     * @return a {@link boolean} indicating whether or not the name is valid.
     */
    private boolean isValidName(final String name) {
        return name != null
                && !name.isEmpty()
                && !name.trim().isEmpty();
    }

    /**
     * Will return true if the name is valid. This means
     * that it has at least 6 ASCII character.
     *
     * @param password The password that has been entered.
     * @return a {@link boolean} indicating whether or not the password is valid.
     */
    private boolean isValidPassword(final String password) {
        return password != null
                && !password.isEmpty()
                && !password.trim().isEmpty()
                && password.length() >= MIN_PASSWORD_LENGTH;
    }

}
