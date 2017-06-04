package com.nervousfish.nervousfish.activities;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import com.github.clans.fab.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.github.clans.fab.FloatingActionMenu;
import com.nervousfish.nervousfish.ConstantKeywords;
import com.nervousfish.nervousfish.R;
import com.nervousfish.nervousfish.data_objects.Contact;
import com.nervousfish.nervousfish.data_objects.IKey;
import com.nervousfish.nervousfish.data_objects.SimpleKey;
import com.nervousfish.nervousfish.exceptions.NoBluetoothException;
import com.nervousfish.nervousfish.modules.database.IDatabase;
import com.nervousfish.nervousfish.modules.pairing.events.BluetoothConnectedEvent;
import com.nervousfish.nervousfish.modules.pairing.events.NewDataReceivedEvent;
import com.nervousfish.nervousfish.service_locator.IServiceLocator;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * The main {@link Activity} that shows a list of all contacts and a button that lets you obtain new
 * public keys from other people
 */
@SuppressWarnings({"checkstyle:ClassFanOutComplexity", "checkstyle:ClassDataAbstractionCoupling",
        "PMD.ExcessiveImports", "PMD.TooFewBranchesForASwitchStatement", "PMD.TooManyMethods"})
//  1)  This warning is because it relies on too many other classes, yet there's still methods like fill databasewithdemodata
//      which will be deleted later on
//  2)  This warning means there are too many instantiations of other classes within this class,
//      which basically comes down to the same problem as the last
//  3)  Another suppression based on the same problem as the previous 2
//  4)  The switch statement for switching sorting types does not have enough branches, because it is designed
//      to be extended when necessairy to more sorting types.
//  5)  Suppressed because this rule is not meant for Android classes like this, that have no other choice
//      than to add methods for overriding the activity state machine and providing View click listeners
public final class MainActivity extends AppCompatActivity {

    private static final Logger LOGGER = LoggerFactory.getLogger("MainActivity");
    private static final int REQUEST_CODE_ENABLE_BLUETOOTH_ON_START = 100;
    private static final int REQUEST_CODE_ENABLE_BLUETOOTH_ON_BUTTON_CLICK = 200;

    private List<Contact> contacts;

    private IServiceLocator serviceLocator;
    private MainActivitySorter sorter;

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

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_main);
        this.setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        try {
            if (this.serviceLocator.getDatabase().getAllContacts().isEmpty()) {
                fillDatabaseWithDemoData();
            }
            this.contacts = this.serviceLocator.getDatabase().getAllContacts();
        } catch (final IOException e) {
            LOGGER.error("Failed to retrieve contacts from database", e);
        }

        sorter = new MainActivitySorter(this);
        sorter.sortOnName();

        try {
            this.serviceLocator.getBluetoothHandler().start();
        } catch (NoBluetoothException e) {
            LOGGER.info("Bluetooth not available on device, disabling button");
            final FloatingActionButton button = (FloatingActionButton) this.findViewById(R.id.pairing_menu_bluetooth);
            button.setEnabled(false);
        } catch (IOException e) {
            LOGGER.info("Bluetooth handler not started, most likely Bluetooth is not enabled");
            this.enableBluetooth(false);
        }

        showSuccessfullBluetoothPopup(intent);

        LOGGER.info("MainActivity created");
    }

    /**
     * Shows a popup that adding a contact went fine if the boolean
     * added in the intent is true.
     *
     * @param intent The intent of this Activity
     */
    private void showSuccessfullBluetoothPopup(final Intent intent) {
        if (intent.getSerializableExtra(ConstantKeywords.SUCCESSFUL_BLUETOOTH) != null) {

            final boolean successfulBluetooth = (boolean) intent.getSerializableExtra(ConstantKeywords.SUCCESSFUL_BLUETOOTH);

            if (successfulBluetooth) {
                new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                        .setTitleText(getString(R.string.contact_added_popup_title))
                        .setContentText(getString(R.string.contact_added_popup_explanation))
                        .setConfirmText(getString(R.string.dialog_ok))
                        .show();
            }
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    protected void onResume() {
        super.onResume();
        try {
            this.contacts = serviceLocator.getDatabase().getAllContacts();
            sorter.sortOnName();
        } catch (final IOException e) {
            LOGGER.error("onResume in MainActivity threw an IOException", e);
        }
    }

    /**
     * Switches the sorting mode.
     *
     * @param view The sort floating action button that was clicked
     */
    public void onSortButtonClicked(final View view) {
        sorter.onSortButtonClicked(view);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onStart() {
        super.onStart();
        this.serviceLocator.registerToEventBus(this);
        try {
            this.contacts = this.serviceLocator.getDatabase().getAllContacts();
        } catch (final IOException e) {
            LOGGER.error("onStart in MainActivity threw an IOException", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onStop() {
        this.serviceLocator.unregisterFromEventBus(this);
        super.onStop();
    }

    /**
     * Goes to the activity associated with the view after clicking a pairing button
     *
     * @param view The view that was clicked
     */
    public void onPairingButtonClicked(final View view) {
        // Close the FAB
        ((FloatingActionMenu) this.findViewById(R.id.pairing_button)).close(true);

        // Open the correct pairing activity
        final Intent intent = new Intent();
        intent.putExtra(ConstantKeywords.SERVICE_LOCATOR, this.serviceLocator);

        switch (view.getId()) {
            case R.id.pairing_menu_bluetooth:
                final BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                if (bluetoothAdapter.isEnabled()) {
                    intent.setComponent(new ComponentName(this, BluetoothConnectionActivity.class));
                    this.startActivity(intent);
                } else {
                    this.enableBluetooth(true);
                    return; // Prevent `this.startActivity()`
                }
                break;
            case R.id.pairing_menu_nfc:
                intent.setComponent(new ComponentName(this, NFCActivity.class));
                this.startActivity(intent);
                break;
            case R.id.pairing_menu_qr:
                intent.setComponent(new ComponentName(this, QRExchangeKeyActivity.class));
                this.startActivity(intent);
                break;
            default:
                LOGGER.error("Unknown pairing button clicked");
                throw new IllegalArgumentException("Only existing buttons can be clicked");
        }
    }

    /**
     * Temporary method to open the {@link ContactActivity} for a contact.
     *
     * @param index The index of the contact in {@code this.contacts}.
     */
    void openContact(final int index) {
        final Intent intent = new Intent(this, ContactActivity.class);
        intent.putExtra(ConstantKeywords.SERVICE_LOCATOR, this.serviceLocator);
        intent.putExtra(ConstantKeywords.CONTACT, this.contacts.get(index));
        this.startActivity(intent);
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
     * Called when a Bluetooth connection is established.
     *
     * @param event Contains additional data about the event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNewBluetoothConnectedEvent(final BluetoothConnectedEvent event) {
        LOGGER.info("onNewBluetoothConnectedEvent called");

        final Intent intent = new Intent(this, WaitActivity.class);
        intent.putExtra(ConstantKeywords.SERVICE_LOCATOR, this.serviceLocator);
        intent.putExtra(ConstantKeywords.WAIT_MESSAGE, getString(R.string.wait_message_slave_verification_method));
        this.startActivityForResult(intent, ConstantKeywords.START_RHYTHM_REQUEST_CODE);
    }

    /**
     * Called when a new contact is received
     *
     * @param event Contains additional data about the event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNewDataReceivedEvent(final NewDataReceivedEvent event) {
        LOGGER.info("onNewDataReceivedEvent called");
        if (event.getClazz().equals(String.class)) {
            final String verificationMessage = (String) event.getData();

            if ("rhythm".equals(verificationMessage)) {
                final Intent intent = new Intent(this, RhythmCreateActivity.class);
                intent.putExtra(ConstantKeywords.SERVICE_LOCATOR, serviceLocator);
                this.startActivityForResult(intent, 0);
            } else if ("visual".equals(verificationMessage)) {
                final Intent intent = new Intent(this, VisualVerificationActivity.class);
                intent.putExtra(ConstantKeywords.SERVICE_LOCATOR, serviceLocator);
                this.startActivityForResult(intent, 0);
            }
        } else if (event.getClazz().equals(Contact.class)) {
            final Contact contact = (Contact) event.getData();
            try {
                LOGGER.info("Checking if the contact exists...");
                if (checkExists(contact)) {
                    LOGGER.warn("Contact already existed...");
                } else {
                    LOGGER.info("Adding contact to database...");
                    this.serviceLocator.getDatabase().addContact(contact);
                    this.contacts = this.serviceLocator.getDatabase().getAllContacts();
                    sorter.sortOnName();
                }
            } catch (IOException e) {
                LOGGER.error("Couldn't get contacts from database", e);
            }
        }
    }

    /**
     * Checks if a name of a given contact exists in the database.
     *
     * @param contact A contact object
     * @return true when a contact with the same exists in the database
     * @throws IOException When database fails to respond
     */
    private boolean checkExists(final Contact contact) throws IOException {
        final String name = contact.getName();
        final List<Contact> list = this.serviceLocator.getDatabase().getAllContacts();
        for (final Contact e : list) {
            if (e.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Exit the application when the user taps the back button twice
     */
    @Override
    public void onBackPressed() {
        new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText(getString(R.string.warning))
                .setContentText(getString(R.string.you_sure_log_out))
                .setCancelText(getString(R.string.no))
                .setConfirmText(getString(R.string.yes))
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(final SweetAlertDialog sDialog) {
                        finish();
                    }
                })
                .show();
    }

    /**
     * Prompt user to enable Bluetooth if it is disabled.
     */
    private void enableBluetooth(final boolean buttonClicked) {
        final BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (buttonClicked && bluetoothAdapter.isEnabled()) {
            final Intent intent = new Intent(this, BluetoothConnectionActivity.class);
            intent.putExtra(ConstantKeywords.SERVICE_LOCATOR, this.serviceLocator);
            this.startActivity(intent);
        } else if (!bluetoothAdapter.isEnabled()) {
            new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText(getString(R.string.enable_bluetooth_questionmark))
                    .setContentText(getString(R.string.enable_bluetooth_explanation))
                    .setCancelText(getString(R.string.cancel))
                    .setConfirmText(getString(R.string.dialog_ok))
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {

                        /**
                         * {@inheritDoc}
                         */
                        @Override
                        public void onClick(final SweetAlertDialog sweetAlertDialog) {
                            sweetAlertDialog.dismiss();
                            LOGGER.info("Requesting to enable Bluetooth");
                            final Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                            if (buttonClicked) {
                                startActivityForResult(intent, MainActivity.REQUEST_CODE_ENABLE_BLUETOOTH_ON_BUTTON_CLICK);
                            } else {
                                startActivityForResult(intent, MainActivity.REQUEST_CODE_ENABLE_BLUETOOTH_ON_START);
                            }
                            LOGGER.info("Request to enable Bluetooth sent");
                        }

                    })
                    .show();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == MainActivity.REQUEST_CODE_ENABLE_BLUETOOTH_ON_BUTTON_CLICK) {
            final Intent intent = new Intent(this, BluetoothConnectionActivity.class);
            intent.putExtra(ConstantKeywords.SERVICE_LOCATOR, this.serviceLocator);
            this.startActivity(intent);
        }
    }

    List<Contact> getContacts() {
        return contacts;
    }

}