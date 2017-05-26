package com.nervousfish.nervousfish.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.ViewFlipper;

import com.nervousfish.nervousfish.ConstantKeywords;
import com.nervousfish.nervousfish.R;
import com.nervousfish.nervousfish.data_objects.Contact;
import com.nervousfish.nervousfish.data_objects.IKey;
import com.nervousfish.nervousfish.data_objects.SimpleKey;
import com.nervousfish.nervousfish.events.NewContactsReceivedEvent;
import com.nervousfish.nervousfish.list_adapters.ContactsByKeyTypeListAdapter;
import com.nervousfish.nervousfish.list_adapters.ContactsByNameListAdapter;
import com.nervousfish.nervousfish.modules.database.IDatabase;
import com.nervousfish.nervousfish.service_locator.IServiceLocator;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * The main activity class that shows a list of all people with their public keys
 */
@SuppressWarnings({"checkstyle:ClassFanOutComplexity", "checkstyle:ClassDataAbstractionCoupling",
        "PMD.ExcessiveImports", "PMD.TooFewBranchesForASwitchStatement"})
//  1)  This warning is because it relies on too many other classes, yet there's still methods like fill databasewithdemodata
//      which will be deleted later on
//  2)  This warning means there are too many instantiations of other classes within this class,
//      which basically comes down to the same problem as the last
//  3)  Another suppression based on the same problem as the previous 2
//  4)  The switch statement for switching sorting types does not have enough branches, because it is designed
//      to be extended when necessairy to more sorting types.
public final class MainActivity extends AppCompatActivity {

    private static final Logger LOGGER = LoggerFactory.getLogger("MainActivity");
    private static final int NUMBER_OF_SORTING_MODES = 2;
    private static final int SORT_BY_NAME = 0;
    private static final int SORT_BY_KEY_TYPE = 1;

    private IServiceLocator serviceLocator;

    private static final Comparator<Contact> NAME_SORTER = new Comparator<Contact>() {
        @Override
        public int compare(final Contact o1, final Contact o2) {
            return o1.getName().compareTo(o2.getName());
        }
    };

    private List<Contact> contacts;
    private Integer currentSorting = 0;

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

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        this.setSupportActionBar(toolbar);

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.bluetoothButton);
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

        try {
            fillDatabaseWithDemoData();
            this.contacts = this.serviceLocator.getDatabase().getAllContacts();
        } catch (final IOException e) {
            LOGGER.error("Failed to retrieve contacts from database", e);
        }

        sortOnName();

        final FloatingActionButton fab2 = (FloatingActionButton) findViewById(R.id.sortButton);
        fab2.setOnClickListener(new View.OnClickListener() {

            /**
             * {@inheritDoc}
             */
            @Override
            public void onClick(final View view) {
                switchSorting();
            }

        });

        this.serviceLocator.getBluetoothHandler().start();

        LOGGER.info("MainActivity created");
    }

    @Override
    protected void onStart() {
        super.onStart();
        this.serviceLocator.registerToEventBus(this);
        try {
            this.contacts = this.serviceLocator.getDatabase().getAllContacts();
        } catch (final IOException e) {
            LOGGER.error("onResume in MainActivity threw an IOException", e);
        }
    }

    @Override
    protected void onStop() {
        this.serviceLocator.unregisterFromEventBus(this);
        super.onStop();
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
        final Intent intent = new Intent(this, ActivateBluetoothActivity.class);
        intent.putExtra(ConstantKeywords.SERVICE_LOCATOR, this.serviceLocator);
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
        //final Contact c = new Contact("Joost", new SimpleKey("Webserver", "dnfh4nl4jknlkjnr4j34klnk3j4nl"));
        //final Contact d = new Contact("Kilian", new SimpleKey("Webmail", "sdjnefiniwfnfejewjnwnkenfk32"));
        //final Contact e = new Contact("Cornel", new SimpleKey("Awesomeness", "nr23uinr3uin2o3uin23oi4un234ijn"));

        final List<Contact> contacts = database.getAllContacts();
        for (final Contact contact : contacts) {
            database.deleteContact(contact.getName());
        }

        database.addContact(a);
        database.addContact(b);
    }

    /**
     * Called when a new contact is received
     *
     * @param event Contains additional data about the event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNewContactsReceivedEvent(final NewContactsReceivedEvent event) {
        try {
            this.contacts = this.serviceLocator.getDatabase().getAllContacts();
            sortOnName();
        } catch (IOException e) {
            LOGGER.error("Couldn't add contact to database", e);
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
        final ContactsByNameListAdapter contactsByNameListAdapter = new ContactsByNameListAdapter(this, this.contacts);
        contactsByNameListAdapter.sort(NAME_SORTER);
        lv.setAdapter(contactsByNameListAdapter);
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
        final ExpandableListView ev = (ExpandableListView) findViewById(R.id.expandableContactListByKeytype);
        final ContactsByKeyTypeListAdapter contactsByKeyTypeListAdapter = new ContactsByKeyTypeListAdapter(this, getKeyTypes(), contacts);
        ev.setAdapter(contactsByKeyTypeListAdapter);
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

}