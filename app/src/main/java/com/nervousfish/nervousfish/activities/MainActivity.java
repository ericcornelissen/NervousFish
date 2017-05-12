package com.nervousfish.nervousfish.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.nervousfish.nervousfish.ConstantKeywords;
import com.nervousfish.nervousfish.R;
import com.nervousfish.nervousfish.data_objects.Contact;
import com.nervousfish.nervousfish.data_objects.SimpleKey;
import com.nervousfish.nervousfish.modules.database.IDatabase;
import com.nervousfish.nervousfish.service_locator.IServiceLocator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

/**
 * The main activity class that shows a list of all people with their public keys
 */
@SuppressWarnings("PMD.AtLeastOneConstructor")
public final class MainActivity extends AppCompatActivity {
    private static final Logger LOGGER = LoggerFactory.getLogger("MainActivity");

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
        LOGGER.info("MainActivity created");

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

        final Intent intent = getIntent();
        serviceLocator = (IServiceLocator) intent.getSerializableExtra(ConstantKeywords.SERVICE_LOCATOR);

        fillDatabaseWithDemoData();

        try {
            lv.setAdapter(new ContactListAdapter(this, serviceLocator.getDatabase().getAllContacts()));
        } catch (final IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Temporarily fill the database with demo data for development.
     */
    private void fillDatabaseWithDemoData() {
        final IDatabase database = serviceLocator.getDatabase();
        try {

            final Contact a = new Contact("Eric", new SimpleKey("jdfs09jdfs09jfs0djfds9jfsd0"));
            final Contact b = new Contact("Stas", new SimpleKey("4ji395j495i34j5934ij534i"));
            final Contact c = new Contact("Joost", new SimpleKey("dnfh4nl4jknlkjnr4j34klnk3j4nl"));
            final Contact d = new Contact("Kilian", new SimpleKey("sdjnefiniwfnfejewjnwnkenfk32"));
            final Contact e = new Contact("Cornel", new SimpleKey("nr23uinr3uin2o3uin23oi4un234ijn"));
            if (!database.getAllContacts().isEmpty()) {
                database.deleteContact(a);
                database.deleteContact(b);
                database.deleteContact(c);
                database.deleteContact(d);
                database.deleteContact(e);
            }
            database.addContact(a);
            database.addContact(b);
            database.addContact(c);
            database.addContact(d);
            database.addContact(e);
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }
}

/**
 * An Adapter which converts a list with contacts into List entries.
 */
final class ContactListAdapter extends ArrayAdapter<Contact> {

    /**
     * Create and initialize a ContactListAdapter.
     *
     * @param context  the Context where the ListView is created
     * @param contacts the list with contacts
     */
    ContactListAdapter(final Context context, final List<Contact> contacts) {
        super(context, 0, contacts);
    }

    /**
     * {@inheritDoc}
     */
    @NonNull
    @Override
    public View getView(final int position, final View convertView, @NonNull final ViewGroup parent) {
        View v = convertView;

        if (v == null) {
            final LayoutInflater vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.contact_list_entry, parent);
        }

        final Contact contact = getItem(position);

        if (contact != null) {
            final TextView name = (TextView) v.findViewById(R.id.name);

            if (name != null) {
                name.setText(contact.getName());
            }
        }

        return v;
    }

}