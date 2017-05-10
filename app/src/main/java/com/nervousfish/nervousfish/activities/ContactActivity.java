package com.nervousfish.nervousfish.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.nervousfish.nervousfish.ConstantKeywords;
import com.nervousfish.nervousfish.R;

import java.util.ArrayList;
import java.util.List;

/**
 * The ContactActivity shows the contacts information and his public keys.
 */
public final class ContactActivity extends AppCompatActivity {

    /**
     * Creates the new activity, should only be called by Android
     *
     * @param savedInstanceState Don't touch this
     */
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_contact);
        final Intent intent = this.getIntent();

        this.setName(intent);
        this.setKeys();
    }

    /**
     * Set the name of the {@link com.nervousfish.nervousfish.data_objects.Contact} to the
     * {@link ContactActivity}.
     *
     * @param intent The intent with which the Activity was called.
     */
    private void setName(final Intent intent) {
        final String name = (String) intent.getSerializableExtra(ConstantKeywords.CONTACT);
        final TextView tv = (TextView) this.findViewById(R.id.contact_name);
        tv.setText(name);
    }

    /**
     * Set the keys of the {@link com.nervousfish.nervousfish.data_objects.Contact} to the
     * {@link ContactActivity}.
     */
    private void setKeys() {
        // Temporary solution to having some keys here...
        final List<String> keys = new ArrayList<>();
        keys.add("Gmail");
        keys.add("FTP server");
        keys.add("Web server");
        keys.add("Hotmail");

        final ListView lv = (ListView) this.findViewById(R.id.listView);
        lv.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, keys));
    }

}
