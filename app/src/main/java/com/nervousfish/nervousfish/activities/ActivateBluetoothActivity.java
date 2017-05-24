package com.nervousfish.nervousfish.activities;


import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
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
@SuppressWarnings("checkstyle:MultipleStringLiterals")
//We want to use a string multiple times
public final class ActivateBluetoothActivity extends Activity {
    //Request and result codes
    static final int RESULT_CODE_FINISH_BLUETOOTH_ACTIVITY = 6;
    private static final int REQUEST_CODE_ENABLE_BLUETOOTH = 100;
    private static final int REQUEST_CODE_BLUETOOTH_ACTIVITY = 111;

    private static final Logger LOGGER = LoggerFactory.getLogger("ActivateBluetoothActivity");
    private IServiceLocator serviceLocator;
    private BluetoothAdapter bluetoothAdapter;

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

        // Setup bluetooth adapter
        setup();

        if (bluetoothAdapter.isEnabled()) {
            final Intent intentConnection = new Intent(this, BluetoothConnectionActivity.class);
            intentConnection.putExtra(ConstantKeywords.SERVICE_LOCATOR, serviceLocator);
            startActivityForResult(intentConnection, REQUEST_CODE_BLUETOOTH_ACTIVITY);
        }

        final ImageButton backButton = (ImageButton) findViewById(R.id.backButtonChange);
        backButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(final View v) {
                finish();
            }
        });
    }

    /**
     * Sets up a {@link BluetoothAdapter}. If Bluetooth isn't supported the activity is canceled.
     */
    public void setup() {
        this.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (this.bluetoothAdapter == null) {
            // consequence for device not supporting bluetooth
            setResult(Activity.RESULT_CANCELED);
            finish();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onStart() {
        super.onStart();
        enableBluetooth();
    }

    /**
     * Enables bluetooth.
     */
    public void enableBluetooth() {
        if (!bluetoothAdapter.isEnabled()) {
            LOGGER.info("Requesting to enable Bluetooth");
            final Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            this.startActivityForResult(enableIntent, REQUEST_CODE_ENABLE_BLUETOOTH);
            LOGGER.info("Request to enable Bluetooth sent");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_ENABLE_BLUETOOTH) {
            final Intent intent = new Intent(this, ActivateBluetoothActivity.class);
            intent.putExtra(ConstantKeywords.SERVICE_LOCATOR, serviceLocator);
            startActivityForResult(intent, REQUEST_CODE_BLUETOOTH_ACTIVITY);
        } else if (requestCode == REQUEST_CODE_ENABLE_BLUETOOTH) {
            new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText(getString(R.string.activating_bluetooth_went_wrong))
                    .setContentText(getString(R.string.activating_bluetooth_went_wrong_explanation))
                    .setConfirmText(getString(R.string.dialog_ok))
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(final SweetAlertDialog sDialog) {
                            sDialog.dismiss();
                            finish();
                        }
                    })
                    .show();
        } else if (resultCode == RESULT_CODE_FINISH_BLUETOOTH_ACTIVITY && requestCode == REQUEST_CODE_BLUETOOTH_ACTIVITY) {
            finish();
        }
    }
}
