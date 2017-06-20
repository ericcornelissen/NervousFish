package com.nervousfish.nervousfish.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import com.nervousfish.nervousfish.R;
import com.nervousfish.nervousfish.data_objects.IKey;
import com.nervousfish.nervousfish.data_objects.KeyPair;
import com.nervousfish.nervousfish.data_objects.Profile;
import com.nervousfish.nervousfish.data_objects.RSAKey;
import com.nervousfish.nervousfish.modules.constants.Constants;
import com.nervousfish.nervousfish.modules.cryptography.IKeyGenerator;
import com.nervousfish.nervousfish.modules.database.IDatabase;
import com.nervousfish.nervousfish.service_locator.IServiceLocator;
import com.nervousfish.nervousfish.service_locator.NervousFish;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

import static com.nervousfish.nervousfish.modules.constants.Constants.ExplicitFieldResultCodes.ALl_FIELDS_EMPTY;
import static com.nervousfish.nervousfish.modules.constants.Constants.ExplicitFieldResultCodes.INPUT_CORRECT;
import static com.nervousfish.nervousfish.modules.constants.Constants.ExplicitFieldResultCodes.NAME_EMPTY;
import static com.nervousfish.nervousfish.modules.constants.Constants.ExplicitFieldResultCodes.PASSWORDS_NOT_EQUAL;
import static com.nervousfish.nervousfish.modules.constants.Constants.ExplicitFieldResultCodes.PASSWORD_EMPTY;
import static com.nervousfish.nervousfish.modules.constants.Constants.ExplicitFieldResultCodes.PASSWORD_TOO_SHORT;
import static com.nervousfish.nervousfish.modules.constants.Constants.InputFieldResultCodes.EMPTY_FIELD;
import static com.nervousfish.nervousfish.modules.constants.Constants.InputFieldResultCodes.TOO_SHORT_FIELD;


/**
 * The {@link android.app.Activity} that is used to create a user profile when the app is first
 * used.
 */
@SuppressWarnings("checkstyle:ReturnCount")
//Suppresses return count to allow multiple returncodes while checking input fields.
public final class CreateProfileActivity extends AppCompatActivity {
    private static final Logger LOGGER = LoggerFactory.getLogger("CreateProfileActivity");
    private final CreateProfileCustomKeyHelper customKeyHelper = new CreateProfileCustomKeyHelper(this, new CustomKeySetter());
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
        final Constants.ExplicitFieldResultCodes result = this.validateInputFields();
        switch (result) {
            case INPUT_CORRECT:
                if (((CheckBox) this.findViewById(R.id.checkbox_use_existing_keypair)).isChecked()) {
                    this.customKeyHelper.askForCustomKeypair();
                } else {
                    this.finishCreatingProfile(this.helper.generateKeyPairs(IKey.Types.RSA));
                }
                break;
            case ALl_FIELDS_EMPTY:
                this.showProfileNotCreatedDialog(this.getString(R.string.create_profile_all_fields_empty));
                break;
            case NAME_EMPTY:
                this.showProfileNotCreatedDialog(this.getString(R.string.create_profile_empty_name));
                break;
            case PASSWORD_EMPTY:
                this.showProfileNotCreatedDialog(this.getString(R.string.create_profile_empty_password));
                break;
            case PASSWORD_TOO_SHORT:
                this.showProfileNotCreatedDialog(this.getString(R.string.create_proflie_too_short_password));
                break;
            case PASSWORDS_NOT_EQUAL:
                this.showProfileNotCreatedDialog(this.getString(R.string.create_profile_passwords_not_equal));
                break;
            default:
                break;

        }
    }

    /**
     * Creates the new {@link Profile}, adds it to the database and goes to {@link MainActivity}
     */
    private void finishCreatingProfile(final List<KeyPair> keyPairs) {
        final String name = this.nameInput.getText().toString();
        final String password = this.passwordInput.getText().toString();
        final IDatabase database = this.serviceLocator.getDatabase();

        try {
            final Profile userProfile = new Profile(name, keyPairs);

            database.createDatabase(userProfile, password);
            database.loadDatabase(password);

            this.showProfileCreatedDialog();
        } catch (final IOException e) {
            LOGGER.error("Something went wrong when creating a profile", e);
            this.showProfileNotCreatedDialog(this.getString(R.string.create_profile_error_adding_to_database));
        }
    }

    /**
     * Validates if the input fields are not empty and if the input is valid.
     * This also means that the password and the repeat password should be the same,
     * and the password length is larger or equal to 6.
     *
     * @return a {@link Constants.ExplicitFieldResultCodes} which is the result code of the various input validations
     */
    private Constants.ExplicitFieldResultCodes validateInputFields() {
        final Constants.InputFieldResultCodes nameValidation = this.helper.validateName(this.nameInput);
        final Constants.InputFieldResultCodes passwordValidation = this.helper.validatePassword(this.passwordInput);
        final Constants.InputFieldResultCodes repeatPasswordValidation = this.helper.validatePassword(this.repeatPasswordInput);

        if (nameValidation == EMPTY_FIELD && passwordValidation == EMPTY_FIELD
                && repeatPasswordValidation == EMPTY_FIELD) {
            return ALl_FIELDS_EMPTY;
        } else if (nameValidation == EMPTY_FIELD) {
            return NAME_EMPTY;
        } else if (passwordValidation == EMPTY_FIELD) {
            return PASSWORD_EMPTY;
        } else if (passwordValidation == TOO_SHORT_FIELD) {
            return PASSWORD_TOO_SHORT;
        } else if (this.helper.passwordsEqual(this.passwordInput, this.repeatPasswordInput)) {
            return INPUT_CORRECT;
        } else {
            return PASSWORDS_NOT_EQUAL;
        }
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
                .setConfirmClickListener(dialog -> {
                    dialog.dismiss();
                    this.nextActivity();
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

    /**
     * Can be given by {@link CreateProfileActivity} to classes that are allowed finish creating the new profile
     * by supplying a new RSAKey
     */
    final class CustomKeySetter {

        void setRSAKey(final RSAKey publicKey, final RSAKey privateKey) {
            final List<KeyPair> keyPairs = new ArrayList<>();
            keyPairs.add(new KeyPair("Custom key", publicKey, privateKey));
            CreateProfileActivity.this.finishCreatingProfile(keyPairs);
        }
    }
}
