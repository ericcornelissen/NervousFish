package com.nervousfish.nervousfish.service_locator;

import com.nervousfish.nervousfish.services.constants.IConstants;
import com.nervousfish.nervousfish.services.cryptography.IEncryptor;
import com.nervousfish.nervousfish.services.cryptography.IKeyGenerator;
import com.nervousfish.nervousfish.services.database.IDatabase;
import com.nervousfish.nervousfish.services.pairing.IBluetoothHandler;
import com.nervousfish.nervousfish.services.pairing.INFCHandler;
import com.nervousfish.nervousfish.services.pairing.IQRHandler;
import com.nervousfish.nervousfish.services.filesystem.IFileSystem;

import java.io.Serializable;

/**
 * Provides the interface that should be implemented by all Service Locators.
 */
public interface IServiceLocator extends Serializable {

    String getAndroidFilesDir();
    IDatabase getDatabase();
    IKeyGenerator getKeyGenerator();
    IEncryptor getEncryptor();
    IFileSystem getFileSystem();
    IConstants getConstants();
    IBluetoothHandler getBluetoothHandler();
    INFCHandler getNFCHandler();
    IQRHandler getQRHandler();

    /**
     * Registers the class specified to the EventBus
     *
     * @param object The Object that should be registed to the EventBus
     */
    void registerToEventBus(final Object object);

}
