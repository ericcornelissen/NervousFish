package com.nervousfish.nervousfish.service_locator;

import com.nervousfish.nervousfish.modules.constants.IConstants;
import com.nervousfish.nervousfish.modules.cryptography.IEncryptor;
import com.nervousfish.nervousfish.modules.cryptography.IKeyGenerator;
import com.nervousfish.nervousfish.modules.database.IDatabase;
import com.nervousfish.nervousfish.modules.pairing.IBluetoothHandler;
import com.nervousfish.nervousfish.modules.pairing.INFCHandler;
import com.nervousfish.nervousfish.modules.pairing.IQRHandler;
import com.nervousfish.nervousfish.modules.filesystem.IFileSystem;

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
