package com.nervousfish.nervousfish.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;

import com.nervousfish.nervousfish.R;
import com.nervousfish.nervousfish.data_objects.IKey;
import com.nervousfish.nervousfish.data_objects.KeyPair;
import com.nervousfish.nervousfish.data_objects.Profile;
import com.nervousfish.nervousfish.modules.constants.Constants;
import com.nervousfish.nervousfish.modules.cryptography.IKeyGenerator;
import com.nervousfish.nervousfish.modules.database.IDatabase;
import com.nervousfish.nervousfish.service_locator.IServiceLocator;
import com.nervousfish.nervousfish.service_locator.NervousFish;

import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import nl.tudelft.ewi.ds.bankver.IBAN;
import nl.tudelft.ewi.ds.bankver.bank.IBANVerifier;

import static com.nervousfish.nervousfish.modules.constants.Constants.ExplicitFieldResultCodes.ALl_FIELDS_EMPTY;
import static com.nervousfish.nervousfish.modules.constants.Constants.ExplicitFieldResultCodes.INPUT_CORRECT;
import static com.nervousfish.nervousfish.modules.constants.Constants.ExplicitFieldResultCodes.NAME_EMPTY;
import static com.nervousfish.nervousfish.modules.constants.Constants.ExplicitFieldResultCodes.PASSWORDS_NOT_EQUAL;
import static com.nervousfish.nervousfish.modules.constants.Constants.ExplicitFieldResultCodes.PASSWORD_EMPTY;
import static com.nervousfish.nervousfish.modules.constants.Constants.ExplicitFieldResultCodes.PASSWORD_TOO_SHORT;
import static com.nervousfish.nervousfish.modules.constants.Constants.InputFieldResultCodes.EMPTY_FIELD;
import static com.nervousfish.nervousfish.modules.constants.Constants.InputFieldResultCodes.INVALID_IBAN;
import static com.nervousfish.nervousfish.modules.constants.Constants.InputFieldResultCodes.TOO_SHORT_FIELD;

/**
 * The {@link android.app.Activity} that is used to create a user profile when the app is first
 * used.
 */
@SuppressWarnings({"checkstyle:ReturnCount", "PMD.CyclomaticComplexity", "PMD.StdCyclomaticComplexity",
    "PMD.ExcessiveImports"})
//1. Suppresses return count to allow multiple returncodes while checking input fields.
//2 and 3. The complexity does not make the code unreadible at this moment.
//4. We want to have so much imports so we dont have to write Constants.ExplicitFieldResultCodes every time.
public final class CreateProfileActivity extends AppCompatActivity {

    private static final Logger LOGGER = LoggerFactory.getLogger("CreateProfileActivity");
    private IServiceLocator serviceLocator;
    private CustomKeyboardHelper customKeyboard;
    private CreateProfileHelper helper;
    private EditText nameInput;
    private EditText passwordInput;
    private EditText repeatPasswordInput;
    private EditText ibanInput;

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
        this.ibanInput = (EditText) this.findViewById(R.id.iban_create_profile);

        LOGGER.info("Activity created");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onBackPressed() {
        if (this.customKeyboard.isVisible()) {
            this.customKeyboard.hide();
        }
    }

    /**
     * Gets triggered when the Submit button is clicked.
     *
     * @param view The {@link View} clicked
     */
    public void onSubmitClick(final View view) {
        Validate.notNull(view);
        final Constants.ExplicitFieldResultCodes result = this.validateInputFields();
        switch (result) {
            case INPUT_CORRECT:
                final String name = nameInput.getText().toString();
                final String password = passwordInput.getText().toString();
                IBAN iban = null;
                if (IBANVerifier.isValidIBAN(ibanInput.getText().toString())) {
                    iban = new IBAN(ibanInput.getText().toString());
                }
                final IDatabase database = this.serviceLocator.getDatabase();

                try {
                    // Create the new profile
                    final List<KeyPair> keyPairs = helper.generateKeyPairs(this.getKeyTypeFromInput());
                    final Profile userProfile = new Profile(name, keyPairs, iban);

                    database.createDatabase(userProfile, password);
                    database.loadDatabase(password);

                    this.showProfileCreatedDialog();
                } catch (final IOException e) {
                    LOGGER.error("Something went wrong when creating a profile", e);
                    this.showProfileNotCreatedDialog(this.getString(R.string.create_profile_error_adding_to_database));
                }
                break;
            case ALl_FIELDS_EMPTY:
                this.showProfileNotCreatedDialog(this.getString(R.string.create_profile_all_fields_empty));
                break;
            case NAME_EMPTY:
                this.showProfileNotCreatedDialog(this.getString(R.string.create_profile_empty_name));
                break;
            case INVALID_IBAN:
                this.showProfileNotCreatedDialog(this.getString(R.string.create_profile_invalid_iban));
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
        final Constants.InputFieldResultCodes ibanValidation = this.helper.validateIban(this.ibanInput);

        if (nameValidation == EMPTY_FIELD && passwordValidation == EMPTY_FIELD
                && repeatPasswordValidation == EMPTY_FIELD) {
            return ALl_FIELDS_EMPTY;
        } else if (nameValidation == EMPTY_FIELD) {
            return NAME_EMPTY;
        } else if (passwordValidation == EMPTY_FIELD) {
            return PASSWORD_EMPTY;
        } else if (passwordValidation == TOO_SHORT_FIELD) {
            return PASSWORD_TOO_SHORT;
        } else if (ibanValidation == INVALID_IBAN) {
            return Constants.ExplicitFieldResultCodes.INVALID_IBAN;
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
                    dialog.dismissWithAnimation();
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
     * Gets from the radio buttons in this activity the one checked.
     *
     * @return The {@link IKey.Types} of the key that is checked
     */
    public IKey.Types getKeyTypeFromInput() {
        final RadioButton rsaRadioButton = (RadioButton) this.findViewById(R.id.checkbox_rsa_key);
        final RadioButton ed25519RadioButton = (RadioButton) this.findViewById(R.id.checkbox_ed25519_key);
        if (rsaRadioButton.isChecked()) {
            return IKey.Types.RSA;
        } else if (ed25519RadioButton.isChecked()) {
            return IKey.Types.Ed25519;
        }
        throw new IllegalArgumentException("No radio button selected");
    }
}
