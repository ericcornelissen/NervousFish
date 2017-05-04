package com.nervousfish.nervousfish.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.nervousfish.nervousfish.ConstantKeywords;
import com.nervousfish.nervousfish.R;
import com.nervousfish.nervousfish.service_locator.IServiceLocator;

import java.util.ArrayList;
import java.util.List;

/**
 * The main activity class that shows a list of all people with their public keys
 */
@SuppressWarnings("PMD.AtLeastOneConstructor")
public final class MainActivity extends AppCompatActivity {

    private final List<String> contacts = new ArrayList<>();

    /**
     * Creates the new activity, should only be called by Android
     *
     * @param savedInstanceState Don't touch this
     */
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Intent intent = getIntent();
        final IServiceLocator serviceLocator = (IServiceLocator) intent.getSerializableExtra(ConstantKeywords.SERVICE_LOCATOR);

        // Example use
        //serviceLocator.getConstants();

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {

            @SuppressWarnings("PMD.MethodCommentRequirement")
            @Override
            public void onClick(final View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        final ListView lv = (ListView) findViewById(R.id.listView);

        //Retrieve the contacts from the database
        getContacts();

        lv.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, contacts));

    }

    /**
     * Temporary method for filling the listview with some friends.
     */
    private void getContacts() {
        contacts.add("Eric");
        contacts.add("Kilian");
        contacts.add("Joost");
        contacts.add("Stas");
    }
}
