package com.nervousfish.nervousfish.activities;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.github.clans.fab.Label;
import com.nervousfish.nervousfish.ConstantKeywords;
import com.nervousfish.nervousfish.R;
import com.nervousfish.nervousfish.data_objects.Contact;
import com.nervousfish.nervousfish.data_objects.RSAKey;
import com.nervousfish.nervousfish.exceptions.NoBluetoothException;
import com.nervousfish.nervousfish.modules.database.IDatabase;
import com.nervousfish.nervousfish.modules.pairing.IBluetoothHandler;
import com.nervousfish.nervousfish.modules.pairing.events.BluetoothConnectedEvent;
import com.nervousfish.nervousfish.service_locator.IServiceLocator;
import com.nervousfish.nervousfish.service_locator.NervousFish;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

/**
 * The main {@link Activity} that shows a list of all contacts and a button that lets you obtain new
 * public keys from other people
 */
@SuppressWarnings({"checkstyle:ClassFanOutComplexity", "checkstyle:ClassDataAbstractionCoupling",
        "PMD.ExcessiveImports", "PMD.TooFewBranchesForASwitchStatement", "PMD.TooManyMethods",
        "PMD.AvoidCatchingGenericException", "PMD.AvoidCatchingNPE", "PMD.CyclomaticComplexity"})
//  1)  This warning is because it relies on too many other classes, yet there's still methods like fill databasewithdemodata
//      which will be deleted later on
//  2)  This warning means there are too many instantiations of other classes within this class,
//      which basically comes down to the same problem as the last
//  3)  Another suppression based on the same problem as the previous 2
//  4)  The switch statement for switching sorting Types does not have enough branches, because it is designed
//      to be extended when necessary to more sorting Types.
//  5)  Suppressed because this rule is not meant for Android classes like this, that have no other choice
//      than to add methods for overriding the activity state machine and providing View click listeners
//  6 and 7)  Because the Service is sometimes null after scanning the QR code, we want to give a nice popup for it.
//  8)  There are a lot of if checks involved in determining which pairing button was clicked and when
public final class MainActivity extends AppCompatActivity {

    static final int ENABLE_BLUETOOTH_ON_START = 100;
    static final int ENABLE_BLUETOOTH_ON_BUTTON_CLICK = 200;
    static final Logger LOGGER = LoggerFactory.getLogger("MainActivity");

    private IServiceLocator serviceLocator;
    private IDatabase database;
    private MainActivitySorter sorter;
    private MainActivityPopups popups;
    private List<Contact> contacts;

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_main);

        this.serviceLocator = NervousFish.getServiceLocator();
        this.database = this.serviceLocator.getDatabase();
        this.popups = new MainActivityPopups(this);

        final Toolbar toolbar = (Toolbar) this.findViewById(R.id.toolbar_main);
        this.setSupportActionBar(toolbar);

        if (this.getSupportActionBar() != null) {
            this.getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        try {
            this.contacts = this.database.getAllContacts();
        } catch (final IOException e) {
            LOGGER.error("Failed to retrieve contacts from database", e);
        }

        // Start Bluetooth
        this.startBluetooth();

        // Check if NFC is available
        if (NfcAdapter.getDefaultAdapter(this) == null) {
            LOGGER.info("NFC not available on device, disabling button");
            final FloatingActionButton button = (FloatingActionButton) this.findViewById(R.id.pairing_menu_nfc);
            button.setEnabled(false);
        }

        // Initialize sorter
        this.sorter = new MainActivitySorter(this);

        // Fab button listeners, inserted programmatically to support older devices
        this.findViewById(R.id.pairing_menu_bluetooth).setOnClickListener(this::onPairingButtonClicked);
        this.findViewById(R.id.pairing_menu_nfc).setOnClickListener(this::onPairingButtonClicked);
        this.findViewById(R.id.pairing_menu_qr).setOnClickListener(this::onPairingButtonClicked);

        // Bluetooth exchange result
        final Intent intent = this.getIntent();
        final Contact contact = (Contact) intent.getSerializableExtra(ConstantKeywords.CONTACT);
        if (contact != null) {
            ContactReceivedHelper.newContactReceived(this.database, this, contact);
        }
        ContactReceivedHelper.newContactReceived(this.database, this, new Contact("hoi", new RSAKey("foo", "bar", "baz")));

        LOGGER.info("Activity created");
    }

    /**
     * Asks if the users wants to give permission to use their location.
     */
    private void askBluetoothLocationPermission() {
        LOGGER.info("Location permission for Bluetooth asked");

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
    }
  
    /**
     * Tries to start bluetooth:
     * - If Bluetooth is already enabled, do nothing
     * - If Bluetooth is not enabled yet, prompt the user to enable it
     * - If the device has no Bluetooth, disable the Bluetooth button
     */
    private void startBluetooth() {
        final IBluetoothHandler bluetoothHandler = this.serviceLocator.getBluetoothHandler();
        try {
            this.askBluetoothLocationPermission();
            //noinspection LawOfDemeter because we don't want to clutter the service locator by adding a method like "startBluetoothHandler"
            bluetoothHandler.start();
        } catch (final NoBluetoothException e) {
            LOGGER.info("Bluetooth not available on device, disabling button", e);
            final FloatingActionButton button = (FloatingActionButton) this.findViewById(R.id.pairing_menu_bluetooth);
            button.setEnabled(false);
        } catch (final IOException e) {
            LOGGER.info("Bluetooth handler not started, most likely Bluetooth is not enabled", e);
            this.enableBluetooth(false);
        } catch (NullPointerException e) {
            LOGGER.error("Could not start the Bluetooth service", e);
            this.popups.showSomethingWentWrong();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onStart() {
        super.onStart();
        this.serviceLocator.registerToEventBus(this);

        LOGGER.info("Activity started");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onResume() {
        super.onResume();

        try {
            this.contacts = this.database.getAllContacts();
            this.sorter.sortOnName();
        } catch (final IOException e) {
            LOGGER.error("onResume in MainActivity threw an IOException", e);
        }

        LOGGER.info("Activity resumed");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onStop() {
        super.onStop();
        this.serviceLocator.unregisterFromEventBus(this);

        LOGGER.info("Activity stopped");
    }

    /**
     * Exit the application when the user taps the back button twice
     */
    @Override
    public void onBackPressed() {
        this.popups.showAreYouSureToLogOut();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == MainActivity.ENABLE_BLUETOOTH_ON_BUTTON_CLICK) {
            final Intent intent = new Intent(this, BluetoothConnectActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            this.startActivity(intent);
        }
    }

    /**
     * Switches the sorting mode.
     *
     * @param view The sort floating action button that was clicked
     */
    public void onSortButtonClicked(final View view) {
        this.sorter.onSortButtonClicked(view);
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

        String textOnLabel = "";
        if (view instanceof Label) {
            textOnLabel = ((Label) view).getText().toString();
        }

        if (view.getId() == R.id.pairing_menu_bluetooth || textOnLabel.equals(this.getResources().getString(R.string.bluetooth))) {
            final BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (bluetoothAdapter.isEnabled()) {
                intent.setComponent(new ComponentName(this, BluetoothConnectActivity.class));
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            } else {
                this.enableBluetooth(true);
                return; // Prevent `this.startActivity()`
            }
        } else if (view.getId() == R.id.pairing_menu_nfc || textOnLabel.equals(getResources().getString(R.string.nfc))) {
            final NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(this);
            if (nfcAdapter.isEnabled()) {
                intent.setComponent(new ComponentName(this, NFCExchangeActivity.class));
            } else {
                this.enableNFC();
                return; // Prevent `this.startActivity()`
            }
        } else if (view.getId() == R.id.pairing_menu_qr || textOnLabel.equals(getResources().getString(R.string.qr))) {
            intent.setComponent(new ComponentName(this, QRExchangeActivity.class));
        } else {
            LOGGER.error("Unknown pairing button clicked: {}", view.getId());
            throw new IllegalArgumentException("Only existing buttons can be clicked");
        }

        this.startActivity(intent);
    }

    /**
     * Gets triggered when the 3 dots button in the toolbar is clicked.
     *
     * @param view The view that was clicked
     */
    public void onClickDotsButton(final View view) {
        final Intent intent = new Intent(this, SettingsActivity.class);
        this.startActivity(intent);
    }

    /**
     * Temporary method to open the {@link ContactActivity} for a contact.
     *
     * @param index The index of the contact in {@code this.contacts}.
     */
    void openContact(final int index) {
        final Intent intent = new Intent(this, ContactActivity.class);
        intent.putExtra(ConstantKeywords.CONTACT, this.contacts.get(index));
        this.startActivity(intent);
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
        intent.putExtra(ConstantKeywords.WAIT_MESSAGE, this.getString(R.string.wait_message_slave_verification_method));
        this.startActivityForResult(intent, ConstantKeywords.START_RHYTHM_REQUEST_CODE);
    }

    /**
     * Prompt user to enable Bluetooth if it is disabled.
     */
    private void enableBluetooth(final boolean buttonClicked) {
        final BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (buttonClicked && bluetoothAdapter.isEnabled()) {
            final Intent intent = new Intent(this, BluetoothConnectActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            this.startActivity(intent);
        } else if (!bluetoothAdapter.isEnabled()) {
            this.popups.showEnableBluetoothPopup(buttonClicked);
        }
    }

    /**
     * Prompt user to enable NFC if it is disabled.
     */
    private void enableNFC() {
        final NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        if (nfcAdapter.isEnabled()) {
            final Intent intent = new Intent(this, NFCExchangeActivity.class);
            this.startActivity(intent);
        } else {
            this.popups.showEnableNFCPopup();
        }
    }

    List<Contact> getContacts() {
        return this.contacts;
    }

}
