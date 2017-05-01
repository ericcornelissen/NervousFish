package com.nervousfish.nervousfish.service_locator;

import com.nervousfish.nervousfish.modules.constants.Constants;
import com.nervousfish.nervousfish.modules.cryptography.EncryptorAdapter;
import com.nervousfish.nervousfish.modules.cryptography.KeyGeneratorAdapter;
import com.nervousfish.nervousfish.modules.database.GsonDatabaseAdapter;
import com.nervousfish.nervousfish.modules.exploring.AndroidBluetoothAdapter;
import com.nervousfish.nervousfish.modules.exploring.AndroidNFCAdapter;
import com.nervousfish.nervousfish.modules.exploring.ZXingQRAdapter;
import com.nervousfish.nervousfish.modules.filesystem.AndroidFileSystemAdapter;

/**
 * Used to make a new Service Locator.
 * We use this class so that all modules of the ServiceLocator are initialized (they must be final) and all modules have a reference to a reference to the service locator
 */
class ServiceLocatorBridge implements IServiceLocatorBridge {
    private final IServiceLocator serviceLocator;

    /**
     * Creates a new {@link IServiceLocator}
     * @return An interface containing the methods that may be used on the newly constructed {@link IServiceLocator}
     */
    static IServiceLocator newInstance() {
        return (new ServiceLocatorBridge()).serviceLocator;
    }

    /**
     * Prevent initialization from other classes
     */
    private ServiceLocatorBridge() {
        this.serviceLocator = new ServiceLocator(
                GsonDatabaseAdapter.newInstance(this).get(),
                KeyGeneratorAdapter.newInstance(this).get(),
                EncryptorAdapter.newInstance(this).get(),
                AndroidFileSystemAdapter.newInstance(this).get(),
                Constants.newInstance(this).get(),
                AndroidBluetoothAdapter.newInstance(this).get(),
                AndroidNFCAdapter.newInstance(this).get(),
                ZXingQRAdapter.newInstance(this).get()
        );
    }

    /**
     * @return The {@link IServiceLocator} of this class
     */
    public IServiceLocator getServiceLocator() {
        return this.serviceLocator;
    }
}
