package com.nervousfish.nervousfish.activities;

import android.app.Activity;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.nervousfish.nervousfish.R;

import java.util.Objects;

import static android.content.Context.INPUT_METHOD_SERVICE;

/**
 * A helper method for {@link Activity}s that want to use a custom keyboard.
 */
final class CustomKeyboardHelper {

    private final Activity activity;
    private final KeyboardView keyboardView;

    /**
     * Creates a new {@link CustomKeyboardHelper} which inserts a custom keyboard into an activity.
     *
     * @param activity The {@link Activity} where the custom keyboard should be used.
     */
    CustomKeyboardHelper(final Activity activity) {
        this.activity = activity;
        this.keyboardView = (KeyboardView) activity.findViewById(R.id.keyboard_view);

        // Create custom keyboard layout
        final Keyboard keyboard = new Keyboard(activity, R.xml.qwerty);
        this.keyboardView.setKeyboard(keyboard);
        this.keyboardView.setPreviewEnabled(false);
        this.keyboardView.setOnKeyboardActionListener(new CustomKeyboardHelper.OnCustomKeyboardActionListener(activity, keyboardView));

        // Disable the default keyboard
        final Window window = this.activity.getWindow();
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    /**
     * Add a new input that should use the custom keyboard.
     *
     * @param editText The {@link EditText} that should use the keyboard.
     */
    void addInput(final EditText editText) {
        editText.setOnFocusChangeListener(this::focusListener);
        editText.setOnClickListener(this::clickListener);
        editText.setCustomSelectionActionModeCallback(new CustomKeyboardHelper.EditPasswordSelectionCallback());
    }

    /**
     * Find out if the custom keyboard is visible.
     *
     * @return A {@code boolean} indicating whether the custom keyboard is visible
     */
    boolean isVisible() {
        return this.isCustomKeyboardVisible();
    }

    /**
     * Hide the custom keyboard.
     */
    void hide() {
        this.hideCustomKeyboard();
    }

    private void focusListener(final View view, final Boolean hasFocus) {
        if (hasFocus) {
            this.clickListener(view);
        } else {
            this.hideCustomKeyboard();
        }
    }

    private void clickListener(final View view) {
        this.keyboardView.setVisibility(View.VISIBLE);
        this.keyboardView.setEnabled(true);

        // Hide the real keyboard
        final InputMethodManager inputMethodManager = (InputMethodManager) this.activity.getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void hideCustomKeyboard() {
        this.keyboardView.setVisibility(View.GONE);
        this.keyboardView.setEnabled(false);
    }

    private boolean isCustomKeyboardVisible() {
        return this.keyboardView.getVisibility() == View.VISIBLE;
    }

    private static final class EditPasswordSelectionCallback implements ActionMode.Callback {

        @Override
        public boolean onCreateActionMode(final ActionMode mode, final Menu menu) {
            return false;
        }

        @Override
        public boolean onPrepareActionMode(final ActionMode mode, final Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(final ActionMode mode, final MenuItem item) {
            return false;
        }

        @Override
        public void onDestroyActionMode(final ActionMode mode) {
            // Unused
        }

    }

    private static final class OnCustomKeyboardActionListener implements KeyboardView.OnKeyboardActionListener {

        private final Activity activity;

        private final KeyboardView keyboardView;

        private boolean isShifted;

        OnCustomKeyboardActionListener(final Activity activity, final KeyboardView keyboardView) {
            this.activity = activity;
            this.keyboardView = keyboardView;
        }

        @Override
        public void onPress(final int primaryCode) {
            // Unused
        }

        @Override
        public void onRelease(final int primaryCode) {
            // Unused
        }

        @Override
        public void onKey(final int primaryCode, final int[] keyCodes) {
            final View focusCurrent = this.activity.getWindow().getCurrentFocus();
            if (focusCurrent == null) {
                return;
            }
            final Class<?> focusClass = focusCurrent.getClass();
            if (!Objects.equals(focusClass, EditText.class) && !Objects.equals(focusCurrent.getClass(), AppCompatEditText.class)) {
                return;
            }

            final EditText edittext = (EditText) focusCurrent;
            final Editable editable = edittext.getText();
            final int start = edittext.getSelectionStart();

            switch (primaryCode) {
                case Keyboard.KEYCODE_CANCEL:
                    final int length = editable.length();
                    if (length > 0) {
                        editable.delete(length - 1, length);
                    }
                    break;
                case Keyboard.KEYCODE_SHIFT:
                    isShifted = !isShifted;
                    keyboardView.setShifted(isShifted);
                    keyboardView.invalidateAllKeys();
                    break;
                default:
                    if (isShifted) {
                        isShifted = false;
                        keyboardView.setShifted(false);
                        keyboardView.invalidateAllKeys();
                        editable.insert(start, Character.toString(Character.toUpperCase((char) primaryCode)));
                    } else {
                        editable.insert(start, Character.toString((char) primaryCode));
                    }
                    break;
            }
        }

        @Override
        public void onText(final CharSequence text) {
            // Unused
        }

        @Override
        public void swipeLeft() {
            // Unused
        }

        @Override
        public void swipeRight() {
            // Unused
        }

        @Override
        public void swipeDown() {
            // Unused
        }

        @Override
        public void swipeUp() {
            // Unused
        }

    }

}
