package com.nervousfish.nervousfish.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.nervousfish.nervousfish.ConstantKeywords;
import com.nervousfish.nervousfish.R;
import com.nervousfish.nervousfish.data_objects.Contact;
import com.nervousfish.nervousfish.data_objects.IKey;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * The ContactActivity shows the contacts information and his public keys.
 */
public final class ContactActivity extends AppCompatActivity {

    /**
     * Creates the new activity, should only be called by Android
     *
     * @param savedInstanceState The saved state of the instance.
     */
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_contact);
        final Intent intent = this.getIntent();

        final Contact contact = (Contact) intent.getSerializableExtra(ConstantKeywords.CONTACT);
        this.setName(contact);
        this.setKeys(contact);

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
                                //TODO: delete a contact from the database
                                sDialog.dismiss();
                                finish();
                            }
                        })
                        .show();
            }
        });
    }

    /**
     * Set the name of the {@link Contact} to the {@link ContactActivity}.
     *
     * @param contact The {@link Contact}.
     */
    private void setName(final Contact contact) {
        final TextView tv = (TextView) this.findViewById(R.id.contact_name);
        tv.setText(contact.getName());
    }

    /**
     * Set the keys of the {@link Contact} to the {@link ContactActivity}.
     *
     * @param contact The {@link Contact}.
     */
    private void setKeys(final Contact contact) {
        final List<String> keys = new ArrayList<>();
        for (final IKey key : contact.getKeys()) {
            keys.add(key.getKey());
        }

        final ListView lv = (ListView) this.findViewById(R.id.listView);
        lv.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, keys));
    }

}
