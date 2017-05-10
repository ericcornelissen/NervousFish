package com.nervousfish.nervousfish.service_locator;

import com.nervousfish.nervousfish.modules.IModule;
import com.nervousfish.nervousfish.modules.constants.Constants;
import com.nervousfish.nervousfish.modules.constants.IConstants;
import com.nervousfish.nervousfish.modules.cryptography.EncryptorAdapter;
import com.nervousfish.nervousfish.modules.cryptography.IEncryptor;
import com.nervousfish.nervousfish.modules.cryptography.IKeyGenerator;
import com.nervousfish.nervousfish.modules.cryptography.KeyGeneratorAdapter;
import com.nervousfish.nervousfish.modules.database.GsonDatabaseAdapter;
import com.nervousfish.nervousfish.modules.database.IDatabase;
import com.nervousfish.nervousfish.modules.filesystem.AndroidFileSystemAdapter;
import com.nervousfish.nervousfish.modules.filesystem.IFileSystem;
import com.nervousfish.nervousfish.modules.pairing.DummyBluetoothHandler;
import com.nervousfish.nervousfish.modules.pairing.DummyNFCHandler;
import com.nervousfish.nervousfish.modules.pairing.DummyQRHandler;
import com.nervousfish.nervousfish.modules.pairing.IBluetoothHandler;
import com.nervousfish.nervousfish.modules.pairing.INFCHandler;
import com.nervousfish.nervousfish.modules.pairing.IQRHandler;

import org.greenrobot.eventbus.EventBus;

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
     */
    @SuppressWarnings("checkstyle:parameternumber")
    ServiceLocator() {
        this.constants = Constants.newInstance(this).get();
        this.fileSystem = AndroidFileSystemAdapter.newInstance(this).get();
        this.database = GsonDatabaseAdapter.newInstance(this).get();
        this.keyGenerator = KeyGeneratorAdapter.newInstance(this).get();
        this.encryptor = EncryptorAdapter.newInstance(this).get();
        this.bluetoothHandler = DummyBluetoothHandler.newInstance(this).get();
        this.nfcHandler = DummyNFCHandler.newInstance(this).get();
        this.qrHandler = DummyQRHandler.newInstance(this).get();
    }

    /**
     * {@inheritDoc}
     */
    public IDatabase getDatabase() {
        checkModule(this.database, "database");
        return this.database;
    }

    /**
     * {@inheritDoc}
     */
    public IKeyGenerator getKeyGenerator() {
        checkModule(this.keyGenerator, "keyGenerator");
        return this.keyGenerator;
    }

    /**
     * {@inheritDoc}
     */
    public IEncryptor getEncryptor() {
        checkModule(this.encryptor, "encryptor");
        return this.encryptor;
    }

    /**
     * {@inheritDoc}
     */
    public IFileSystem getFileSystem() {
        checkModule(this.fileSystem, "fileSystem");
        return this.fileSystem;
    }

    /**
     * {@inheritDoc}
     */
    public IConstants getConstants() {
        checkModule(this.constants, "constants");
        return this.constants;
    }

    /**
     * {@inheritDoc}
     */
    public IBluetoothHandler getBluetoothHandler() {
        checkModule(this.bluetoothHandler, "bluetoothHandler");
        return this.bluetoothHandler;
    }

    /**
     * {@inheritDoc}
     */
    public INFCHandler getNFCHandler() {
        checkModule(this.nfcHandler, "nfcHandler");
        return this.nfcHandler;
    }

    /**
     * {@inheritDoc}
     */
    public IQRHandler getQRHandler() {
        checkModule(this.qrHandler, "qrHandler");
        return this.qrHandler;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void registerToEventBus(final Object object) {
        EventBus.getDefault().register(object);
    }

    private void checkModule(final IModule module, final String moduleName) {
        if (module == null) {
            // TODO: log this when the logging branch is merged
            throw new ModuleNotFoundException("The module \"" + moduleName + "\" is used before it is defined");
        }
    }

    /**
     * Thrown when a module was called before it was initialized.
     */
    private class ModuleNotFoundException extends RuntimeException {
        /**
         * Constructs a new exception to make clear that a module was requested before it was initialized.
         * @param message A message describing in more detail what happened
         */
        ModuleNotFoundException(final String message) {
            super(message);
        }
    }
}
