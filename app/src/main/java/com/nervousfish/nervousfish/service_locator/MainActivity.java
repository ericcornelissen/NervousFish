package com.nervousfish.nervousfish.service_locator;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.nervousfish.nervousfish.R;

import java.util.ArrayList;
import java.util.List;

/**
 * The main activity class that shows a list of all people with their public keys
 */
public class MainActivity extends AppCompatActivity {

    private ListView lv;
    private List<String> contacts = new ArrayList<>();
    private IServiceLocator serviceLocator;
    /**
     * Creates the new activity, should only be called by Android
     *
     * @param savedInstanceState Don't touch this
     */
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.serviceLocator = ServiceLocatorCreator.newInstance();

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

        getSupportActionBar().setDisplayShowTitleEnabled(false);

        lv = (ListView) findViewById(R.id.listView);

        //Retrieve the contacts from the database
        getContacts();

        lv.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, contacts));

    }

    public void getContacts() {
        contacts.add("Eric");
        contacts.add("Kilian");
        contacts.add("Joost");
        contacts.add("Stas");
    }
}
