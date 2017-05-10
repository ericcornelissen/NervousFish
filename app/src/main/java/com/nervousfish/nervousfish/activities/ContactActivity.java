package com.nervousfish.nervousfish.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;

import com.nervousfish.nervousfish.ConstantKeywords;
import com.nervousfish.nervousfish.R;
import com.nervousfish.nervousfish.service_locator.IServiceLocator;

import java.util.ArrayList;
import java.util.List;

/**
 * The ContactActivity shows the contacts information and his public keys.
 */
public final class ContactActivity extends AppCompatActivity {

    private final List<String> keys = new ArrayList<>();

    /**
     * Creates the new activity, should only be called by Android
     *
     * @param savedInstanceState Don't touch this
     */
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        final ListView lv = (ListView) findViewById(R.id.listView);
        final Intent intentContactActivity = getIntent();

        final ImageButton button = (ImageButton) findViewById(R.id.backButton);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                final Activity previousActivity =
                        (Activity) intentContactActivity.getSerializableExtra(ConstantKeywords.PREVIOUS_ACTIVITY);
                final Intent intent = new Intent(ContactActivity.this, previousActivity.getClass());
                intent.putExtra(ConstantKeywords.SERVICE_LOCATOR,
                        intentContactActivity.getSerializableExtra(ConstantKeywords.SERVICE_LOCATOR));
                startActivity(intent);
            }
        });

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
