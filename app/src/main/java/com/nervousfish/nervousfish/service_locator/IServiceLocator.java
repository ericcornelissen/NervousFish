package com.nervousfish.nervousfish.service_locator;

import com.nervousfish.nervousfish.modules.constants.IConstants;
import com.nervousfish.nervousfish.modules.cryptography.IEncryptor;
import com.nervousfish.nervousfish.modules.cryptography.IKeyGenerator;
import com.nervousfish.nervousfish.modules.database.IDatabase;
import com.nervousfish.nervousfish.modules.pairing.IBluetoothHandler;
import com.nervousfish.nervousfish.modules.pairing.INfcHandler;
import com.nervousfish.nervousfish.modules.pairing.IQRHandler;
import com.nervousfish.nervousfish.modules.filesystem.IFileSystem;

import java.io.Serializable;

/**
 * Provides the interface that should be implemented by all Service Locators.
 *
 * <p>A service locator should be dependency injected into all activities that can use it to get access
 * to any service the service locator provides.</p>
 */
@SuppressWarnings("checkstyle:javadocmethod")
public interface IServiceLocator extends Serializable {

    String getAndroidFilesDir();

    IDatabase getDatabase();

    IKeyGenerator getKeyGenerator();

    IEncryptor getEncryptor();

    IFileSystem getFileSystem();

    IConstants getConstants();

    IBluetoothHandler getBluetoothHandler();

    INfcHandler getNFCHandler();

    IQRHandler getQRHandler();

    /**
     * Registers the class specified to the EventBus
     *
     * @param object The Object that should be registed to the EventBus
     */
    void registerToEventBus(Object object);

    /**
     * Unregisters the class specified from the EventBus
     *
     * @param object The Object that should be unregistered from the EventBus
     */
    void unregisterFromEventBus(Object object);

    /**
     * Posts a message on the EventBus
     * @param message The message that should be posted
     */
    void postOnEventBus(Object message);
}
