package com.nervousfish.nervousfish.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
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
 * The main activity class that shows a list of all people with their public keys
 */
public final class CreateProfileActivity extends AppCompatActivity {

    private static final Logger LOGGER = LoggerFactory.getLogger("CreateProfileActivity");
    private static final int MIN_PASSWORD_LENGTH = 6;

    private IServiceLocator serviceLocator;

    /**
     * Creates the new activity, should only be called by Android
     *
     * @param savedInstanceState Don't touch this
     */
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_create_profile);
        final Intent intent = getIntent();
        this.serviceLocator = (IServiceLocator) intent.getSerializableExtra(ConstantKeywords.SERVICE_LOCATOR);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        LOGGER.info("CreateProfileActivity created");
    }

    /**
     * Gets triggered when the Submit button is clicked.
     *
     * @param v The {@link View} clicked
     */
    public void onSubmitClick(final View v) {
        if (validateInputFields()) {
            final EditText nameInputField = (EditText) this.findViewById(R.id.profile_enter_name);
            final String name = nameInputField.getText().toString();
            final KeyPair keyPair = this.generateKeyPair();

            try {
                serviceLocator.getDatabase().addProfile(new Profile(name, keyPair));
                showProfileCreatedDialog();
            } catch (IOException e) {
                showProfileNotCreatedDialog();
            }
        } else {
            showProfileNotCreatedDialog();
        }
    }

    private void showProfileCreatedDialog() {
        new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
            .setTitleText(getString(R.string.profile_created))
            .setContentText(getString(R.string.profile_created_explanation))
            .setConfirmText(getString(R.string.dialog_ok))
            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(final SweetAlertDialog sDialog) {
                    sDialog.dismiss();
                    final Intent intent = new Intent(CreateProfileActivity.this, LoginActivity.class);
                    intent.putExtra(ConstantKeywords.SERVICE_LOCATOR, serviceLocator);
                    CreateProfileActivity.this.startActivity(intent);
                }
            })
            .show();
    }

    private void showProfileNotCreatedDialog() {
        new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText(getString(R.string.could_not_create_profile))
                .setContentText(getString(R.string.could_not_create_profile_explanation))
                .setConfirmText(getString(R.string.dialog_ok))
                .show();
    }

    /**
     * Generates a KeyPair based on the type selected.
     *
     * @return a {@link KeyPair} with the key type selected
     */
    private KeyPair generateKeyPair() {
        final RadioButton rsaKeyButton = (RadioButton) findViewById(R.id.radio_rsa_key);
        if (rsaKeyButton.isChecked()) {
            return serviceLocator.getKeyGenerator().generateRSAKeyPair("NervousFish generated key");
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
        return validateInputFieldName() && validateInputFieldPassword()
                && validateInputFieldPasswordRepeat() && validateInputPasswordsSame();
    }

    /**
     * @return True when the name is valid
     */
    private boolean validateInputFieldName() {
        final EditText nameInputField = (EditText) this.findViewById(R.id.profile_enter_name);

        if (isValidName(nameInputField.getText().toString())) {
            nameInputField.setBackgroundColor(Color.TRANSPARENT);
        } else {
            nameInputField.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.red_fail, null));
            return false;
        }
        return true;
    }

    /**
     * @return True when the password is valid
     */
    private boolean validateInputFieldPassword() {
        final EditText passwordInputField = (EditText) this.findViewById(R.id.profile_enter_password);

        if (isValidName(passwordInputField.getText().toString())
                && passwordInputField.getText().toString().length() >= MIN_PASSWORD_LENGTH) {
            passwordInputField.setBackgroundColor(Color.TRANSPARENT);
        } else {
            passwordInputField.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.red_fail, null));
            return false;
        }
        return true;
    }

    /**
     * @return True when the repeated password is valid
     */
    private boolean validateInputFieldPasswordRepeat() {
        final EditText passwordRepeatInputField = (EditText) this.findViewById(R.id.profile_repeat_password);

        if (isValidName(passwordRepeatInputField.getText().toString())) {
            passwordRepeatInputField.setBackgroundColor(Color.TRANSPARENT);
        } else {
            passwordRepeatInputField.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.red_fail, null));
            return false;
        }
        return true;
    }

    /**
     * @return True when the password matches the repeated password
     */
    private boolean validateInputPasswordsSame() {
        final EditText passwordInputField = (EditText) this.findViewById(R.id.profile_enter_password);
        final EditText passwordRepeatInputField = (EditText) this.findViewById(R.id.profile_repeat_password);

        if (!passwordInputField.getText().toString().equals(passwordRepeatInputField.getText().toString())) {
            passwordInputField.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.red_fail, null));
            passwordRepeatInputField.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.red_fail, null));
            return false;
        }
        return true;
    }

    /**
     * Will return true if the name is valid. This means
     * that it has at least 1 ASCII character.
     *
     * @param name The name that has been entered
     * @return a {@link boolean} telling if the name is valid or not
     */
    private boolean isValidName(final String name) {
        return name != null && !name.isEmpty() && !name.trim().isEmpty();
    }
}
