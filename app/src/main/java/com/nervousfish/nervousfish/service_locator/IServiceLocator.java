package com.nervousfish.nervousfish.service_locator;

import com.nervousfish.nervousfish.modules.constants.IConstants;
import com.nervousfish.nervousfish.modules.cryptography.IEncryptor;
import com.nervousfish.nervousfish.modules.cryptography.IKeyGenerator;
import com.nervousfish.nervousfish.modules.database.IDatabase;
import com.nervousfish.nervousfish.modules.exploring.IBluetoothHandler;
import com.nervousfish.nervousfish.modules.exploring.INFCHandler;
import com.nervousfish.nervousfish.modules.exploring.IQRHandler;
import com.nervousfish.nervousfish.modules.filesystem.IFileSystem;

public interface IServiceLocator {
    IDatabase getDatabase();
    IKeyGenerator getKeyGenerator();
    IEncryptor getEncryptor();
    IFileSystem getFileSystem();
    IConstants getConstants();
    IBluetoothHandler getBluetoothHandler();
    INFCHandler getNFCHandler();
    IQRHandler getQRHandler();
}
