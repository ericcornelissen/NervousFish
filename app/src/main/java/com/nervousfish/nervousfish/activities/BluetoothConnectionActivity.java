package com.nervousfish.nervousfish.activities;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.nervousfish.nervousfish.ConstantKeywords;
import com.nervousfish.nervousfish.R;
import com.nervousfish.nervousfish.data_objects.Contact;
import com.nervousfish.nervousfish.modules.pairing.IBluetoothHandler;
import com.nervousfish.nervousfish.modules.pairing.events.BluetoothConnectedEvent;
import com.nervousfish.nervousfish.service_locator.IServiceLocator;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * An {@link Activity} that establishes and manages a bluetooth connection.
 * It shows a screen with the Bluetooth devices with which the user is paired and other Bluetooth devices
 * that are detected near the device of the user.
 */
@SuppressWarnings("PMD.ExcessiveImports") // Uses many Android and utility classes
public final class BluetoothConnectionActivity extends AppCompatActivity {

    private static final Logger LOGGER = LoggerFactory.getLogger("BluetoothConnectionActivity");
    private static final int DISCOVERABLE_DURATION = 300; // Device discoverable for 300 seconds

    private final Set<BluetoothDevice> newDevices = new HashSet<>();

    private IServiceLocator serviceLocator;
    private BluetoothAdapter bluetoothAdapter;
    private IBluetoothHandler bluetoothHandler;
    private Set<BluetoothDevice> pairedDevices;
    /**
     * Used to fill the listview of newly discovered Bluetooth devices
     */
    private ArrayAdapter<String> newDevicesArrayAdapter;
    /**
     * Used to fill the listview of paired Bluetooth devices
     */
    private ArrayAdapter<String> pairedDevicesArrayAdapter;

    // Create a BroadcastReceiver for ACTION_FOUND.
    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

        public void onReceive(final Context context, final Intent intent) {
            final String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                addNewDevice(intent);
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                setNoDevicesFound();
            }
        }

    };

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_bluetooth_connection);

        final Intent intent = getIntent();
        this.serviceLocator = (IServiceLocator) intent.getSerializableExtra(ConstantKeywords.SERVICE_LOCATOR);

        // Register for broadcasts when a device is discovered.
        final IntentFilter foundFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        this.registerReceiver(this.broadcastReceiver, foundFilter);

        // Set result CANCELED in case the user backs out
        this.setResult(Activity.RESULT_CANCELED);

        // Initialize array adapters. One for already paired devices and
        // one for newly discovered devices
        this.pairedDevicesArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        this.newDevicesArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);

        // Find and set up the ListView for paired devices
        final ListView pairedListView = (ListView) findViewById(R.id.paired_list);
        pairedListView.setAdapter(this.pairedDevicesArrayAdapter);
        pairedListView.setOnItemClickListener(new DeviceClickListener());

        // Find and set up the ListView for newly discovered devices
        final ListView newDevicesListView = (ListView) findViewById(R.id.discovered_list);
        newDevicesListView.setAdapter(this.newDevicesArrayAdapter);
        newDevicesListView.setOnItemClickListener(new DeviceClickListener());

        // Register for broadcasts when discovery has finished
        final IntentFilter discoveryFilter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        this.registerReceiver(this.broadcastReceiver, discoveryFilter);

        // Get the AndroidBluetoothHandler.
        this.bluetoothHandler = this.serviceLocator.getBluetoothHandler();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onStart() {
        super.onStart();

        this.serviceLocator.registerToEventBus(this);
        this.queryPairedDevices();
        this.discoverDevices();
        LOGGER.info("Started the service and started discovering");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onStop() {
        super.onStop();

        this.serviceLocator.unregisterFromEventBus(this);
        LOGGER.info("Stopped BluetoothConnectionActivity");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onBackPressed() {
        LOGGER.info("Back button was pressed");
        this.finish();
    }

    /**
     * Lines up all paired devices.
     */
    public void queryPairedDevices() {
        this.findViewById(R.id.paired_list).setVisibility(View.VISIBLE);

        this.pairedDevices = this.bluetoothAdapter.getBondedDevices();
        for (final BluetoothDevice device : this.pairedDevices) {
            this.pairedDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
        }

        LOGGER.info("Pairing query done");
    }

    /**
     * Starts Discovering bluetooth devices
     */
    public void discoverDevices() {
        this.setTitle(R.string.scanning);
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);

        this.stopDiscovering();

        final Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, DISCOVERABLE_DURATION);
        this.startActivity(discoverableIntent);

        this.bluetoothAdapter.startDiscovery();
    }

    /**
     * Stops discovering bluetooth devices.
     */
    public void stopDiscovering() {
        if (this.bluetoothAdapter.isDiscovering()) {
            this.bluetoothAdapter.cancelDiscovery();
        }
    }

    /**
     * Called when the device is connected over Bluetooth
     *
     * @param event Describes the event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(final BluetoothConnectedEvent event) {
        LOGGER.info("onBluetoothConnectedEvent called");
        try {
            final List<Contact> list = this.serviceLocator.getDatabase().getAllContacts();
            for (final Contact e : list) {
                this.bluetoothHandler.send(e);
            }
        } catch (final IOException e) {
            LOGGER.warn("Writing all contacts issued an IOexception", e);
        }
    }

    /**
     * Add new device to the list of Bluetooth devices.
     *
     * @param intent The {@link BroadcastReceiver} {@code intent}.
     */
    private void addNewDevice(final Intent intent) {
        final BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

        // Skip paired devices and devices without a name.
        if (device.getBondState() != BluetoothDevice.BOND_BONDED && !device.getName().equals("null")) {
            this.newDevices.add(device);
            this.newDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
        }
    }

    /**
     * Set message when no devices
     */
    private void setNoDevicesFound() {
        this.setTitle(R.string.select_device);
        if (this.newDevicesArrayAdapter.getCount() == 0) {
            this.newDevicesArrayAdapter.add(getString(R.string.no_devices_found));
        }
    }

    /**
     * The on-click listener for all devices in the ListViews
     */
    private final class DeviceClickListener implements AdapterView.OnItemClickListener {
        /**
         * {@inheritDoc}
         */
        @Override
        public void onItemClick(final AdapterView<?> av, final View v, final int arg2, final long arg3) {
            // Cancel discovery because it's costly and we're about to connect
            stopDiscovering();

            // Get the device MAC address, which is the last 17 chars in the View
            final String info = ((TextView) v).getText().toString();
            if (!info.equals(getString(R.string.no_devices_found))) {
                final String address = info.substring(info.length() - 17);
                final BluetoothDevice device = getDevice(address);
                bluetoothHandler.connect(device);
            }
        }

        /**
         * Gets the device corresponding to the address from either the paired or the new devices.
         *
         * @param address The MAC address corresponding to the device to be found
         * @return The BLuetoothDevice corresponding to the mac address.
         */
        private BluetoothDevice getDevice(final String address) {
            for (final BluetoothDevice device : pairedDevices) {
                if (device.getAddress().equals(address)) {
                    return device;
                }
            }
            for (final BluetoothDevice device : newDevices) {
                if (device.getAddress().equals(address)) {
                    return device;
                }
            }
            return null;
        }

    }

}