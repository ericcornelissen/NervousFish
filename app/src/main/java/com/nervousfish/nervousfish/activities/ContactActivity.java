package com.nervousfish.nervousfish.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

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

    private static final Logger LOGGER = LoggerFactory.getLogger("ContactActivity");
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
        super.setContentView(R.layout.activity_contact);
        final Intent intent = this.getIntent();

        serviceLocator = (IServiceLocator) intent.getSerializableExtra(ConstantKeywords.SERVICE_LOCATOR);

        contact = (Contact) intent.getSerializableExtra(ConstantKeywords.CONTACT);
        this.setName(contact.getName());
        this.setKeys(contact.getKeys());

        final ImageButton backButton = (ImageButton) findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(final View v) {
                finish();
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
     * Opens when the 3 dots are clicked.
     *
     * @param v - the View element clicked
     */
    public void showPopupMenu(final View v) {
        final PopupMenu popup = new PopupMenu(this, v);
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(final MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.delete_contact_menu_item) {
                    new SweetAlertDialog(ContactActivity.this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText(getString(R.string.popup_you_sure))
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
                            } catch (final IllegalArgumentException e) {
                                LOGGER.error("IllegalArgumentException while deleting contact in ContactActivity");
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
                            } catch (final IOException e) {
                                LOGGER.error("IOException while deleting contact in ContactActivity");
                                sDialog .setTitleText("Something went wrong")
                                        .setContentText(("There went something wrong deleting this contact, "
                                                + "please try again."))
                                        .setConfirmText("OK")
                                        .setConfirmClickListener(null)
                                        .changeAlertType(SweetAlertDialog.ERROR_TYPE);
                            }
                            }
                        })
                        .show();
                    return true;
                } else if (menuItem.getItemId() == R.id.edit_contact_menu_iten) {
                    final Intent intent = new Intent(ContactActivity.this, ChangeContactActivity.class);
                    intent.putExtra(ConstantKeywords.SERVICE_LOCATOR, serviceLocator);
                    intent.putExtra(ConstantKeywords.CONTACT, contact);
                    ContactActivity.this.startActivityForResult(intent, RESULT_FIRST_USER);
                    return true;
                } else {
                    return false;
                }
            }
        });
        final MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.edit_contact_menu, popup.getMenu());
        popup.show();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_FIRST_USER) {
            setName(((Contact) data.getSerializableExtra(ConstantKeywords.CONTACT)).getName());
        }
    }

}
