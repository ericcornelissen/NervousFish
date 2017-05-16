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
import android.widget.TextView;
import android.widget.ViewSwitcher;

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
public final class ContactActivity extends AppCompatActivity {

    private static final Logger LOGGER = LoggerFactory.getLogger("LoginActivity");
    private IServiceLocator serviceLocator;

    /**
     * Creates the new activity, should only be called by Android
     *
     * @param savedInstanceState The saved state of the instance.
     */
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_contact);
        final Intent intent = this.getIntent();

        serviceLocator = (IServiceLocator) intent.getSerializableExtra(ConstantKeywords.SERVICE_LOCATOR);

        final Contact contact = (Contact) intent.getSerializableExtra(ConstantKeywords.CONTACT);
        this.setName(contact.getName());
        this.setKeys(contact.getKeys());

        final ImageButton backButton = (ImageButton) findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(final View v) {
                finish();
            }
        });

        final ImageButton deleteButton = (ImageButton) findViewById(R.id.deleteButton);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(final View v) {
                new SweetAlertDialog(ContactActivity.this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText(getString(R.string.delete_popup_you_sure))
                        .setContentText(getString(R.string.delete_popup_no_recovery))
                        .setCancelText(getString(R.string.cancel))
                        .setConfirmText(getString(R.string.yes_delete))
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(final SweetAlertDialog sDialog) {
                                try {
                                    serviceLocator.getDatabase().deleteContact(contact.getName());
                                    sDialog .setTitleText("Deleted!")
                                            .setContentText("The contact has been deleted!")
                                            .setConfirmText("OK")
                                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                    @Override
                                                    public void onClick(final SweetAlertDialog sDialog) {
                                                        sDialog.dismiss();
                                                        finish();
                                                    }
                                                })
                                            .showCancelButton(false)
                                            .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                                } catch (IllegalArgumentException e) {
                                    sDialog .setTitleText("The contact doesn't exist")
                                            .setContentText("It looks like the contact was already deleted.")
                                            .setConfirmText("OK")
                                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                @Override
                                                public void onClick(final SweetAlertDialog sDialog) {
                                                    sDialog.dismiss();
                                                    finish();
                                                }
                                            })
                                            .showCancelButton(false)
                                            .changeAlertType(SweetAlertDialog.WARNING_TYPE);
                                } catch (IOException e) {
                                    sDialog .setTitleText("Something went wrong")
                                            .setContentText(("There went something wrong deleting this contact, " +
                                                    "please try again."))
                                            .setConfirmText("OK")
                                            .setConfirmClickListener(null)
                                            .changeAlertType(SweetAlertDialog.ERROR_TYPE);
                                }
                            }
                        })
                        .show();
            }
        });
    }

    /**
     * Set the name of the {@link Contact} to the {@link ContactActivity}.
     *
     * @param name The name.
     */
    private void setName(final String name) {
        final TextView tv = (TextView) this.findViewById(R.id.contact_name);
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
     * Gets called when there is clicked on the contact name.
     * After running this method, it is possible to change the name.
     *
     * @param v - the view clicked on
     */
    public void contactNameClicked(final View v) {
        ViewSwitcher switcher = (ViewSwitcher) findViewById(R.id.switch_name_field);
        TextView contactName = (TextView) findViewById(R.id.contact_name);
        //Switch to edittext
        switcher.showNext();
        EditText editText = (EditText) findViewById(R.id.edit_contact_name);
        editText.setText(contactName.getText());
        editText.requestFocus();
        //Show keyboard
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);

        ImageButton saveButton = (ImageButton) findViewById(R.id.save_button);
        saveButton.setVisibility(View.VISIBLE);
    }

    /**
     * When the save button is clicked this method is called.
     * It saves the new contact name.
     *
     * @param v - the view clicked on
     */
    public void saveNewContactName(final View v) {
        ViewSwitcher switcher = (ViewSwitcher) findViewById(R.id.switch_name_field);
        TextView contactName = (TextView) switcher.getNextView();
        EditText editText = (EditText) findViewById(R.id.edit_contact_name);
        switcher.showNext();
        //Update contact
        try {
            final Contact oldContact = serviceLocator.getDatabase().getContactWithName(contactName.getText().toString());
            final Contact newContact = new Contact(editText.getText().toString(), oldContact.getKeys());
            serviceLocator.getDatabase().updateContact(oldContact, newContact);
        } catch (IOException e) {
            LOGGER.error("IOException while updating contactname");
        }
        //Update text on screen and set savebutton to gone.
        contactName.setText(editText.getText());
        ImageButton saveButton = (ImageButton) findViewById(R.id.save_button);
        saveButton.setVisibility(View.GONE);
        //Dont show keyboard
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

}
