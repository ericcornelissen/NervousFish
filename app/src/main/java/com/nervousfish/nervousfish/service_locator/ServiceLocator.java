package com.nervousfish.nervousfish.service_locator;

import com.nervousfish.nervousfish.modules.constants.IConstants;
import com.nervousfish.nervousfish.modules.cryptography.IEncryptor;
import com.nervousfish.nervousfish.modules.cryptography.IKeyGenerator;
import com.nervousfish.nervousfish.modules.database.IDatabase;
import com.nervousfish.nervousfish.modules.pairing.IBluetoothHandler;
import com.nervousfish.nervousfish.modules.pairing.INFCHandler;
import com.nervousfish.nervousfish.modules.pairing.IQRHandler;
import com.nervousfish.nervousfish.modules.filesystem.IFileSystem;

/**
 * Manages all modules and provides access to them.
 */
final class ServiceLocator implements IServiceLocator {
    private final IDatabase database;
    private final IKeyGenerator keyGenerator;
    private final IEncryptor encryptor;
    private final IFileSystem fileSystem;
    private final IConstants constants;
    private final IBluetoothHandler bluetoothHandler;
    private final INFCHandler nfcHandler;
    private final IQRHandler qrHandler;

    /**
     * Package-private constructor of the service locator
     * @param database The {@link IDatabase} module
     * @param keyGenerator The {@link IKeyGenerator} module
     * @param encryptor The {@link IEncryptor} module
     * @param fileSystem The {@link IFileSystem} module
     * @param constants The {@link IConstants} module
     * @param bluetoothHandler The {@link IBluetoothHandler} module
     * @param nfcHandler The {@link INFCHandler} module
     * @param qrHandler The {@link IQRHandler} module
     */
    @SuppressWarnings("checkstyle:parameternumber")
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
