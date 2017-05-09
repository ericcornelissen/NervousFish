package com.nervousfish.nervousfish.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.nervousfish.nervousfish.ConstantKeywords;
import com.nervousfish.nervousfish.R;
import com.nervousfish.nervousfish.service_locator.IServiceLocator;

import java.util.ArrayList;
import java.util.List;

/**
 * The ContactActivity shows the contacts information and his public keys.
 */
@SuppressWarnings("PMD.AtLeastOneConstructor")
public final class ContactActivity extends AppCompatActivity {

    private final List<String> keys = new ArrayList<>();
    @SuppressWarnings("PMD.SingularField")
    //In the future we will use this field more than once
    private IServiceLocator serviceLocator;

    /**
     * Creates the new activity, should only be called by Android
     *
     * @param savedInstanceState Don't touch this
     */
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        final Intent intent = getIntent();
        this.serviceLocator = (IServiceLocator) intent.getSerializableExtra(ConstantKeywords.SERVICE_LOCATOR);

        // Example use
        this.serviceLocator.getConstants();

        final ListView lv = (ListView) findViewById(R.id.listView);

        //Retrieve the contacts from the database
        getKeys();

        lv.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, keys));

    }

    /**
     * Temporary method for filling the listview with some keys.
     */
    private void getKeys() {
        keys.add("Gmail");
        keys.add("FTP server");
        keys.add("Web server");
        keys.add("Hotmail");
    }
}
