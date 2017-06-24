package com.nervousfish.nervousfish.activities;

import android.graphics.Color;
import android.widget.EditText;

import com.nervousfish.nervousfish.data_objects.AKeyPair;
import com.nervousfish.nervousfish.data_objects.Ed25519KeyPair;
import com.nervousfish.nervousfish.data_objects.IKey;
import com.nervousfish.nervousfish.data_objects.RSAKeyPair;
import com.nervousfish.nervousfish.modules.constants.Constants;
import com.nervousfish.nervousfish.modules.cryptography.IKeyGenerator;

import org.apache.commons.lang3.Validate;

import java.util.ArrayList;
import java.util.List;

import nl.tudelft.ewi.ds.bankver.bank.IBANVerifier;

import static com.nervousfish.nervousfish.modules.constants.Constants.InputFieldResultCodes.CORRECT_FIELD;
import static com.nervousfish.nervousfish.modules.constants.Constants.InputFieldResultCodes.EMPTY_FIELD;
import static com.nervousfish.nervousfish.modules.constants.Constants.InputFieldResultCodes.INVALID_IBAN;
import static com.nervousfish.nervousfish.modules.constants.Constants.InputFieldResultCodes.TOO_SHORT_FIELD;

/**
 * Helper method for the logical functionality of the {@link CreateProfileActivity}.
 */
final class CreateProfileHelper {

    private static final int MIN_PASSWORD_LENGTH = 6;

    private static final String DEFAULT_KEY_NAME = "NervousFish generated key";
    private static final String EMPTY_STRING = "";

    private final IKeyGenerator keyGenerator;
    private final int alertColor;

    /**
     * Helper class for the create profile {@link android.app.Activity}
     * containing its logical functionality.
     *
     * @param keyGenerator A {@link IKeyGenerator} to generate keys.
     * @param alertColor   The {@link Color} to set as alert for incorrect values.
     */
    CreateProfileHelper(final IKeyGenerator keyGenerator, final int alertColor) {
        Validate.notNull(keyGenerator);
        this.keyGenerator = keyGenerator;
        this.alertColor = alertColor;
    }

    /**
     * Generates a RSAKeyPair and returns it in a List.
     *
     * @return a list with {@link RSAKeyPair}
     */
    List<RSAKeyPair> generateRSAKeyPair() {
        final List<RSAKeyPair> keyPairs = new ArrayList<>();
        keyPairs.add(this.keyGenerator.generateRSAKeyPair(CreateProfileHelper.DEFAULT_KEY_NAME));
        return keyPairs;
    }

    /**
     * Generates a Ed25519KeyPair and returns it in a List.
     *
     * @return a list with {@link Ed25519KeyPair}
     */
    List<Ed25519KeyPair> generateEd25519KeyPair() {
        final List<Ed25519KeyPair> keyPairs = new ArrayList<>();
        keyPairs.add(this.keyGenerator.generateEd25519KeyPair(CreateProfileHelper.DEFAULT_KEY_NAME));
        return keyPairs;
    }

    /**
     * @param input The {@link EditText} to evaluate.
     * @return The resultcode of isvalidname.
     */
    Constants.InputFieldResultCodes validateName(final EditText input) {
        Validate.notNull(input);
        final String name = input.getText().toString();
        if (this.isValidName(name) == CORRECT_FIELD) {
            input.setBackgroundColor(Color.TRANSPARENT);
            return CORRECT_FIELD;
        } else if (this.isValidName(name) == EMPTY_FIELD) {
            input.setBackgroundColor(this.alertColor);
            return EMPTY_FIELD;
        }
        return CORRECT_FIELD;
    }

    /**
     * @param input The {@link EditText} to evaluate.
     * @return The resultcode of validateIban.
     */
    Constants.InputFieldResultCodes validateIban(final EditText input) {
        Validate.notNull(input);
        final String iban = input.getText().toString();
        if (EMPTY_STRING.equals(iban) || IBANVerifier.isValidIBAN(iban)) {
            input.setBackgroundColor(Color.TRANSPARENT);
            return CORRECT_FIELD;
        } else if (!IBANVerifier.isValidIBAN(iban)) {
            input.setBackgroundColor(this.alertColor);
            return INVALID_IBAN;
        }
        return CORRECT_FIELD;
    }

    /**
     * @param input The {@link EditText} to evaluate.
     * @return The result code of the password validation.
     */
    Constants.InputFieldResultCodes validatePassword(final EditText input) {
        Validate.notNull(input);
        final String password = input.getText().toString();
        switch (this.isValidPassword(password)) {
            case CORRECT_FIELD:
                input.setBackgroundColor(Color.TRANSPARENT);
                return CORRECT_FIELD;
            case EMPTY_FIELD:
                input.setBackgroundColor(this.alertColor);
                return EMPTY_FIELD;
            case TOO_SHORT_FIELD:
                input.setBackgroundColor(this.alertColor);
                return TOO_SHORT_FIELD;
            default:
                return CORRECT_FIELD;
        }
    }

    /**
     * @param passwordInput The {@link EditText} of the original password.
     * @param repeatInput   The {@link EditText} of the repeat password.
     * @return True when the password matches the repeated password.
     */
    boolean passwordsEqual(final EditText passwordInput, final EditText repeatInput) {
        Validate.notNull(passwordInput);
        Validate.notNull(repeatInput);
        final String initialPassword = passwordInput.getText().toString();
        final String repeatPassword = repeatInput.getText().toString();
        if (!initialPassword.equals(repeatPassword)) {
            passwordInput.setBackgroundColor(this.alertColor);
            repeatInput.setBackgroundColor(this.alertColor);
            return false;
        }

        passwordInput.setBackgroundColor(Color.TRANSPARENT);
        repeatInput.setBackgroundColor(Color.TRANSPARENT);
        return true;
    }

    /**
     * Will return true if the name is valid. This means
     * that it has at least 1 ASCII character.
     *
     * @param name The name that has been entered.
     * @return a {@link boolean} indicating whether or not the name is valid.
     */
    private Constants.InputFieldResultCodes isValidName(final String name) {
        if (name != null && !name.isEmpty()
                && !name.trim().isEmpty()) {
            return CORRECT_FIELD;
        }
        return EMPTY_FIELD;

    }

    /**
     * Will return true if the name is valid. This means
     * that it has at least 6 ASCII character.
     *
     * @param password The password that has been entered.
     * @return a {@link int} indicating why the password is invalid or if it's valid.
     */
    private Constants.InputFieldResultCodes isValidPassword(final String password) {
        if (password.isEmpty() || password.trim().isEmpty()) {
            return EMPTY_FIELD;
        } else if (password.length() < MIN_PASSWORD_LENGTH) {
            return TOO_SHORT_FIELD;
        } else {
            return CORRECT_FIELD;
        }
    }

}
