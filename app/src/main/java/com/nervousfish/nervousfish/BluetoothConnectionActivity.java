package com.nervousfish.nervousfish;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.nervousfish.nervousfish.data_objects.Contact;
import com.nervousfish.nervousfish.modules.pairing.BluetoothConnectionService;
import com.nervousfish.nervousfish.service_locator.IServiceLocator;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Set;

/**
 * Created by Kilian on 2/05/2017.
 * This Bluetooth activity class establishes and manages a bluetooth connection.
 */

@SuppressWarnings({"PMD.UnusedLocalVariable", "PMD.UnusedPrivateField", "PMD.SingularField",
        "PMD.AvoidFinalLocalVariable", "PMD.NullAssignment", "PMD.TooFewBranchesForASwitchStatement"})
// 1 + 2 +3)because it's used for storing, later it will also be accessed
// 4) This is taken from the Android manual on bluetooth :/
// 5) This is necessary for freeing up resources, also see end of 3
// 6) Will be larger when implemented

public class BluetoothConnectionActivity extends AppCompatActivity {

    // Message types sent from the BluetoothChatService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_DEVICE_NAME = 3;

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
    private ArrayAdapter<String> newDevicesArrayAdapter;
    private ArrayAdapter<String> pairedDevicesArrayAdapter;
    private IServiceLocator serviceLocator;
    private String connectedDeviceName = null; //string of the connected device name


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
                setProgressBarIndeterminateVisibility(false);
                setTitle("select_device");
                if (newDevicesArrayAdapter.getCount() == 0) {
                    final String noDevices = "none_found";
                    newDevicesArrayAdapter.add(noDevices);
                }
            }

        }
    };

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
            BluetoothDevice device = getDevice(address);
            bluetoothConnectionService.connect(device);


            // Create the result Intent and include the MAC address
            final Intent intent = new Intent();
            intent.putExtra(EXTRA_DEVICE_ADDRESS, address);


            // Set result and finish this Activity
            setResult(Activity.RESULT_OK, intent);
            finish();

        }
    };




    /**
     * {@inheritDoc}
     */
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Register for broadcasts when a device is discovered.
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(broadcastReceiver, filter);

        // Setup the window
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        // setContentView(R.layout.activity_device_list);

        // Set result CANCELED in case the user backs out
        setResult(Activity.RESULT_CANCELED);

        // SetUp bluetooth adapter
        setUp();

        // Initialize array adapters. One for already paired devices and
        // one for newly discovered devices
        // TODO: replace all the resources in the code below (All the R.something.something references)
        pairedDevicesArrayAdapter =
                new ArrayAdapter<>(this, R.layout.content_main);
        newDevicesArrayAdapter = new ArrayAdapter<String>(this, R.layout.content_main);

        // Initializes the button for discovering devices
        final Button discoveryButton = (Button) findViewById(R.id.fab);
        discoveryButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(final View v) {

                discoverDevices();
            }
        });



        // Find and set up the ListView for paired devices
        final ListView pairedListView = (ListView) findViewById(R.id.toolbar);
        pairedListView.setAdapter(pairedDevicesArrayAdapter);
        pairedListView.setOnItemClickListener(mDeviceClickListener);

        // Get the Paired Devices list
        queryPairedDevices();

        // Find and set up the ListView for newly discovered devices
        final ListView newDevicesListView = (ListView) findViewById(R.id.toolbar);
        newDevicesListView.setAdapter(newDevicesArrayAdapter);
        newDevicesListView.setOnItemClickListener(mDeviceClickListener);



        // Register for broadcasts when discovery has finished
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        this.registerReceiver(broadcastReceiver, filter);

        // Get the serviceLocator.
        final Intent intent = getIntent();
        this.serviceLocator = (IServiceLocator) intent.getSerializableExtra(ConstantKeywords.SERVICE_LOCATOR);

        // Get the BluetoothConnectionService.
        this.bluetoothConnectionService = (BluetoothConnectionService) serviceLocator.getBluetoothHandler();


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
            throw new UnsupportedOperationException();
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
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        for(BluetoothDevice device: pairedDevices) {
            pairedDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
        }
    }

    /**
     * Starts Discovering bluetooth devices
     */
    public void discoverDevices() {

        // Indicate scanning in the title
        setProgressBarIndeterminateVisibility(true);
        setTitle("scanning");

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
        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, DISCOVERABILITY_TIME);
        startActivity(discoverableIntent);
    }

    /**
     * Gets the device corresponding to the address from either the paired or the new devices.
     * @param address The MAC address corresponding to the device to be found
     * @return The BLuetoothDevice corresponding to the mac address.
     */
    private BluetoothDevice getDevice(String address) {
        for(BluetoothDevice device: pairedDevices) {
            if(device.getAddress().equals(address)) {
                return device;
            }
        }
        for(BluetoothDevice device: newDevices) {
            if(device.getAddress().equals(address)) {
                return device;
            }
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_ENABLE_BLUETOOTH:
                if (resultCode == RESULT_OK) {
                    bluetoothConnectionService.start();
                    return;
                }
                break;
            default:

                break;
        }
    }

    /**
     * The Handler that gets information back from the BluetoothChatService
     */
    private final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BluetoothConnectionService.STATE_CONNECTED:
                            //TODO: do iets met connected met evt device name
                            break;
                        case BluetoothConnectionService.STATE_CONNECTING:
                            //TODO: do iets met connecting
                            break;
                        case BluetoothConnectionService.STATE_LISTEN:
                        case BluetoothConnectionService.STATE_NONE:
                            //TODO: do iets met not connected
                            break;
                    }
                    break;
                case MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    ByteArrayInputStream in = new ByteArrayInputStream(readBuf);
                    ObjectInputStream is = null;
                    Contact temp = null;
                    try {
                        is = new ObjectInputStream(in);
                        temp = (Contact) is.readObject();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    if (temp != null) {
                        //TODO: save the contact in the db
                    }
                    break;
                case MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    connectedDeviceName = msg.getData().getString("device_name");
                    //TODO: iets met de device name???
                    break;
            }
        }
    };

}