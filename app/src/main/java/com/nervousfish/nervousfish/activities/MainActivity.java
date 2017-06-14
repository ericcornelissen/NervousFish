package com.nervousfish.nervousfish.activities;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.github.clans.fab.Label;
import com.nervousfish.nervousfish.ConstantKeywords;
import com.nervousfish.nervousfish.R;
import com.nervousfish.nervousfish.data_objects.Contact;
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
import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * The main {@link Activity} that shows a list of all contacts and a button that lets you obtain new
 * public keys from other people
 */
@SuppressWarnings({"checkstyle:ClassFanOutComplexity", "checkstyle:ClassDataAbstractionCoupling",
        "PMD.ExcessiveImports", "PMD.TooFewBranchesForASwitchStatement", "PMD.TooManyMethods", "PMD.CyclomaticComplexity"})
//  1)  This warning is because it relies on too many other classes, yet there's still methods like fill databasewithdemodata
//      which will be deleted later on
//  2)  This warning means there are too many instantiations of other classes within this class,
//      which basically comes down to the same problem as the last
//  3)  Another suppression based on the same problem as the previous 2
//  4)  The switch statement for switching sorting Types does not have enough branches, because it is designed
//      to be extended when necessary to more sorting Types.
//  5)  Suppressed because this rule is not meant for Android classes like this, that have no other choice
//      than to add methods for overriding the activity state machine and providing View click listeners
//  6)  There are a lot of if checks involved in determining which pairing button was clicked and when
public final class MainActivity extends AppCompatActivity {

    private static final Logger LOGGER = LoggerFactory.getLogger("MainActivity");
    private static final int ENABLE_BLUETOOTH_ON_START = 100;
    private static final int ENABLE_BLUETOOTH_ON_BUTTON_CLICK = 200;

    private IServiceLocator serviceLocator;
    private IDatabase database;
    private MainActivitySorter sorter;
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

        final IBluetoothHandler bluetoothHandler = this.serviceLocator.getBluetoothHandler();
        // Start Bluetooth
        try {
            //noinspection LawOfDemeter because we don't want to clutter the service locator by adding a method like "startBluetoothHandler"
            bluetoothHandler.start();
        } catch (final NoBluetoothException e) {
            LOGGER.info("Bluetooth not available on device, disabling button", e);
            final FloatingActionButton button = (FloatingActionButton) this.findViewById(R.id.pairing_menu_bluetooth);
            button.setEnabled(false);
        } catch (final IOException e) {
            LOGGER.info("Bluetooth handler not started, most likely Bluetooth is not enabled", e);
            this.enableBluetooth(false);
        }

        // Bluetooth exchange result
        final Intent intent = this.getIntent();
        final Object successfulBluetooth = intent.getSerializableExtra(ConstantKeywords.SUCCESSFUL_EXCHANGE);
        this.showSuccessfulBluetoothPopup(successfulBluetooth);

        if (NfcAdapter.getDefaultAdapter(this) == null) {
            LOGGER.info("NFC not available on device, disabling button");
            final FloatingActionButton button = (FloatingActionButton) this.findViewById(R.id.pairing_menu_nfc);
            button.setEnabled(false);
        }

        // Initialize sorter
        this.sorter = new MainActivitySorter(this);

        LOGGER.info("Activity created");
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
        new SweetAlertDialog(this, SweetAlertDialog.NORMAL_TYPE)
                .setTitleText(this.getString(R.string.popup_log_out_title))
                .setContentText(this.getString(R.string.popup_log_out_description))
                .setCancelText(this.getString(R.string.no))
                .setConfirmText(this.getString(R.string.yes))
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(final SweetAlertDialog sDialog) {
                        final Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                        startActivity(intent);
                    }
                })
                .show();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == MainActivity.ENABLE_BLUETOOTH_ON_BUTTON_CLICK) {
            final Intent intent = new Intent(this, BluetoothConnectionActivity.class);
            this.startActivity(intent);
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
                intent.setComponent(new ComponentName(this, BluetoothConnectionActivity.class));
                this.startActivity(intent);
            } else {
                this.enableBluetooth(true);
                return; // Prevent `this.startActivity()`
            }
        } else if (view.getId() == R.id.pairing_menu_nfc || textOnLabel.equals(getResources().getString(R.string.nfc))) {
            final NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(this);
            if (nfcAdapter.isEnabled()) {
                intent.setComponent(new ComponentName(this, NFCActivity.class));
                this.startActivity(intent);
            } else {
                this.enableNFC();
                return; // Prevent `this.startActivity()`
            }

        } else if (view.getId() == R.id.pairing_menu_qr || textOnLabel.equals(getResources().getString(R.string.qr))) {
            intent.setComponent(new ComponentName(this, QRExchangeKeyActivity.class));
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
     * Shows a popup that adding a contact went fine if the boolean
     * added in the intent is true.
     *
     * @param successfulBluetooth The intents value for {@code SUCCESSFUL_BLUETOOTH}.
     */
    private void showSuccessfulBluetoothPopup(final Object successfulBluetooth) {
        if (successfulBluetooth != null) {
            final boolean success = (boolean) successfulBluetooth;
            if (success) {
                new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                        .setTitleText(this.getString(R.string.contact_added_popup_title))
                        .setContentText(this.getString(R.string.contact_added_popup_explanation))
                        .setConfirmText(this.getString(R.string.dialog_ok))
                        .show();
            }
        }
    }

    /**
     * Prompt user to enable Bluetooth if it is disabled.
     */
    private void enableBluetooth(final boolean buttonClicked) {
        final BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (buttonClicked && bluetoothAdapter.isEnabled()) {
            final Intent intent = new Intent(this, BluetoothConnectionActivity.class);
            this.startActivity(intent);
        } else if (!bluetoothAdapter.isEnabled()) {
            final String description = this.getString(
                    buttonClicked
                            ? R.string.popup_enable_bluetooth_exchange : R.string.popup_enable_bluetooth_findable);

            new SweetAlertDialog(this, SweetAlertDialog.NORMAL_TYPE)
                    .setTitleText(this.getString(R.string.popup_enable_bluetooth_title))
                    .setContentText(description)
                    .setCancelText(this.getString(R.string.no))
                    .setConfirmText(this.getString(R.string.yes))
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(final SweetAlertDialog sweetAlertDialog) {
                            sweetAlertDialog.dismiss();

                            LOGGER.info("Requesting to enable Bluetooth");
                            final Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                            if (buttonClicked) {
                                MainActivity.this.startActivityForResult(intent, ENABLE_BLUETOOTH_ON_BUTTON_CLICK);
                            } else {
                                MainActivity.this.startActivityForResult(intent, ENABLE_BLUETOOTH_ON_START);
                            }
                            LOGGER.info("Request to enable Bluetooth sent");
                        }
                    })
                    .show();
        }
    }

    private void enableNFC() {
        final NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        if (nfcAdapter.isEnabled()) {
            final Intent intent = new Intent(this, NFCActivity.class);
            this.startActivity(intent);
        } else {
            final String description;
            description = this.getString(R.string.popup_enable_nfc_settings);
            LOGGER.info("Requesting to enable NFC");
            new SweetAlertDialog(this, SweetAlertDialog.NORMAL_TYPE)
                    .setTitleText(this.getString(R.string.popup_enable_nfc_title))
                    .setContentText(description)
                    .setCancelText(this.getString(R.string.no))
                    .setConfirmText(this.getString(R.string.yes))
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(final SweetAlertDialog dialog) {
                            dialog.dismiss();
                            startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
                            LOGGER.info("Request to enable NFC sent, forwarded to settings");
                        }
                    })
                    .show();
        }
    }

    List<Contact> getContacts() {
        return new ArrayList<>(this.contacts);
    }

}
