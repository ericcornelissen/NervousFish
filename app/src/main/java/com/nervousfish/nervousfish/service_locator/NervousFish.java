package com.nervousfish.nervousfish.service_locator;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.nervousfish.nervousfish.modules.pairing.AndroidBluetoothService;
import com.nervousfish.nervousfish.modules.pairing.PairingWrapper;

/**
 * The controller of the NervousFish application
 */
public final class NervousFish extends Application implements INervousFish {
    private static NervousFish instance;

    private AndroidBluetoothService bluetoothService;
    private boolean bound;
    private Runnable onServiceBoundRunnable;
    private final ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(final ComponentName componentName, final IBinder service) {
            final AndroidBluetoothService.LocalBinder binder = (AndroidBluetoothService.LocalBinder) service;
            bluetoothService = binder.getService();
            bound = true;
            if (onServiceBoundRunnable != null) {
                onServiceBoundRunnable.run();
            }
        }

        @Override
        public void onServiceDisconnected(final ComponentName componentName) {
            bound = false;
        }
    };

    public static Context getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        final Intent serviceIntent = new Intent(this, AndroidBluetoothService.class);
        bindService(serviceIntent, connection, Context.BIND_AUTO_CREATE);

        NervousFish.instance = this;
    }

    /**
     * Warning: this method should only be used by {@link EntryActivity} to initialize the Service Locator
     * @return The global bluetooth service used for bluetooth connections
     */
    AndroidBluetoothService _getBluetoothService() {
        return bluetoothService;
    }

    /**
     * @return The global bluetooth service used for bluetooth connections
     */
    public PairingWrapper<AndroidBluetoothService> getBluetoothService() {
        return new PairingWrapper<>(bluetoothService);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setOnBluetoothServiceBound(final Runnable runnable) {
        if (bound) {
            runnable.run();
        } else {
            this.onServiceBoundRunnable = runnable;
        }
    }
}
