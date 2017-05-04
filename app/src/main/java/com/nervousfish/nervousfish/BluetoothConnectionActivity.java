package com.nervousfish.nervousfish;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.util.Set;

/**
 * Created by Kilian on 2/05/2017.
 * This Bluetooth activity class establishes and manages a bluetooth connection.
 */

public class BluetoothConnectionActivity extends AppCompatActivity {
    private BluetoothAdapter bluetoothAdapter;
    private static final int REQUEST_CODE_ENABLE_BLUETOOTH = 100;
    private static final int REQUEST_CODE_CHECK_BLUETOOTH_STATE = 101;
    private Set<BluetoothDevice> pairedDevices;

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Register for broadcasts when a device is discovered.
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mReceiver, filter);
    }

    // Create a BroadcastReceiver for ACTION_FOUND.
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Discovery has found a device. Get the BluetoothDevice
                // object and its info from the Intent.
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                //do something with device.getAddress() etc.
            }
        }
    };

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Don't forget to unregister the ACTION_FOUND receiver.
        unregisterReceiver(mReceiver);
    }

    /**
     * Sets up a bluetoothAdapter if it's supported and handles the problem when it's not.
     */
    public void setUp() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            // consequence for device not supporting bluetooth
        } else {
            enableBluetooth();

            // Proceed by first checking the already bonded devices list
            // to see if the intended partner device has already been bonded to
           queryPairedDevices();

            // Something like if pairedDevices.contains(whatWeWant) do
            // Find out the MAC address and connect to it (while canceling the discovery of course)

            checkBluetoothState();

            // start discovering
            bluetoothAdapter.startDiscovery();

            // code that handles discovered devices

            // cancel discovery before starting up connection!
            bluetoothAdapter.cancelDiscovery();

        }
    }

    /**
     * Enables bluetooth.
     */
    public void enableBluetooth() {
        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_CODE_ENABLE_BLUETOOTH);
        }
    }

    /**
     * Checks bluetooth state.
     */
    public void checkBluetoothState() {
         Intent checkBtStateIntent = new Intent(BluetoothAdapter.ACTION_STATE_CHANGED);
         startActivityForResult(checkBtStateIntent, REQUEST_CODE_CHECK_BLUETOOTH_STATE);
    }

    /**
     * Lines up all paired devices.
     */
    public void queryPairedDevices() {
        pairedDevices = bluetoothAdapter.getBondedDevices();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case    REQUEST_CODE_ENABLE_BLUETOOTH:
                if (resultCode == RESULT_OK) {
                    // approve
                }
                break;
            default:

                break;
        }
    }

}
