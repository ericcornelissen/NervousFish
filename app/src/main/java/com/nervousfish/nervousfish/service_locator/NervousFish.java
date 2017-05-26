package com.nervousfish.nervousfish.service_locator;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.nervousfish.nervousfish.ConstantKeywords;
import com.nervousfish.nervousfish.modules.pairing.AndroidBluetoothService;

/**
 * The controller of the NervousFish application
 */
public final class NervousFish extends Application implements INervousFish {
    private static NervousFish instance;
    private AndroidBluetoothService bluetoothService;
    private boolean bound;
    private Runnable onServiceBoundRunnable;
    private static Context context;

    public static synchronized NervousFish getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Intent serviceIntent = new Intent(this, AndroidBluetoothService.class);
        bindService(serviceIntent, connection, Context.BIND_AUTO_CREATE);

        this.context = getApplicationContext();
    }

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            AndroidBluetoothService.LocalBinder binder = (AndroidBluetoothService.LocalBinder) service;
            bluetoothService = binder.getService();
            bound = true;
            if (onServiceBoundRunnable != null) {
                onServiceBoundRunnable.run();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            bound = false;
        }
    };

    public AndroidBluetoothService getBluetoothService() {
        return bluetoothService;
    }

    public void setOnServiceBound(final Runnable runnable) {
        if (bound) {
            runnable.run();
        } else {
            this.onServiceBoundRunnable = runnable;
        }
    }

    public static Context getContext() {
        return context;
    }
}
