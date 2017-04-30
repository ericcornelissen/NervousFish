package com.nervousfish.nervousfish.service_locator;

import com.nervousfish.nervousfish.modules.constants.IConstants;
import com.nervousfish.nervousfish.modules.cryptography.IEncryptor;
import com.nervousfish.nervousfish.modules.cryptography.IKeyGenerator;
import com.nervousfish.nervousfish.modules.database.IDatabase;
import com.nervousfish.nervousfish.modules.exploring.IBluetoothHandler;
import com.nervousfish.nervousfish.modules.exploring.INFCHandler;
import com.nervousfish.nervousfish.modules.exploring.IQRHandler;
import com.nervousfish.nervousfish.modules.filesystem.IFileSystem;

public final class ServiceLocator implements IServiceLocator {
    private final IDatabase database;
    private final IKeyGenerator keyGenerator;
    private final IEncryptor encryptor;
    private final IFileSystem fileSystem;
    private final IConstants constants;
    private final IBluetoothHandler bluetoothHandler;
    private final INFCHandler nfcHandler;
    private final IQRHandler qrHandler;

    ServiceLocator(
            final IDatabase database,
            final IKeyGenerator keyGenerator,
            final IEncryptor encryptor,
            final IFileSystem fileSystem,
            final IConstants constants,
            final IBluetoothHandler bluetoothHandler,
            final INFCHandler nfcHandler,
            final IQRHandler qrHandler
    ) {
        this.database = database;
        this.keyGenerator = keyGenerator;
        this.encryptor = encryptor;
        this.fileSystem = fileSystem;
        this.constants = constants;
        this.bluetoothHandler = bluetoothHandler;
        this.nfcHandler = nfcHandler;
        this.qrHandler = qrHandler;
    }

    /**
     * {@inheritDoc}
     */
    public IDatabase getDatabase() {
        return this.database;
    }

    /**
     * {@inheritDoc}
     */
    public IKeyGenerator getKeyGenerator() {
        return this.keyGenerator;
    }

    /**
     * {@inheritDoc}
     */
    public IEncryptor getEncryptor() {
        return this.encryptor;
    }

    /**
     * {@inheritDoc}
     */
    public IFileSystem getFileSystem() {
        return this.fileSystem;
    }

    /**
     * {@inheritDoc}
     */
    public IConstants getConstants() {
        return this.constants;
    }

    /**
     * {@inheritDoc}
     */
    public IBluetoothHandler getBluetoothHandler() {
        return this.bluetoothHandler;
    }

    /**
     * {@inheritDoc}
     */
    public INFCHandler getNFCHandler() {
        return this.nfcHandler;
    }

    /**
     * {@inheritDoc}
     */
    public IQRHandler getQRHandler() {
        return this.qrHandler;
    }
}
