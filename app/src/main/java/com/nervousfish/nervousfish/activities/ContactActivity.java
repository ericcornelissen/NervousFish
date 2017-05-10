package com.nervousfish.nervousfish.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.nervousfish.nervousfish.ConstantKeywords;
import com.nervousfish.nervousfish.R;
import com.nervousfish.nervousfish.data_objects.Contact;
import com.nervousfish.nervousfish.data_objects.IKey;

import java.util.ArrayList;
import java.util.List;

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
        final IKey key = contact.getPublicKey();
        keys.add(key.getKey());

        final ListView lv = (ListView) this.findViewById(R.id.listView);
        lv.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, keys));
    }

}
