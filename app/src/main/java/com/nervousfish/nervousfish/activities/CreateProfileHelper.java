package com.nervousfish.nervousfish.activities;

import android.graphics.Color;
import android.widget.EditText;

import com.nervousfish.nervousfish.data_objects.IKey;
import com.nervousfish.nervousfish.data_objects.KeyPair;
import com.nervousfish.nervousfish.modules.cryptography.IKeyGenerator;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Helper method for the logical functionality of the {@link CreateProfileActivity}.
 */
final class CreateProfileHelper {

    private static final int MIN_PASSWORD_LENGTH = 6;
    private static final int GOOD_FIELD = 100;
    private static final int EMPTY_FIELD = 101;
    private static final int TOO_SHORT_FIELD = 102;

    private static final String DEFAULT_KEY_NAME = "NervousFish generated key";

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
        this.keyGenerator = keyGenerator;
        this.alertColor = alertColor;
    }

    /**
     * Generates a KeyPair based on the type selected.
     *
     * @param keyType The type of key to generate.
     * @return a {@link KeyPair} with the key type selected
     */
    Collection<KeyPair> generateKeyPairs(final IKey.Types keyType) {
        final Collection<KeyPair> keyPairs = new ArrayList<>();
        switch (keyType) {
            case RSA:
                keyPairs.add(this.keyGenerator.generateRSAKeyPair(CreateProfileHelper.DEFAULT_KEY_NAME));
                break;
            case Ed25519:
                keyPairs.add(this.keyGenerator.generateEd25519KeyPair(CreateProfileHelper.DEFAULT_KEY_NAME));
                break;
            default:
                throw new IllegalArgumentException("The selected key is not implemented");
        }

        return keyPairs;
    }

    /**
     * @param input The {@link EditText} to evaluate.
     * @return The resultcode of isvalidname.
     */
    int validateName(final EditText input) {
        final String name = input.getText().toString();
        if (this.isValidName(name) == GOOD_FIELD) {
            input.setBackgroundColor(Color.TRANSPARENT);
            return GOOD_FIELD;
        } else if (this.isValidName(name) == EMPTY_FIELD) {
            input.setBackgroundColor(this.alertColor);
            return EMPTY_FIELD;
        }
        return GOOD_FIELD;
    }

    /**
     * @param input The {@link EditText} to evaluate.
     * @return The result code of the password validation.
     */
    int validatePassword(final EditText input) {
        final String password = input.getText().toString();
        switch (this.isValidPassword(password)) {
            case GOOD_FIELD:
                input.setBackgroundColor(Color.TRANSPARENT);
                return GOOD_FIELD;
            case EMPTY_FIELD:
                input.setBackgroundColor(this.alertColor);
                return EMPTY_FIELD;
            case TOO_SHORT_FIELD:
                input.setBackgroundColor(this.alertColor);
                return TOO_SHORT_FIELD;
            default:
                return GOOD_FIELD;
        }
    }

    /**
     * @param passwordInput The {@link EditText} of the original password.
     * @param repeatInput   The {@link EditText} of the repeat password.
     * @return True when the password matches the repeated password.
     */
    boolean passwordsEqual(final EditText passwordInput, final EditText repeatInput) {
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
    private int isValidName(final String name) {
        if (name != null && !name.isEmpty()
                && !name.trim().isEmpty()) {
            return GOOD_FIELD;
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
    private int isValidPassword(final String password) {
        if (password.isEmpty() || password.trim().isEmpty()) {
            return EMPTY_FIELD;
        } else if (password.length() < MIN_PASSWORD_LENGTH) {
            return TOO_SHORT_FIELD;
        } else {
            return GOOD_FIELD;
        }
    }

}
