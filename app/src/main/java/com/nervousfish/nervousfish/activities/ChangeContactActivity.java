package com.nervousfish.nervousfish.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import com.nervousfish.nervousfish.ConstantKeywords;
import com.nervousfish.nervousfish.R;
import com.nervousfish.nervousfish.data_objects.Contact;
import com.nervousfish.nervousfish.data_objects.IKey;
import com.nervousfish.nervousfish.service_locator.IServiceLocator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * The ContactActivity shows the contacts information and his public keys.
 */
public final class ChangeContactActivity extends AppCompatActivity {

    private static final Logger LOGGER = LoggerFactory.getLogger("ChangeContactActivity");
    private IServiceLocator serviceLocator;
    private Contact contact;

    /**
     * Creates the new activity, should only be called by Android
     *
     * @param savedInstanceState The saved state of the instance.
     */
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_change_contact);
        final Intent intent = this.getIntent();

        serviceLocator = (IServiceLocator) intent.getSerializableExtra(ConstantKeywords.SERVICE_LOCATOR);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        contact = (Contact) intent.getSerializableExtra(ConstantKeywords.CONTACT);
        this.setName(contact.getName());
        this.setKeys(contact.getKeys());

        final ImageButton backButton = (ImageButton) findViewById(R.id.backButtonChange);
        backButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(final View v) {
                final EditText editText = (EditText) findViewById(R.id.edit_contact_name);
                if (editText.getText().toString().equals(contact.getName())) {
                    finish();
                } else {
                    new SweetAlertDialog(ChangeContactActivity.this, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText(getString(R.string.popup_you_sure))
                            .setContentText(getString(R.string.go_back_changes_lost))
                            .setCancelText(getString(R.string.cancel))
                            .setConfirmText(getString(R.string.yes_go_back))
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(final SweetAlertDialog sDialog) {
                                    sDialog.dismiss();
                                    finish();
                                }
                            })
                            .show();
                }
            }
        });

    }

    /**
     * Set the name of the {@link Contact} to the {@link ContactActivity}.
     *
     * @param name The name.
     */
    private void setName(final String name) {
        final EditText tv = (EditText) this.findViewById(R.id.edit_contact_name);
        tv.setText(name);
    }

    /**
     * Set the keys of the {@link Contact} to the {@link ContactActivity}.
     *
     * @param keys A {@link Collection} of {@link IKey}s.
     */
    private void setKeys(final Collection<IKey> keys) {
        final List<String> keyNames = new ArrayList<>();
        for (final IKey key : keys) {
            keyNames.add(key.getName());
        }

        final ListView lv = (ListView) this.findViewById(R.id.listView);
        lv.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, keyNames));
    }

    /**
     * When the save button is clicked this method is called.
     * It saves the new contact name.
     *
     * @param v - the view clicked on
     */
    public void saveContact(final View v) {
        //Dont show keyboard anymore
        final InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

        final EditText editText = (EditText) findViewById(R.id.edit_contact_name);
        if(isValidName(editText.getText().toString())) {
            //Update contact
            try {
                final Contact newContact = new Contact(editText.getText().toString(), contact.getKeys());
                if (!contact.equals(newContact)) {
                    serviceLocator.getDatabase().updateContact(contact, newContact);
                    contact = newContact;
                }
            } catch (final IOException e) {
                LOGGER.error("IOException while updating contactname");
            }

            setResult(RESULT_FIRST_USER,
                    new Intent().putExtra(ConstantKeywords.CONTACT, contact));
            finish();
        } else {
            new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText(getString(R.string.invalid_name))
                    .setContentText(getString(R.string.invalid_name_explanation))
                    .setConfirmText(getString(R.string.dialog_ok))
                    .setConfirmClickListener(null)
                    .show();
        }
    }

    /**
     * Will return true if the name is valid. This means
     * that it has at least 1 ASCII character.
     *
     * @param name - the name that has been entered
     * @return a {@link boolean} telling if the name is valid or not
     */
    private boolean isValidName(final String name) {
        return name != null && !name.isEmpty() && !name.trim().isEmpty();
    }

}
