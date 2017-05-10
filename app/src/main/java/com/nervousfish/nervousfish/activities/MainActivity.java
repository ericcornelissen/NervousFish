package com.nervousfish.nervousfish.activities;

import com.nervousfish.nervousfish.data_objects.Contact;

import java.util.ArrayList;
import android.content.Intent;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.nervousfish.nervousfish.ConstantKeywords;
import com.nervousfish.nervousfish.R;
import com.nervousfish.nervousfish.data_objects.SimpleKey;

import java.util.List;

/**
 * The main activity class that shows a list of all people with their public keys
 */
public final class MainActivity extends AppCompatActivity {

    private final List<Contact> contacts = new ArrayList<>();

    /**
     * Creates the new activity, should only be called by Android
     *
     * @param savedInstanceState Don't touch this
     */
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            /**
             * {@inheritDoc}
             */
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

        lv.setAdapter(new ContactListAdapter(this, contacts));
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            /**
             * {@inheritDoc}
             */
            @Override
            public void onItemClick(final AdapterView<?> parent, final View view, final int index, final long id) {
                openContact(index);
            }
        });
    }

    /**
     * Temporary method to open the {@link ContactActivity} for a contact.
     *
     * @param index The index of the contact in {@code this.contacts}.
     */
    private void openContact(final int index) {
        final Intent intent = new Intent(this, ContactActivity.class);
        intent.putExtra(ConstantKeywords.CONTACT, "Average Joe");
        this.startActivity(intent);
    }

    /**
     * Temporary method for filling the listview with some friends.
     */
    private void getContacts() {
        contacts.add(new Contact("Eric", new SimpleKey("4y5uh4334j43j034934j634096j3409j63409")));
        contacts.add(new Contact("Joost", new SimpleKey("vmiadofjaei3jrioejsOJi9oidnicjPShljKDHf")));
        contacts.add(new Contact("Stas", new SimpleKey("soai4ha4iluhak4djnfuiqnu3na;kjhdf9jdo;i")));
        contacts.add(new Contact("Kilian", new SimpleKey("u09srtjs0r9gjs09rjg09egrjeag90rjeg90a")));
        contacts.add(new Contact("Cornel", new SimpleKey("5hu94h4ju64j6234h6l2h46ljk2h34jkl624")));
    }
}


class ContactListAdapter extends ArrayAdapter<Contact> {

    public ContactListAdapter(final Context context, final List<Contact> items) {
        super(context, 0, items);
    }

    @Override
    public View getView(final int position, final View convertView, final ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            final LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.contact_list_entry, null);
        }

        final Contact contact = getItem(position);

        if (contact != null) {
            final TextView name = (TextView) v.findViewById(R.id.name);
            final TextView pubKey = (TextView) v.findViewById(R.id.pubKeySnippet);

            if (name != null) {
                name.setText(contact.getName());
            }

            if (pubKey != null) {
                final String publicKey = contact.getPublicKey().getKey();
                if(publicKey.length() > 30) {
                    final String pubKeySnippet = contact.getPublicKey().getKey().substring(0, 30) + "...";
                    pubKey.setText(pubKeySnippet);
                } else {
                    pubKey.setText(publicKey);
                }
            }
        }

        return v;
    }

}