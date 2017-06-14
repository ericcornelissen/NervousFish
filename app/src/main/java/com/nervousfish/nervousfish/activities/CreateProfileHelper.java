package com.nervousfish.nervousfish.activities;

import android.graphics.Color;
import android.widget.EditText;

import com.nervousfish.nervousfish.data_objects.IKey;
import com.nervousfish.nervousfish.data_objects.KeyPair;
import com.nervousfish.nervousfish.data_objects.SimpleKey;
import com.nervousfish.nervousfish.modules.cryptography.IKeyGenerator;

/**
 * Helper method for the logical functionality of the {@link CreateProfileActivity}.
 */
class CreateProfileHelper {

    private static final int MIN_PASSWORD_LENGTH = 6;
    private static final int PASSWORD_TOO_SHORT = 100;
    private static final int PASSWORD_EMPTY = 101;
    private static final int PASSWORD_GOOD = 102;
    private static final String DEFAULT_KEY_NAME = "NervousFish generated key";

    private final IKeyGenerator keyGenerator;
    private final int alertColor;

    /**
     * Helper class for the create profile {@link android.app.Activity}
     * containing its logical functionality.
     *
     * @param keyGenerator A {@link IKeyGenerator} to generate keys.
     * @param alertColor The {@link Color} to set as alert for incorrect values.
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
    KeyPair generateKeyPair(final IKey.Types keyType) {
        switch (keyType) {
            case RSA:
                return this.keyGenerator.generateRSAKeyPair(CreateProfileHelper.DEFAULT_KEY_NAME);
            case Simple:
                final IKey publicKey = new SimpleKey("public", "foo");
                final IKey privateKey = new SimpleKey("private", "bar");
                return new KeyPair(CreateProfileHelper.DEFAULT_KEY_NAME, publicKey, privateKey);
            default:
                throw new IllegalArgumentException("The selected key is not implemented");
        }
    }

    /**
     * @param input The {@link EditText} to evaluate.
     * @return True when the name is valid.
     */
    boolean validateName(final EditText input) {
        final String name = input.getText().toString();
        if (this.isValidName(name)) {
            input.setBackgroundColor(Color.TRANSPARENT);
        } else {
            input.setBackgroundColor(this.alertColor);
            return false;
        }

        return true;
    }

    /**
     * @param input The {@link EditText} to evaluate.
     * @return True when the password is valid.
     */
    boolean validatePassword(final EditText input) {
        final String password = input.getText().toString();
        if (this.isValidPassword(password)) {
            input.setBackgroundColor(Color.TRANSPARENT);
        } else {
            input.setBackgroundColor(this.alertColor);
            return false;
        }

        return true;
    }

    /**
     * @param passwordInput The {@link EditText} of the original password.
     * @param repeatInput The {@link EditText} of the repeat password.
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
     * @return a {@link int} indicating why the password is invalid or if it's valid.
     */
    private int isValidPassword(final String password) {
        if (password.isEmpty() || password.trim().isEmpty() || password == null) {
            return PASSWORD_EMPTY;
        } else if (password.length() >= MIN_PASSWORD_LENGTH) {
            return PASSWORD_TOO_SHORT;
        } else {
            return PASSWORD_GOOD;
        }
    }

}
