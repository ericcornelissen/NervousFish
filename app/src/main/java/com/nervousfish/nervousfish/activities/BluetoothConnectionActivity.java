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
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.nervousfish.nervousfish.ConstantKeywords;
import com.nervousfish.nervousfish.R;
import com.nervousfish.nervousfish.events.BluetoothConnectedEvent;
import com.nervousfish.nervousfish.modules.pairing.IBluetoothHandler;
import com.nervousfish.nervousfish.service_locator.IServiceLocator;

import org.greenrobot.eventbus.Subscribe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * This Bluetooth activity class establishes and manages a bluetooth connection.
 */

@SuppressWarnings({"PMD.TooManyMethods", "PMD.NullAssignment", "PMD.EmptyWhileStmt"})
// 1) This is taken from the Android manual on bluetooth :/
// 2) This is necessary for freeing up resources
// 3) Temporary busy wait, needs eventbus

public class BluetoothConnectionActivity extends Activity {

    private static final Logger LOGGER = LoggerFactory.getLogger("BluetoothConnectionActivity");
    private static final int DISCOVERABLE_DURATION = 300;

    // Device is now discoverable for 300 seconds
    private BluetoothAdapter bluetoothAdapter;
    private IBluetoothHandler bluetoothHandler;
    private Set<BluetoothDevice> newDevices;
    private Set<BluetoothDevice> pairedDevices;

    private ArrayAdapter<String> newDevicesArrayAdapter;
    // Create a BroadcastReceiver for ACTION_FOUND.
    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        public void onReceive(final Context context, final Intent intent) {
            final String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                final BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // If it's already paired, skip it, because it's been listed already
                if (device.getBondState() != BluetoothDevice.BOND_BONDED && !device.getName().equals("null")) {
                    newDevices.add(device);
                    newDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                }
                // When discovery is finished, change the Activity title
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                setTitle("select_device");
                if (newDevicesArrayAdapter.getCount() == 0) {
                    newDevicesArrayAdapter.add(getString(R.string.no_devices_found));
                }
            }

        }
    };
    private ArrayAdapter<String> pairedDevicesArrayAdapter;

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Intent intent = getIntent();
        // Get the serviceLocator.
        final IServiceLocator serviceLocator = (IServiceLocator) intent.getSerializableExtra(ConstantKeywords.SERVICE_LOCATOR);

        setupBluetoothAdapter();

        // Register for broadcasts when a device is discovered.
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(broadcastReceiver, filter);

        setContentView(R.layout.activity_bluetooth_connection);

        // Set result CANCELED in case the user backs out
        setResult(Activity.RESULT_CANCELED);

        // Initialize array adapters. One for already paired devices and
        // one for newly discovered devices
        pairedDevicesArrayAdapter =
                new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        newDevicesArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);

        // Find and set up the ListView for paired devices
        final ListView pairedListView = (ListView) findViewById(R.id.paired_list);
        pairedListView.setAdapter(pairedDevicesArrayAdapter);
        pairedListView.setOnItemClickListener(new DeviceClickListener());


        // Find and set up the ListView for newly discovered devices
        final ListView newDevicesListView = (ListView) findViewById(R.id.discovered_list);
        newDevicesListView.setAdapter(newDevicesArrayAdapter);
        newDevicesListView.setOnItemClickListener(new DeviceClickListener());


        // Register for broadcasts when discovery has finished
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        this.registerReceiver(broadcastReceiver, filter);

        // Get the AndroidBluetoothHandler.
        this.bluetoothHandler = serviceLocator.getBluetoothHandler();

        this.newDevices = new HashSet<>();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onDestroy() {
        unregisterReceiver(this.broadcastReceiver);
        this.bluetoothAdapter = null;
        super.onDestroy();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onStart() {
        super.onStart();
        bluetoothHandler.start();

        // Get the Paired Devices list
        queryPairedDevices();
        discoverDevices();
        LOGGER.info("Started the service and started discovering");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onStop() {
        LOGGER.info("Stopping from activity");
        super.onStop();
        //bluetoothHandler.stop();
    }

    /**
     * Gets triggered when the back button is clicked.
     *
     * @param v - the {@link View} clicked
     */
    public void onBackButtonClick(final View v) {
        setResult(ActivateBluetoothActivity.RESULT_CODE_FINISH_BLUETOOTH_ACTIVITY);
        finish();
    }

    /**
     * Sets up a bluetoothAdapter if it's supported and handles the problem when it's not.
     */
    public void setupBluetoothAdapter() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            // consequence for device not supporting bluetooth
            setResult(Activity.RESULT_CANCELED);
            finish();
        }
    }

    /**
     * Lines up all paired devices.
     */
    public void queryPairedDevices() {
        pairedDevices = bluetoothAdapter.getBondedDevices();
        findViewById(R.id.paired_list).setVisibility(View.VISIBLE);
        for (final BluetoothDevice device : pairedDevices) {
            pairedDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
        }
        LOGGER.info("Pairing query done");
    }

    /**
     * Starts Discovering bluetooth devices
     */
    public void discoverDevices() {

        setTitle("scanning");

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                1);

        // Turn on sub-title for new devices
        // findViewById(R.id.title_new_devices).setVisibility(View.VISIBLE);

        if (bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.cancelDiscovery();
        }
        final Intent discoverableIntent =
                new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, DISCOVERABLE_DURATION);
        startActivity(discoverableIntent);
        bluetoothAdapter.startDiscovery();
    }

    /**
     * Stops discovering bluetooth devices.
     */
    public void stopDiscovering() {
        if (bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.cancelDiscovery();
        }
    }

    /**
     * Called when a new bluetooth device is connected
     *
     * @param event Details about the event
     */
    @Subscribe
    public void onEvent(final BluetoothConnectedEvent event) {
        try {
            bluetoothHandler.sendAllContacts();
        } catch (final IOException e) {
            LOGGER.warn("Writing all contacts issued an IOexception", e);
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

                // Create the result Intent and include the MAC address
                /*final Intent intent = new Intent();
                intent.putExtra(EXTRA_DEVICE_ADDRESS, address);


                // Set result and finish this Activity
                setResult(Activity.RESULT_OK, intent);
                finish();*/
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