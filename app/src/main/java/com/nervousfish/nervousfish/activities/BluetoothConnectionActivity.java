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
import com.nervousfish.nervousfish.modules.pairing.BluetoothConnectionService;
import com.nervousfish.nervousfish.service_locator.IServiceLocator;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Kilian on 2/05/2017.
 * This Bluetooth activity class establishes and manages a bluetooth connection.
 */

@SuppressWarnings({"PMD.TooManyMethods", "PMD.NullAssignment", "PMD.EmptyWhileStmt"})
// 1) This is taken from the Android manual on bluetooth :/
// 2) This is necessary for freeing up resources
// 3) Temporary busy wait, needs eventbus

public class BluetoothConnectionActivity extends AppCompatActivity {

    public static final String EXTRA_DEVICE_ADDRESS = "device_address";

    //Request codes
    private static final int REQUEST_CODE_ENABLE_BLUETOOTH = 100;
    private static final int REQUEST_CODE_CHECK_BLUETOOTH_STATE = 101;

    // Device is now discoverable for 300 seconds
    private static final int DISCOVERABILITY_TIME = 300;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothConnectionService bluetoothConnectionService;
    private Set<BluetoothDevice> newDevices;
    private Set<BluetoothDevice> pairedDevices;
    /**
     * The on-click listener for all devices in the ListViews
     */
    private final AdapterView.OnItemClickListener mDeviceClickListener
            = new AdapterView.OnItemClickListener() {
        public void onItemClick(final AdapterView<?> av, final View v, final int arg2, final long arg3) {
            // Cancel discovery because it's costly and we're about to connect
            stopDiscovering();

            // Get the device MAC address, which is the last 17 chars in the View
            final String info = ((TextView) v).getText().toString();
            final String address = info.substring(info.length() - 17);
            final BluetoothDevice device = getDevice(address);
            bluetoothConnectionService.connect(device);
            try {
                // busy wait is ugly, I think this needs the EventBus (see unused Handler in this class)
                while (bluetoothConnectionService.getState() != BluetoothConnectionService.STATE_CONNECTED) { /* temporary */}
                bluetoothConnectionService.writeAllContacts();
            } catch (final IOException e) {
                e.printStackTrace();
            }


            // Create the result Intent and include the MAC address
            final Intent intent = new Intent();
            intent.putExtra(EXTRA_DEVICE_ADDRESS, address);


            // Set result and finish this Activity
            setResult(Activity.RESULT_OK, intent);
            finish();

        }
    };
    private ArrayAdapter<String> newDevicesArrayAdapter;
    // Create a BroadcastReceiver for ACTION_FOUND.
    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        public void onReceive(final Context context, final Intent intent) {
            final String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                final BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // If it's already paired, skip it, because it's been listed already
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                    newDevices.add(device);
                    newDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                }
                // When discovery is finished, change the Activity title
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                setTitle("select_device");
                if (newDevicesArrayAdapter.getCount() == 0) {
                    newDevicesArrayAdapter.add("none_found");
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

        // Register for broadcasts when a device is discovered.
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(broadcastReceiver, filter);

        setContentView(R.layout.activity_bluetoothconection);

        // Set result CANCELED in case the user backs out
        setResult(Activity.RESULT_CANCELED);

        // SetUp bluetooth adapter
        setUp();

        // Initialize array adapters. One for already paired devices and
        // one for newly discovered devices
        pairedDevicesArrayAdapter =
                new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        newDevicesArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);

        // Find and set up the ListView for paired devices
        final ListView pairedListView = (ListView) findViewById(R.id.pairedlist);
        pairedListView.setAdapter(pairedDevicesArrayAdapter);
        pairedListView.setOnItemClickListener(mDeviceClickListener);


        // Find and set up the ListView for newly discovered devices
        final ListView newDevicesListView = (ListView) findViewById(R.id.discoveredlist);
        newDevicesListView.setAdapter(newDevicesArrayAdapter);
        newDevicesListView.setOnItemClickListener(mDeviceClickListener);


        // Register for broadcasts when discovery has finished
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        this.registerReceiver(broadcastReceiver, filter);

        // Get the serviceLocator.
        final Intent intent = getIntent();
        final IServiceLocator serviceLocator = (IServiceLocator) intent.getSerializableExtra(ConstantKeywords.SERVICE_LOCATOR);

        // Get the BluetoothConnectionService.
        this.bluetoothConnectionService = (BluetoothConnectionService) serviceLocator.getBluetoothHandler();

        this.newDevices = new HashSet<>();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Don't forget to unregister the ACTION_FOUND receiver.
        breakDown();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onStart() {
        super.onStart();
        enableBluetooth();
        bluetoothConnectionService.start();
        // Get the Paired Devices list
        queryPairedDevices();
        discoverDevices();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onStop() {
        super.onStop();
        bluetoothConnectionService.stop();
    }

    /**
     * Sets up a bluetoothAdapter if it's supported and handles the problem when it's not.
     */
    public void setUp() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            // consequence for device not supporting bluetooth
            setResult(Activity.RESULT_CANCELED);
            finish();
        }
    }

    /**
     * Deletes bluetoothadapter and unregisters broadcastreceiver
     */
    public void breakDown() {
        unregisterReceiver(broadcastReceiver);
        bluetoothAdapter = null;
    }

    /**
     * Enables bluetooth.
     */
    public void enableBluetooth() {
        if (!bluetoothAdapter.isEnabled()) {
            final Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_CODE_ENABLE_BLUETOOTH);
        }
    }

    /**
     * Checks bluetooth state.
     */
    public void checkBluetoothState() {
        final Intent checkBtStateIntent = new Intent(BluetoothAdapter.ACTION_STATE_CHANGED);
        startActivityForResult(checkBtStateIntent, REQUEST_CODE_CHECK_BLUETOOTH_STATE);
    }

    /**
     * Lines up all paired devices.
     */
    public void queryPairedDevices() {
        pairedDevices = bluetoothAdapter.getBondedDevices();
        findViewById(R.id.pairedlist).setVisibility(View.VISIBLE);
        for (final BluetoothDevice device : pairedDevices) {
            pairedDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
        }
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
     * Sets the device to being discoverable.
     */
    public void setDiscoverable() {
        final Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, DISCOVERABILITY_TIME);
        startActivity(discoverableIntent);
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