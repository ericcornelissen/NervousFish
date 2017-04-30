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
 * Created by jverb on 4/30/2017.
 */

public class ServiceLocatorBridge {
    private final IServiceLocator serviceLocator;

    public ServiceLocatorBridge(final IServiceLocatorCreatedCommand resultCallback) {
        this.serviceLocator = new ServiceLocator(
                GsonDatabaseAdapter.register(this),
                KeyGeneratorAdapter.register(this),
                EncryptorAdapter.register(this),
                AndroidFileSystemAdapter.register(this),
                Constants.register(this),
                AndroidBluetoothAdapter.register(this),
                AndroidNFCAdapter.register(this),
                AndroidQRAdapter.register(this)
        );
        resultCallback.execute(serviceLocator);
    }

    public IServiceLocator getServiceLocator() {
        return this.serviceLocator;
    }
}
