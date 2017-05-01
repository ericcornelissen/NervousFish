package com.nervousfish.nervousfish.service_locator;

import com.nervousfish.nervousfish.modules.constants.Constants;
import com.nervousfish.nervousfish.modules.cryptography.EncryptorAdapter;
import com.nervousfish.nervousfish.modules.cryptography.KeyGeneratorAdapter;
import com.nervousfish.nervousfish.modules.database.GsonDatabaseAdapter;
import com.nervousfish.nervousfish.modules.exploring.AndroidBluetoothAdapter;
import com.nervousfish.nervousfish.modules.exploring.AndroidNFCAdapter;
import com.nervousfish.nervousfish.modules.exploring.AndroidQRAdapter;
import com.nervousfish.nervousfish.modules.filesystem.AndroidFileSystemAdapter;
import com.nervousfish.nervousfish.util.commands.IServiceLocatorCreatedCommand;

/**
 * Used to make a new Service Locator.
 * We use this class so that all modules of the ServiceLocator are initialized (they must be final) and all modules have a reference to a reference to the service locator
 * A callback is used so that
 */
class ServiceLocatorBridge implements IServiceLocatorBridge {
    private final IServiceLocator serviceLocator;

    ServiceLocatorBridge(final IServiceLocatorCreatedCommand resultCallback) {
        this.serviceLocator = new ServiceLocator(
                GsonDatabaseAdapter.newInstance(this).get(),
                KeyGeneratorAdapter.newInstance(this).get(),
                EncryptorAdapter.newInstance(this).get(),
                AndroidFileSystemAdapter.newInstance(this).get(),
                Constants.newInstance(this).get(),
                AndroidBluetoothAdapter.newInstance(this).get(),
                AndroidNFCAdapter.newInstance(this).get(),
                AndroidQRAdapter.newInstance(this).get()
        );
        resultCallback.execute(serviceLocator);
    }

    public IServiceLocator getServiceLocator() {
        return this.serviceLocator;
    }
}
