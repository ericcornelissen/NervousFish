package com.nervousfish.nervousfish.service_locator;

import com.nervousfish.nervousfish.events.SLReadyEvent;
import com.nervousfish.nervousfish.modules.constants.Constants;
import com.nervousfish.nervousfish.modules.cryptography.EncryptorAdapter;
import com.nervousfish.nervousfish.modules.cryptography.KeyGeneratorAdapter;
import com.nervousfish.nervousfish.modules.database.GsonDatabaseAdapter;
import com.nervousfish.nervousfish.modules.filesystem.AndroidFileSystemAdapter;
import com.nervousfish.nervousfish.modules.pairing.DummyBluetoothHandler;
import com.nervousfish.nervousfish.modules.pairing.DummyNFCHandler;
import com.nervousfish.nervousfish.modules.pairing.DummyQRHandler;

import org.greenrobot.eventbus.EventBus;

/**
 * Used to make a new Service Locator.
 * We use this class so that all modules of the ServiceLocator are initialized (they must be final) and all modules have a reference to a reference to the service locator
 */
final class ServiceLocatorCreator implements IServiceLocatorCreator {
    private final IServiceLocator serviceLocator;

    /**
     * Prevent initialization from other classes
     */
    private ServiceLocatorCreator() {
        this.serviceLocator = new ServiceLocator(
                GsonDatabaseAdapter.newInstance(this).get(),
                KeyGeneratorAdapter.newInstance(this).get(),
                EncryptorAdapter.newInstance(this).get(),
                AndroidFileSystemAdapter.newInstance(this).get(),
                Constants.newInstance(this).get(),
                DummyBluetoothHandler.newInstance(this).get(),
                DummyNFCHandler.newInstance(this).get(),
                DummyQRHandler.newInstance(this).get()
        );
    }

    /**
     * Creates a new {@link IServiceLocator}
     *
     * @return An interface containing the methods that may be used on the newly constructed {@link IServiceLocator}
     */
    static IServiceLocator newInstance() {
        final IServiceLocator tmp = new ServiceLocatorCreator().serviceLocator;
        EventBus.getDefault().post(new SLReadyEvent());
        return tmp;
    }

    /**
     * @return The {@link IServiceLocator} of this class
     */
    public IServiceLocator getServiceLocator() {
        return this.serviceLocator;
    }
}
