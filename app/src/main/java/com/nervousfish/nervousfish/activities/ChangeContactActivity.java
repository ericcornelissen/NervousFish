package com.nervousfish.nervousfish.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;

import com.nervousfish.nervousfish.ConstantKeywords;
import com.nervousfish.nervousfish.R;
import com.nervousfish.nervousfish.data_objects.Contact;
import com.nervousfish.nervousfish.service_locator.IServiceLocator;
import com.nervousfish.nervousfish.service_locator.NervousFish;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import cn.pedant.SweetAlert.SweetAlertDialog;
import nl.tudelft.ewi.ds.bankver.IBAN;
import nl.tudelft.ewi.ds.bankver.bank.IBANVerifier;

/**
 * An {@link Activity} that allows the user to change attributes of contacts.
 */
public final class ChangeContactActivity extends AppCompatActivity {

    private static final Logger LOGGER = LoggerFactory.getLogger("ChangeContactActivity");
    private static final String EMPTY_STRING = "";

    private IServiceLocator serviceLocator;
    private Contact contact;

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_change_contact);
        this.serviceLocator = NervousFish.getServiceLocator();

        if (this.getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        final Intent intent = this.getIntent();
        this.contact = (Contact) intent.getSerializableExtra(ConstantKeywords.CONTACT);
        ListviewActivityHelper.setText(this, this.contact.getName(), R.id.edit_contact_name_input);
        ListviewActivityHelper.setKeys(this, this.contact.getKeys(), R.id.list_view_edit_contact);
        ListviewActivityHelper.setText(this, this.contact.getIbanAsString(), R.id.contact_page_change_iban);

        final ImageButton backButton = (ImageButton) this.findViewById(R.id.back_button_change);
        backButton.setOnClickListener(new BackButtonListener());

        LOGGER.info("Activity created");
    }

    /**
     * When the save button is clicked this method is called.
     * It saves the new contact name.
     *
     * @param v The view clicked on
     */
    public void saveContact(final View v) {
        // Don't show keyboard anymore
        final InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

        final EditText editTextName = (EditText) findViewById(R.id.edit_contact_name_input);
        final EditText editTextIBAN = (EditText) findViewById(R.id.contact_page_change_iban);
        String ibanString = editTextIBAN.getText().toString();
        final boolean validName = isValidName(editTextName.getText().toString());
        boolean validIban = IBANVerifier.isValidIBAN(ibanString);
        if(!validIban && (ibanString.equals(EMPTY_STRING) || ibanString.equals("-"))) {
            validIban = true;
            ibanString = EMPTY_STRING;
        }
        if (validName && validIban) {
            //Update contact
            try {
                Contact newContact = new Contact(editTextName.getText().toString(), contact.getKeys());
                if (ibanString.equals(EMPTY_STRING)) {
                    newContact = new Contact(editTextName.getText().toString(), contact.getKeys(),
                            new IBAN(ibanString));
                }
                if (!contact.equals(newContact)) {
                    serviceLocator.getDatabase().updateContact(contact, newContact);
                    contact = newContact;
                }
            } catch (final IOException e) {
                LOGGER.error("IOException while updating contactname", e);
            }

            setResult(RESULT_FIRST_USER,
                    new Intent().putExtra(ConstantKeywords.CONTACT, contact));
            finish();
        } else if(!validName) {
            new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText(getString(R.string.invalid_name))
                    .setContentText(getString(R.string.invalid_name_explanation))
                    .setConfirmText(getString(R.string.dialog_ok))
                    .setConfirmClickListener(null)
                    .show();
        } else if(!validIban) {
            new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText(getString(R.string.invalid_iban))
                    .setContentText(getString(R.string.invalid_iban_explanation))
                    .setConfirmText(getString(R.string.dialog_ok))
                    .setConfirmClickListener(null)
                    .show();
        }
    }

    /**
     * Will return true if the name is valid. This means
     * that it has at least 1 ASCII character.
     *
     * @param name The name that has been entered
     * @return a {@link boolean} telling if the name is valid or not
     */
    private boolean isValidName(final String name) {
        return name != null && !name.isEmpty() && !name.trim().isEmpty();
    }

    private final class BackButtonListener implements View.OnClickListener {

        /**
         * {@inheritDoc}
         */
        @Override
        public void onClick(final View v) {
            final EditText editTextName = (EditText) findViewById(R.id.edit_contact_name_input);
            final EditText editTextIBAN = (EditText) findViewById(R.id.contact_page_change_iban);
            if (editTextName.getText().toString().equals(contact.getName())
                    && (editTextIBAN.getText().toString().equals(contact.getIban())
                    || editTextIBAN.getText().toString().equals(ChangeContactActivity.this.getString(R.string.dash)))) {
                finish();
            } else {
                new SweetAlertDialog(ChangeContactActivity.this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText(getString(R.string.are_you_sure))
                        .setContentText(getString(R.string.go_back_changes_lost))
                        .setCancelText(getString(R.string.cancel))
                        .setConfirmText(getString(R.string.yes_go_back))
                        .setConfirmClickListener(new DiscardChangesClickListener())
                        .show();
            }
        }

    }

    private final class DiscardChangesClickListener implements SweetAlertDialog.OnSweetClickListener {

        /**
         * {@inheritDoc}
         */
        @Override
        public void onClick(final SweetAlertDialog sweetAlertDialog) {
            sweetAlertDialog.dismiss();
            finish();
        }

    }

}
