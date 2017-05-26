package com.nervousfish.nervousfish.service_locator;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;

import com.nervousfish.nervousfish.ConstantKeywords;
import com.nervousfish.nervousfish.activities.LoginActivity;
import com.nervousfish.nervousfish.modules.pairing.AndroidBluetoothService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The main activity class that shows a list of all people with their public keys
 */
public final class EntryActivity extends Activity {

    private static final Logger LOGGER = LoggerFactory.getLogger("EntryActivity");

    private AndroidBluetoothService service;
    private IServiceLocator serviceLocator;

    /**
     * Creates the new activity, should only be called by Android
     *
     * @param savedInstanceState Don't touch this
     */
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final String androidFileDir = this.getFilesDir().getPath();
        serviceLocator = new ServiceLocator(androidFileDir);

        LOGGER.info("EntryActivity created");

        final Intent serviceIntent = new Intent(this, AndroidBluetoothService.class);
        bindService(serviceIntent, connection, Context.BIND_AUTO_CREATE);

        final Intent intent = new Intent(this, LoginActivity.class);
        intent.putExtra(ConstantKeywords.SERVICE_LOCATOR, serviceLocator);
        this.startActivity(intent);
    }

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            AndroidBluetoothService.LocalBinder binder = (AndroidBluetoothService.LocalBinder) service;
            EntryActivity.this.service = binder.getService();
            binder.getService().setServiceLocator(EntryActivity.this.serviceLocator);
            binder.getService().start();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };
}
