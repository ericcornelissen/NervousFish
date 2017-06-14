package com.nervousfish.nervousfish.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import com.nervousfish.nervousfish.R;
import com.nervousfish.nervousfish.data_objects.IKey;
import com.nervousfish.nervousfish.data_objects.KeyPair;
import com.nervousfish.nervousfish.data_objects.Profile;
import com.nervousfish.nervousfish.modules.cryptography.IKeyGenerator;
import com.nervousfish.nervousfish.modules.database.IDatabase;
import com.nervousfish.nervousfish.service_locator.IServiceLocator;
import com.nervousfish.nervousfish.service_locator.NervousFish;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * The {@link android.app.Activity} that is used to create a user profile when the app is first
 * used.
 */
public final class CreateProfileActivity extends AppCompatActivity {

    private static final Logger LOGGER = LoggerFactory.getLogger("CreateProfileActivity");

    private static final int GOOD_FIELD = 100;
    private static final int EMPTY_FIELD = 101;
    private static final int TOO_SHORT_FIELD = 102;
    private static final int PASSWORD_TOO_SHORT = 103;
    private static final int PASSWORD_EMPTY = 104;
    private static final int NAME_EMPTY = 105;
    private static final int PASSWORDS_NOT_EQUAL = 106;


    private IServiceLocator serviceLocator;
    private CreateProfileHelper helper;
    private EditText nameInput;
    private EditText passwordInput;
    private EditText repeatPasswordInput;

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_create_profile);
        this.serviceLocator = NervousFish.getServiceLocator();

        // Initialize helper class
        final IKeyGenerator keyGenerator = this.serviceLocator.getKeyGenerator();
        final int alertColor = ResourcesCompat.getColor(this.getResources(), R.color.red_fail, null);
        this.helper = new CreateProfileHelper(keyGenerator, alertColor);

        // Find input views
        this.nameInput = (EditText) this.findViewById(R.id.profile_enter_name);
        this.passwordInput = (EditText) this.findViewById(R.id.profile_enter_password);
        this.repeatPasswordInput = (EditText) this.findViewById(R.id.profile_repeat_password);

        LOGGER.info("Activity created");
    }

    /**
     * Gets triggered when the Submit button is clicked.
     *
     * @param view The {@link View} clicked
     */
    public void onSubmitClick(final View view) {
        switch (this.validateInputFields()) {
            case GOOD_FIELD:
                final String name = this.nameInput.getText().toString();
                final KeyPair keyPair = this.helper.generateKeyPair(IKey.Types.RSA);

                try {
                    final IDatabase database = this.serviceLocator.getDatabase();
                    final Profile profile = new Profile(name, keyPair);
                    database.addProfile(profile);
                    this.showProfileCreatedDialog();
                } catch (IOException e) {
                    this.showProfileNotCreatedDialog(this.getString(R.string.create_profile_error_adding_to_database));
                }
                break;
            case PASSWORD_EMPTY:
                this.showProfileNotCreatedDialog(this.getString(R.string.create_profile_empty_password));
                break;
            case PASSWORD_TOO_SHORT:
                this.showProfileNotCreatedDialog(this.getString(R.string.create_proflie_too_short_password));
                break;
            case NAME_EMPTY:
                this.showProfileNotCreatedDialog(this.getString(R.string.create_profile_empty_name));
                break;
            case PASSWORDS_NOT_EQUAL:
                this.showProfileNotCreatedDialog(this.getString(R.string.create_profile_passwords_not_equal));
                break;
            default:
                break;

        }
    }

    /**
     * Validates if the input fields are not empty and if the input is valid.
     * This also means that the password and the repeat password should be the same,
     * and the password length is larger or equal to 6.
     *
     * @return a {@link int} which is the result code of the various input validations
     */
    private int validateInputFields() {
        if (this.helper.validateName(this.nameInput) == EMPTY_FIELD) {
            return NAME_EMPTY;
        }
        final int passwordValidation = this.helper.validatePassword(this.passwordInput);
        if (passwordValidation == EMPTY_FIELD) {
            return PASSWORD_EMPTY;
        } else if (passwordValidation == TOO_SHORT_FIELD) {
            return PASSWORD_TOO_SHORT;
        }
        if (!this.helper.passwordsEqual(this.passwordInput, this.repeatPasswordInput)) {
            return PASSWORDS_NOT_EQUAL;
        }
        return GOOD_FIELD;
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
                .setTitleText(this.getString(R.string.profile_created_title))
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
    private void showProfileNotCreatedDialog(final String message) {
        new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText(this.getString(R.string.profile_not_created_title))
                .setContentText(message)
                .setConfirmText(this.getString(R.string.dialog_ok))
                .show();
    }

}
