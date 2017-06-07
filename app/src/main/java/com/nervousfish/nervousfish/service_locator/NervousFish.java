package com.nervousfish.nervousfish.service_locator;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.nervousfish.nervousfish.ConstantKeywords;
import com.nervousfish.nervousfish.activities.FirstUseActivity;
import com.nervousfish.nervousfish.activities.LoginActivity;
import com.nervousfish.nervousfish.data_objects.Profile;
import com.nervousfish.nervousfish.modules.database.IDatabase;
import com.nervousfish.nervousfish.modules.pairing.AndroidBluetoothService;
import com.nervousfish.nervousfish.modules.pairing.PairingWrapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * The controller of the NervousFish application. It provides an access point for the
 * {@link com.nervousfish.nervousfish.modules.pairing.IBluetoothHandlerService} and holds the
 * current global state of the application.
 */
public final class NervousFish extends Application implements INervousFish {

    private static final Logger LOGGER = LoggerFactory.getLogger("NervousFish");

    private static NervousFish instance;

    private final ServiceConnection connection = new ServiceConnectionImpl();
    private AndroidBluetoothService bluetoothService;
    private Runnable onServiceBoundRunnable;
    private IServiceLocator serviceLocator;
    private boolean bound;

    /**
     * @return The NervousFish {@link Application} instance
     */
    public static Context getInstance() {
        return NervousFish.instance;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onCreate() {
        super.onCreate();

        final Intent serviceIntent = new Intent(this, AndroidBluetoothService.class);
        this.bindService(serviceIntent, this.connection, Context.BIND_AUTO_CREATE);

        final String androidFileDir = this.getFilesDir().getPath();
        this.serviceLocator = new ServiceLocator(androidFileDir);

        List<Profile> profiles = new ArrayList<>();
        try {
            final IDatabase database = this.serviceLocator.getDatabase();
            profiles = database.getProfiles();
        } catch (IOException e) {
            LOGGER.error("IOException while getting profiles", e);
        }

        Intent intent = new Intent(this, LoginActivity.class);
        if (profiles.isEmpty()) {
            intent = new Intent(this, FirstUseActivity.class);
        }

        intent.putExtra(ConstantKeywords.SERVICE_LOCATOR, this.serviceLocator);
        this.startActivity(intent);

        NervousFish.instance = this;
    }

    /**
     * Warning: this method should only be used by {@link EntryActivity} to initialize the Service Locator
     * @return The global bluetooth service used for bluetooth connections
     */
    AndroidBluetoothService getBluetoothServiceWithinPackage() {
        return this.bluetoothService;
    }

    /**
     * @return The global bluetooth service used for bluetooth connections
     */
    public PairingWrapper<AndroidBluetoothService> getBluetoothService() {
        return new PairingWrapper<>(this.bluetoothService);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setOnBluetoothServiceBound(final Runnable runnable) {
        if (this.bound) {
            runnable.run();
        } else {
            this.onServiceBoundRunnable = runnable;
        }
    }

    private class ServiceConnectionImpl implements ServiceConnection {

        /**
         * {@inheritDoc}
         */
        @Override
        public void onServiceConnected(final ComponentName componentName, final IBinder service) {
            final AndroidBluetoothService.LocalBinder binder = (AndroidBluetoothService.LocalBinder) service;
            bluetoothService = binder.getService();
            bluetoothService.setServiceLocator(serviceLocator);
            bound = true;
            if (onServiceBoundRunnable != null) {
                onServiceBoundRunnable.run();
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void onServiceDisconnected(final ComponentName componentName) {
            bound = false;
        }

    }

}
