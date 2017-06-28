package com.nervousfish.nervousfish.activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;

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

import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import nl.tudelft.ewi.ds.bankver.IBAN;
import nl.tudelft.ewi.ds.bankver.bank.IBANVerifier;

/**
 * The {@link android.app.Activity} that is used to create a user profile when the app is first
 * used.
 */
@SuppressWarnings({"checkstyle:ReturnCount", "PMD.CyclomaticComplexity", "PMD.StdCyclomaticComplexity",
        "PMD.ExcessiveImports", "checkstyle:ClassDataAbstractionCoupling", "checkstyle:ClassFanOutComplexity"})
// 1) Suppresses return count to allow multiple returncodes while checking input fields.
// 2) Suppress excessive imports because it's necessairy and the 2 added imports methods would be unlogical
//    to outsource to another class
// 3) Suppressed because the class is not meant to be extensible or inheritable
//4. We want to have so much imports so we dont have to write Constants.ExplicitFieldResultCodes every time.
public final class CreateProfileActivity extends AppCompatActivity {
    private static final Logger LOGGER = LoggerFactory.getLogger("CreateProfileActivity");
    private final CreateProfileCustomKeyHelper customKeyHelper = new CreateProfileCustomKeyHelper(this, new CustomKeyPairSetter());
    private IServiceLocator serviceLocator;
    private CreateProfileHelper helper;
    private EditText nameInput;
    private EditText passwordInput;
    private EditText repeatPasswordInput;
    private EditText ibanInput;
    private RadioButton radiobuttonRSA;
    private RadioButton radiobuttonEd25519;
    private RadioButton radiobuttonExistingKeypair;

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
        this.radiobuttonRSA = (RadioButton) this.findViewById(R.id.rb_rsa);
        this.radiobuttonEd25519 = (RadioButton) this.findViewById(R.id.rb_ed25519);
        this.radiobuttonExistingKeypair = (RadioButton) this.findViewById(R.id.rb_existing_keypair);

        LOGGER.info("Activity created");
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
                if (this.radiobuttonExistingKeypair.isChecked()) {
                    this.customKeyHelper.askForCustomKeypair();
                } else {
                    this.finishCreatingProfile(this.generateKeyPairList());
                }
                break;
            case ALL_FIELDS_EMPTY:
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
     * Called when the user clicks on the info button of the keys
     *
     * @param view The button that was clicked
     */
    public void onKeyInfoButtonClicked(final View view) {
        final AlertDialog.Builder alert = new AlertDialog.Builder(this);

        final LayoutInflater inflater = this.getLayoutInflater();
        @SuppressLint("InflateParams")
        // 1) Suppressed because this dialog does not need a parent
        final View dialogView = inflater.inflate(R.layout.info_keys, null);
        alert.setView(dialogView);

        alert.setPositiveButton(this.getString(R.string.dialog_ok), null);
        alert.show();
    }

    /**
     * Creates the new {@link Profile}, adds it to the database and goes to {@link MainActivity}
     */
    private void finishCreatingProfile(final List<KeyPair> keyPairs) {
        final String name = this.nameInput.getText().toString();
        final String password = this.passwordInput.getText().toString();
        IBAN iban = null;
        if (IBANVerifier.isValidIBAN(ibanInput.getText().toString())) {
            iban = new IBAN(ibanInput.getText().toString());
        }
        final IDatabase database = this.serviceLocator.getDatabase();

        try {
            final Profile userProfile = new Profile(name, keyPairs, iban);

            database.createDatabase(userProfile, password);
            database.loadDatabase(password);

            this.showProfileCreatedDialog();
        } catch (final IOException e) {
            LOGGER.error("Something went wrong when creating a profile", e);
            this.showProfileNotCreatedDialog(this.getString(R.string.create_profile_error_adding_to_database));
        }
    }

    /*
     * Generates a list of key pairs, given the checked boxes.
     *
     * @return A list of keypairs with the types checked in the boxes.
     */
    private List<KeyPair> generateKeyPairList() {
        final List<IKey.Types> keytypesToGenerate = new ArrayList<>();
        if (this.radiobuttonRSA.isChecked()) {
            keytypesToGenerate.add(IKey.Types.RSA);
        }
        if (this.radiobuttonEd25519.isChecked()) {
            keytypesToGenerate.add(IKey.Types.Ed25519);
        }
        final List<KeyPair> keyPairs = new ArrayList<>();
        for (final IKey.Types type : keytypesToGenerate) {
            keyPairs.addAll(this.helper.generateKeyPairs(type));
        }

        return keyPairs;

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

        if (nameValidation == Constants.InputFieldResultCodes.EMPTY_FIELD && passwordValidation == Constants.InputFieldResultCodes.EMPTY_FIELD
                && repeatPasswordValidation == Constants.InputFieldResultCodes.EMPTY_FIELD) {
            return Constants.ExplicitFieldResultCodes.ALL_FIELDS_EMPTY;
        } else if (nameValidation == Constants.InputFieldResultCodes.EMPTY_FIELD) {
            return Constants.ExplicitFieldResultCodes.NAME_EMPTY;
        } else if (passwordValidation == Constants.InputFieldResultCodes.EMPTY_FIELD) {
            return Constants.ExplicitFieldResultCodes.PASSWORD_EMPTY;
        } else if (passwordValidation == Constants.InputFieldResultCodes.TOO_SHORT_FIELD) {
            return Constants.ExplicitFieldResultCodes.PASSWORD_TOO_SHORT;
        } else if (ibanValidation == Constants.InputFieldResultCodes.INVALID_IBAN) {
            return Constants.ExplicitFieldResultCodes.INVALID_IBAN;
        } else if (this.helper.passwordsEqual(this.passwordInput, this.repeatPasswordInput)) {
            return Constants.ExplicitFieldResultCodes.INPUT_CORRECT;
        } else {
            return Constants.ExplicitFieldResultCodes.PASSWORDS_NOT_EQUAL;
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
        final RadioButton rsaRadioButton = (RadioButton) this.findViewById(R.id.rb_rsa);
        final RadioButton ed25519RadioButton = (RadioButton) this.findViewById(R.id.rb_ed25519);
        if (rsaRadioButton.isChecked()) {
            return IKey.Types.RSA;
        } else if (ed25519RadioButton.isChecked()) {
            return IKey.Types.Ed25519;
        }
        throw new IllegalArgumentException("No radio button selected");
    }

    /**
     * Can be given by {@link CreateProfileActivity} to classes that are allowed to finish the creation of the new profile
     * by supplying a new RSAKey
     */
    final class CustomKeyPairSetter {

        /**
         * Sets the custom key of the user to the RSA key pair specified
         *
         * @param publicKey  The public part of the RSA key pair
         * @param privateKey The private part of the RSA key pair
         */
        void setRSAKeyPair(final RSAKey publicKey, final RSAKey privateKey) {
            final List<KeyPair> keyPairs = new ArrayList<>();
            keyPairs.add(new KeyPair("Custom key", publicKey, privateKey));
            CreateProfileActivity.this.finishCreatingProfile(keyPairs);
        }
    }
}
