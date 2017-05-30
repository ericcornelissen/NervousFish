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
 * Used as an in-between activity to the BluetoothConnectionActivity to activate the Bluetooth
 * before going to that activity.
 */
public final class ActivateBluetoothActivity extends Activity {

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
        this.setContentView(R.layout.activity_activate_bluetooth);

        final Intent intent = this.getIntent();
        this.serviceLocator = (IServiceLocator) intent.getSerializableExtra(ConstantKeywords.SERVICE_LOCATOR);

        // Setup bluetooth adapter
        this.setupBluetoothAdapter();
        if (this.bluetoothAdapter.isEnabled()) {
            final Intent intentConnection = new Intent(this, BluetoothConnectionActivity.class);
            intentConnection.putExtra(ConstantKeywords.SERVICE_LOCATOR, this.serviceLocator);
            this.startActivityForResult(intentConnection, ActivateBluetoothActivity.REQUEST_CODE_BLUETOOTH_ACTIVITY);
        }

        final ImageButton backButton = (ImageButton) findViewById(R.id.back_button_change);
        backButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(final View v) {
                finish();
            }
        });

        LOGGER.info("ActivateBluetoothActivity created");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onStart() {
        super.onStart();
        this.enableBluetooth();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LOGGER.debug(Integer.toString(resultCode));
        LOGGER.debug(Integer.toString(requestCode));

        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_ENABLE_BLUETOOTH) {
            this.activateBluetooth();
        } else if (requestCode == REQUEST_CODE_ENABLE_BLUETOOTH) {
            this.activationFailed();
        } else if (resultCode == RESULT_CODE_FINISH_BLUETOOTH_ACTIVITY && requestCode == REQUEST_CODE_BLUETOOTH_ACTIVITY) {
            this.finish();
        }
    }

    /**
     * Sets up a {@link BluetoothAdapter}. If Bluetooth isn't supported the activity is canceled.
     */
    public void setupBluetoothAdapter() {
        this.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (this.bluetoothAdapter == null) {
            // Cancel activity if Bluetooth could not be disabled.
            this.setResult(Activity.RESULT_CANCELED);
            this.finish();
        }
    }

    /**
     * Enable Bluetooth.
     */
    public void enableBluetooth() {
        if (!this.bluetoothAdapter.isEnabled()) {
            LOGGER.info("Requesting to enable Bluetooth");
            final Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            this.startActivityForResult(intent, ActivateBluetoothActivity.REQUEST_CODE_ENABLE_BLUETOOTH);
            LOGGER.info("Request to enable Bluetooth sent");
        }
    }

    /**
     * Cancel the {@link ActivateBluetoothActivity} activity.
     *
     * @param v The view initiating the call.
     */
    public void onCancelActivateBluetoothActivityClick(final View v) {
        this.finish();
    }

    /**
     * Continue to next activity when Bluetooth has been enabled.
     */
    private void activateBluetooth() {
        final Intent intent = new Intent(this, ActivateBluetoothActivity.class);
        intent.putExtra(ConstantKeywords.SERVICE_LOCATOR, this.serviceLocator);
        this.startActivityForResult(intent, ActivateBluetoothActivity.REQUEST_CODE_BLUETOOTH_ACTIVITY);
    }

    /**
     * Show dialog notifying the user that activating Bluetooth failed.
     */
    private void activationFailed() {
        new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText(getString(R.string.activating_bluetooth_went_wrong))
                .setContentText(getString(R.string.activating_bluetooth_went_wrong_explanation))
                .setConfirmText(getString(R.string.dialog_ok))
                .setConfirmClickListener(new FailedOnClickListener())
                .show();
    }

    private final class FailedOnClickListener implements SweetAlertDialog.OnSweetClickListener {

        /**
         * {@inheritDoc}
         */
        @Override
        public void onClick(final SweetAlertDialog sweetAlertDialog) {
            sweetAlertDialog.dismiss();
            finish();
        }

    }

}
