package com.nervousfish.nervousfish.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;

import com.nervousfish.nervousfish.R;
import com.nervousfish.nervousfish.data_objects.IKey;
import com.nervousfish.nervousfish.data_objects.KeyPair;
import com.nervousfish.nervousfish.data_objects.Profile;
import com.nervousfish.nervousfish.modules.cryptography.IKeyGenerator;
import com.nervousfish.nervousfish.service_locator.EntryActivity;
import com.nervousfish.nervousfish.service_locator.IServiceLocator;
import com.nervousfish.nervousfish.service_locator.NervousFish;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * The {@link android.app.Activity} used to create a new profile.
 */
public final class CreateProfileActivity extends AppCompatActivity {

    private static final Logger LOGGER = LoggerFactory.getLogger("CreateProfileActivity");

    private IServiceLocator serviceLocator;
    private CreateProfileHelper helper;
    private EditText nameInput;
    private EditText passwordInput;
    private EditText repeatPasswordInput;

    /**
     * Creates the new activity, should only be called by Android.
     *
     * @param savedInstanceState The saved state of the previous instance.
     */
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_create_profile);
        this.serviceLocator = NervousFish.getServiceLocator();

        final Window window = this.getWindow();
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        // Initialize helper class
        final IKeyGenerator keyGenerator = this.serviceLocator.getKeyGenerator();
        final int alertColor = ResourcesCompat.getColor(this.getResources(), R.color.red_fail, null);
        this.helper = new CreateProfileHelper(keyGenerator, alertColor);

        // Find input views
        this.nameInput = (EditText) this.findViewById(R.id.profile_enter_name);
        this.passwordInput = (EditText) this.findViewById(R.id.profile_enter_password);
        this.repeatPasswordInput = (EditText) this.findViewById(R.id.profile_repeat_password);

        LOGGER.info("activity created");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onStart() {
        super.onStart();

        if (this.serviceLocator == null) {
            final Intent intent = new Intent(this, EntryActivity.class);
            this.startActivity(intent);
        }
    }

    /**
     * Gets triggered when the Submit button is clicked.
     *
     * @param v The {@link View} clicked
     */
    public void onSubmitClick(final View v) {
        if (this.validateInputFields()) {
            final String name = this.nameInput.getText().toString();
            final KeyPair keyPair = this.helper.generateKeyPair(IKey.Types.RSA);

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
     * Validates if the input fields are not empty and if the input is valid.
     * This also means that the password and the repeat password should be the same,
     * and the password length is larger or equal to 6.
     *
     * @return a {@link boolean} which is true if all fields are valid
     */
    private boolean validateInputFields() {
        return this.helper.validateName(this.nameInput)
                & this.helper.validatePassword(this.passwordInput)
                & this.helper.validatePassword(this.repeatPasswordInput)
                & this.helper.passwordsEqual(this.passwordInput, this.repeatPasswordInput);
    }

    /**
     * Progress to the next activity from the {@link CreateProfileActivity}.
     */
    private void nextActivity() {
        final Intent intent = new Intent(this, MainActivity.class);
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

}
