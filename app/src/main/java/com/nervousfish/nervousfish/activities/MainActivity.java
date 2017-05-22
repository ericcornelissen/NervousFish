package com.nervousfish.nervousfish.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.nervousfish.nervousfish.ConstantKeywords;
import com.nervousfish.nervousfish.R;
import com.nervousfish.nervousfish.data_objects.Contact;
import com.nervousfish.nervousfish.data_objects.IKey;
import com.nervousfish.nervousfish.data_objects.SimpleKey;
import com.nervousfish.nervousfish.modules.database.IDatabase;
import com.nervousfish.nervousfish.service_locator.IServiceLocator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The main activity class that shows a list of all people with their public keys
 */
@SuppressWarnings({"PMD.ExcessiveImports","PMD.TooFewBranchesForASwitchStatement"})
public final class MainActivity extends AppCompatActivity {

    private static final Logger LOGGER = LoggerFactory.getLogger("MainActivity");
    private static final int NUMBER_OF_SORTING_MODES = 2;
    private static final int SORT_BY_NAME = 0;
    private static final int SORT_BY_KEY_TYPE = 1;

    private IServiceLocator serviceLocator;
    private List<Contact> contacts;
    private Integer currentSorting = 0;


    final private Comparator<Contact> nameSorter = new Comparator<Contact>() {
        @Override
        public int compare(final Contact o1,final Contact o2) {
            return o1.getName().compareTo(o2.getName());
        }
    };


    /**
     * Creates the new activity, should only be called by Android
     *
     * @param savedInstanceState Don't touch this
     */
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Intent intent = getIntent();
        this.serviceLocator = (IServiceLocator) intent.getSerializableExtra(ConstantKeywords.SERVICE_LOCATOR);
        this.setContentView(R.layout.activity_main);

        try {
            this.contacts = serviceLocator.getDatabase().getAllContacts();
        } catch (final IOException e) {
            e.printStackTrace();
        }
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        this.setSupportActionBar(toolbar);

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {

            /**
             * {@inheritDoc}
             */
            @Override
            public void onClick(final View view) {
                LOGGER.info("Bluetooth button clicked");
                startBluetoothPairing();
            }

        });

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        sortOnName();

        final FloatingActionButton fab2 = (FloatingActionButton) findViewById(R.id.fab2);
        fab2.setOnClickListener(new View.OnClickListener() {

            /**
             * {@inheritDoc}
             */
            @Override
            public void onClick(final View view) {
                switchSorting();
            }

        });

        LOGGER.info("MainActivity created");
    }

    /**
     * Temporary method to open the {@link ContactActivity} for a contact.
     *
     * @param index The index of the contact in {@code this.contacts}.
     */
    private void openContact(final int index) {
        final Intent intent = new Intent(this, ContactActivity.class);
        intent.putExtra(ConstantKeywords.SERVICE_LOCATOR, this.serviceLocator);
        intent.putExtra(ConstantKeywords.CONTACT, this.contacts.get(index));
        this.startActivity(intent);
    }

    private void startBluetoothPairing() {
        final Intent intent = new Intent(this, BluetoothConnectionActivity.class);
        intent.putExtra(ConstantKeywords.SERVICE_LOCATOR, serviceLocator);
        startActivity(intent);
    }

    /**
     * Temporarily fill the database with demo data for development.
     * Checkstyle is disabled, because this method is only temporarily
     */
    @SuppressWarnings("checkstyle:multipleStringLiterals")
    private void fillDatabaseWithDemoData() throws IOException {
        final IDatabase database = this.serviceLocator.getDatabase();
        final Collection<IKey> keys = new ArrayList<>();
        keys.add(new SimpleKey("Webmail", "jdfs09jdfs09jfs0djfds9jfsd0"));
        keys.add(new SimpleKey("Webserver", "jasdgoijoiahl328hg09asdf322"));
        final Contact a = new Contact("Eric", keys);
        final Contact b = new Contact("Stas", new SimpleKey("FTP", "4ji395j495i34j5934ij534i"));
        final Contact c = new Contact("Joost", new SimpleKey("Webserver", "dnfh4nl4jknlkjnr4j34klnk3j4nl"));
        final Contact d = new Contact("Kilian", new SimpleKey("Webmail", "sdjnefiniwfnfejewjnwnkenfk32"));
        final Contact e = new Contact("Cornel", new SimpleKey("Awesomeness", "nr23uinr3uin2o3uin23oi4un234ijn"));

        final List<Contact> contacts = database.getAllContacts();
        for (final Contact contact: contacts) {
            database.deleteContact(contact.getName());
        }

        database.addContact(a);
        database.addContact(b);
        database.addContact(c);
        database.addContact(d);
        database.addContact(e);
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            this.contacts = serviceLocator.getDatabase().getAllContacts();

        } catch (final IOException e) {
            LOGGER.error("onResume in MainActivity threw an IOException");
        }
    }

    /**
     * Gets all types of keys in the database
     *
     * @return a List with the types of keys.
     */
    private List<String> getKeyTypes() {
        final Set<String> typeSet = new HashSet<>();
        for (final Contact c : contacts) {
            for (final IKey k : c.getKeys()) {
                typeSet.add(k.getType());
            }
        }
        return new ArrayList<>(typeSet);
    }

    /**
     * Switches the sorting mode.
     */
    private void switchSorting() {
        currentSorting++;
        if (currentSorting >= NUMBER_OF_SORTING_MODES) {
            currentSorting = 0;
        }
        final ViewFlipper flipper = (ViewFlipper) findViewById(R.id.viewFlipper);
        flipper.showNext();
        switch (currentSorting) {
            case SORT_BY_NAME:
                sortOnName();
                break;
            case SORT_BY_KEY_TYPE:
                sortOnKeyType();
                break;
            default:
                break;
        }
    }

    /**
     * Sorts contacts by name
     */
    private void sortOnName() {
        final ListView lv = (ListView) findViewById(R.id.listView);
        final ContactListAdapter contactListAdapter = new ContactListAdapter(this, this.contacts);
        contactListAdapter.sort(nameSorter);
        lv.setAdapter(contactListAdapter);
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
     * Sorts contacts by key type
     */
    private void sortOnKeyType() {
        final ExpandableListView ev = (ExpandableListView) findViewById(R.id.expandableListView);
        final ExpandableListAdapter expandableListAdapter = new ExpandableListAdapter(this, getKeyTypes(), contacts);
        ev.setAdapter(expandableListAdapter);
        ev.setOnItemClickListener(new AdapterView.OnItemClickListener() {

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
     * An Adapter which converts a list with contacts into List entries.
     */
    private final class ContactListAdapter extends ArrayAdapter<Contact> {

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
                v = vi.inflate(R.layout.contact_list_entry, null);
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

    @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
    private final class ExpandableListAdapter extends BaseExpandableListAdapter {

        final private Map<String, List<Contact>> groupedContacts;
        final private List<String> types;
        final private Activity context;

        public ExpandableListAdapter(final Activity context, final List<String> types, final List<Contact> contacts) {
            super();
            this.context = context;
            this.types = types;
            groupedContacts = new HashMap<>();
            for (final String type : types) {
                groupedContacts.put(type, new ArrayList<Contact>());
            }
            for (final Contact contact : contacts) {
                for (final String type : types) {
                    if (!groupedContacts.get(type).contains(contact)) {
                        groupedContacts.get(type).add(contact);
                    }
                }
            }


        }

        @Override
        public Object getChild(final int groupPosition, final int childPosition) {

            return groupedContacts.get(types.get(groupPosition)).get(childPosition);
        }

        @Override
        public long getChildId(final int groupPosition, final int childPosition) {
            return childPosition;
        }

        @Override
        public View getChildView(final int groupPosition, final int childPosition,
                                 final boolean isLastChild, final View convertView, final ViewGroup parent) {
            final Contact contact = (Contact) getChild(groupPosition, childPosition);
            View v = convertView;

            if (v == null) {
                final LayoutInflater vi = context.getLayoutInflater();
                v = vi.inflate(R.layout.contact_list_entry, null);
            }


            if (contact != null) {
                final TextView name = (TextView) v.findViewById(R.id.name);

                if (name != null) {
                    name.setText(contact.getName());
                }
            }

            return v;
        }

        @Override
        public int getChildrenCount(final int groupPosition) {
            return groupedContacts.get(types.get(groupPosition)).size();
        }

        @Override
        public Object getGroup(final int groupPosition) {
            return types.get(groupPosition);
        }

        @Override
        public int getGroupCount() {
            return types.size();
        }

        @Override
        public long getGroupId(final int groupPosition) {
            return groupPosition;
        }

        @Override
        public View getGroupView(final int groupPosition, final boolean isExpanded,
                                 final View convertView, final ViewGroup parent) {
            final String type = (String) getGroup(groupPosition);
            View view = convertView;
            if (convertView == null) {
                final LayoutInflater vi = context.getLayoutInflater();
                view = vi.inflate(R.layout.key_type, null);
            }

            final TextView item = (TextView) view.findViewById(R.id.type);
            item.setTypeface(null, Typeface.BOLD);
            item.setText("Keytype: "+type);
            return view;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public boolean isChildSelectable(final int groupPosition, final int childPosition) {
            return true;
        }
    }
}