package com.nervousfish.nervousfish.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import com.nervousfish.nervousfish.ConstantKeywords;
import com.nervousfish.nervousfish.R;
import com.nervousfish.nervousfish.service_locator.IServiceLocator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.pedant.SweetAlert.SweetAlertDialog;

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

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        LOGGER.info("CreateProfileActivity created");
    }

    /**
     * Gets triggered when the Submit button is clicked.
     *
     * @param v - the {@link View} clicked
     */
    public void onSubmitClick(final View v) {
        if(validateInputFields()) {
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
        } else {
            new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText(getString(R.string.could_not_create_profile))
                    .setContentText(getString(R.string.could_not_create_profile_explanation))
                    .setConfirmText(getString(R.string.dialog_ok))
                    .setConfirmClickListener(null)
                    .show();
        }
    }

    /**
     * Validates if the input fields are not empty and if the input is valid.
     * This also means that the password and the repeat password should be the same,
     * and the password length is larger or equal to 6.
     *
     * @return a {@link boolean} which is true if all fields are valid
     */
    private boolean validateInputFields() {
        boolean allValid = true;

        final EditText nameInputField = (EditText) this.findViewById(R.id.profileEnterName);
        if(!isValidName(nameInputField.getText().toString())) {
            nameInputField.setBackgroundColor(Color.parseColor("#dd6b55"));
            allValid = false;
        } else {
            nameInputField.setBackgroundColor(Color.TRANSPARENT);
        }

        final EditText passwordInputField = (EditText) this.findViewById(R.id.profileEnterPassword);
        if(!isValidName(passwordInputField.getText().toString()) || !(passwordInputField.getText().toString().length() >= 6)) {
            passwordInputField.setBackgroundColor(Color.parseColor("#dd6b55"));
            allValid = false;
        } else {
            passwordInputField.setBackgroundColor(Color.TRANSPARENT);
        }

        final EditText passwordRepeatInputField = (EditText) this.findViewById(R.id.profileRepeatPassword);
        if(!isValidName(passwordRepeatInputField.getText().toString())) {
            passwordRepeatInputField.setBackgroundColor(Color.parseColor("#dd6b55"));
            allValid = false;
        } else {
            passwordRepeatInputField.setBackgroundColor(Color.TRANSPARENT);
        }

        if(!passwordInputField.getText().toString().equals(passwordRepeatInputField.getText().toString())) {
            passwordInputField.setBackgroundColor(Color.parseColor("#dd6b55"));
            passwordRepeatInputField.setBackgroundColor(Color.parseColor("#dd6b55"));
            allValid = false;
        }

        return allValid;
    }

    /**
     * Will return true if the name is valid. This means
     * that it has at least 1 ASCII character.
     *
     * @param name - the name that has been entered
     * @return a {@link boolean} telling if the name is valid or not
     */
    private boolean isValidName(final String name) {
        return name != null && !name.isEmpty() && !name.trim().isEmpty();
    }
}
