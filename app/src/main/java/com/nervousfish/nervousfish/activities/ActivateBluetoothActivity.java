package com.nervousfish.nervousfish.activities;


import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import com.nervousfish.nervousfish.ConstantKeywords;
import com.nervousfish.nervousfish.R;
import com.nervousfish.nervousfish.service_locator.IServiceLocator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Used as an in-between screen to the BluetoothConnectionActivity to activate the
 * Bluetooth before we go to that activity.
 */
public final class ActivateBluetoothActivity extends Activity {
    private static final Logger LOGGER = LoggerFactory.getLogger("ActivateBluetoothActivity");
    private IServiceLocator serviceLocator;
    private BluetoothAdapter bluetoothAdapter;

    //Request codes
    private static final int REQUEST_CODE_ENABLE_BLUETOOTH = 100;

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Intent intent = getIntent();
        this.serviceLocator = (IServiceLocator) intent.getSerializableExtra(ConstantKeywords.SERVICE_LOCATOR);
        setContentView(R.layout.activity_activate_bluetooth);
        LOGGER.info("ActivateBluetoothActivity created");

        // SetUp bluetooth adapter
        setUp();

        if (bluetoothAdapter.isEnabled()) {
            final Intent intentConnection = new Intent(ActivateBluetoothActivity.this, BluetoothConnectionActivity.class);
            intentConnection.putExtra(ConstantKeywords.SERVICE_LOCATOR, serviceLocator);
            startActivity(intentConnection);
        }

        final ImageButton backButton = (ImageButton) findViewById(R.id.backButtonChange);
        backButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(final View v) {
                finish();
            }
        });
    }

    /**
     * Sets up a bluetoothAdapter if it's supported and handles the problem when it's not.
     */
    public void setUp() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            // consequence for device not supporting bluetooth
            setResult(Activity.RESULT_CANCELED);
            Log.d("test", "11111");
            finish();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onStart() {
        super.onStart();

        Log.d("test", "onStart");
        enableBluetooth();
    }

    /**
     * Enables bluetooth.
     */
    public void enableBluetooth() {
        LOGGER.info("Requesting to enable Bluetooth");
        if (!bluetoothAdapter.isEnabled()) {
            final Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            this.startActivityForResult(enableIntent, REQUEST_CODE_ENABLE_BLUETOOTH);
            LOGGER.info("Request to enable Bluetooth sent");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && requestCode == REQUEST_CODE_ENABLE_BLUETOOTH){
            final Intent intent = new Intent(this, ActivateBluetoothActivity.class);
            intent.putExtra(ConstantKeywords.SERVICE_LOCATOR, serviceLocator);
            startActivity(intent);
        } else{
            Log.d("test", "Neenee");
        }
    }
}
