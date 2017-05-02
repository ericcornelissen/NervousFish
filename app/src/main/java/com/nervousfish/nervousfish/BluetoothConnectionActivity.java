package com.nervousfish.nervousfish;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;

/**
 * Created by Kilian on 2/05/2017.
 */

public class BluetoothConnectionActivity extends Activity {
    private BluetoothAdapter bluetoothAdapter;
    private Integer REQUEST_CODE = 200;

    public void setUp() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(bluetoothAdapter == null) {
            // consequence for device not supporting bluetooth
        }

        if(!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_CODE);

            Intent checkBtStateIntent = new Intent(BluetoothAdapter.ACTION_STATE_CHANGED);
            startActivityForResult(checkBtStateIntent, REQUEST_CODE);
        }
    }
}
