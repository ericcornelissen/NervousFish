package com.nervousfish.nervousfish.service_locator;

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
import com.nervousfish.nervousfish.modules.pairing.BluetoothConnectionService;
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

    private final String androidFilesDir;
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
    ServiceLocator(final String androidFilesDir) {
        this.androidFilesDir = androidFilesDir;
        this.constants = Constants.newInstance(this).get();
        this.fileSystem = AndroidFileSystemAdapter.newInstance(this).get();
        this.database = GsonDatabaseAdapter.newInstance(this).get();
        this.keyGenerator = KeyGeneratorAdapter.newInstance(this).get();
        this.encryptor = EncryptorAdapter.newInstance(this).get();
        this.bluetoothHandler = BluetoothConnectionService.newInstance(this).get();
        this.nfcHandler = DummyNFCHandler.newInstance(this).get();
        this.qrHandler = DummyQRHandler.newInstance(this).get();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getAndroidFilesDir() {
        this.assertExists(this.androidFilesDir, "androidFileDir");
        return this.androidFilesDir;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IDatabase getDatabase() {
        this.assertExists(this.database, "database");
        return this.database;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IKeyGenerator getKeyGenerator() {
        this.assertExists(this.keyGenerator, "keyGenerator");
        return this.keyGenerator;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IEncryptor getEncryptor() {
        this.assertExists(this.encryptor, "encryptor");
        return this.encryptor;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IFileSystem getFileSystem() {
        this.assertExists(this.fileSystem, "fileSystem");
        return this.fileSystem;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IConstants getConstants() {
        this.assertExists(this.constants, "constants");
        return this.constants;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IBluetoothHandler getBluetoothHandler() {
        this.assertExists(this.bluetoothHandler, "bluetoothHandler");
        return this.bluetoothHandler;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public INFCHandler getNFCHandler() {
        this.assertExists(this.nfcHandler, "nfcHandler");
        return this.nfcHandler;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IQRHandler getQRHandler() {
        this.assertExists(this.qrHandler, "qrHandler");
        return this.qrHandler;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void registerToEventBus(final Object object) {
        EventBus.getDefault().register(object);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void unregisterFromEventBus(final Object object) {
        EventBus.getDefault().unregister(object);
    }

    /**
     * Checks if the module is initialized and throws an error otherwise.
     *
     * @param object The object to check
     * @param name   The name of the object
     */
    private void assertExists(final Object object, final String name) {
        if (object == null) {
            // TODO: log this when the logging branch is merged
            throw new ModuleNotFoundException("The module \"" + name + "\" is used before it is defined");
        }
    }

    /**
     * Thrown when a module was called before it was initialized.
     */
    private class ModuleNotFoundException extends RuntimeException {

        /**
         * Constructs a new exception to make clear that a module was requested before it was initialized.
         *
         * @param message A message describing in more detail what happened
         */
        ModuleNotFoundException(final String message) {
            super(message);
        }

    }

}
