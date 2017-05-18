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
import com.nervousfish.nervousfish.modules.pairing.DummyBluetoothHandler;
import com.nervousfish.nervousfish.modules.pairing.DummyNFCHandler;
import com.nervousfish.nervousfish.modules.pairing.DummyQRHandler;
import com.nervousfish.nervousfish.modules.pairing.IBluetoothHandler;
import com.nervousfish.nervousfish.modules.pairing.INFCHandler;
import com.nervousfish.nervousfish.modules.pairing.IQRHandler;

import org.greenrobot.eventbus.EventBus;

import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;

/**
 * Manages all modules and provides access to them.
 */
final class ServiceLocator implements IServiceLocator {
    private static final long serialVersionUID = 1408616442873653749L;

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
        this.bluetoothHandler = DummyBluetoothHandler.newInstance(this).get();
        this.nfcHandler = DummyNFCHandler.newInstance(this).get();
        this.qrHandler = DummyQRHandler.newInstance(this).get();
    }

    /**
     * Private constructor for deserializing the service locator
     */
    @SuppressWarnings("checkstyle:parameternumber")
    ServiceLocator(final String androidFilesDir,
                   final IDatabase database,
                   final IKeyGenerator keyGenerator,
                   final IEncryptor encryptor,
                   final IFileSystem fileSystem,
                   final IConstants constants,
                   final IBluetoothHandler bluetoothHandler,
                   final INFCHandler nfcHandler,
                   final IQRHandler qrHandler) {
        this.androidFilesDir = androidFilesDir;
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
    private static class ModuleNotFoundException extends RuntimeException {

        /**
         * Constructs a new exception to make clear that a module was requested before it was initialized.
         *
         * @param message A message describing in more detail what happened
         */
        ModuleNotFoundException(final String message) {
            super(message);
        }

    }


    /**
     * Serialize the created proxy instead of this instance.
     */
    private Object writeReplace() {
        return new SerializationProxy(this);
    }

    /**
     * Ensure that no instance of this class is created because it was present in the stream. A correct
     * stream should only contain instances of the proxy.
     */
    private void readObject(final ObjectInputStream stream) throws InvalidObjectException {
        throw new InvalidObjectException("Proxy required.");
    }

    /**
     * Represents the logical state of this class and copies the data from that class without
     * any consistency checking or defensive copying.
     */
    private static final class SerializationProxy implements Serializable {
        private static final long serialVersionUID = 1408616442873653749L;
        private final String androidFilesDir;
        private final IDatabase database;
        private final IKeyGenerator keyGenerator;
        private final IEncryptor encryptor;
        private final IFileSystem fileSystem;
        private final IConstants constants;
        private final IBluetoothHandler bluetoothHandler;
        private final INFCHandler nfcHandler;
        private final IQRHandler qrHandler;

        SerializationProxy(final ServiceLocator serviceLocator) {
            this.androidFilesDir = serviceLocator.androidFilesDir;
            this.database = serviceLocator.database;
            this.keyGenerator = serviceLocator.keyGenerator;
            this.encryptor = serviceLocator.encryptor;
            this.fileSystem = serviceLocator.fileSystem;
            this.constants = serviceLocator.constants;
            this.bluetoothHandler = serviceLocator.bluetoothHandler;
            this.nfcHandler = serviceLocator.nfcHandler;
            this.qrHandler = serviceLocator.qrHandler;
        }

        private Object readResolve() {
            return new ServiceLocator(this.androidFilesDir,
                    this.database,
                    this.keyGenerator,
                    this.encryptor,
                    this.fileSystem,
                    this.constants,
                    this.bluetoothHandler,
                    this.nfcHandler,
                    this.qrHandler);
        }
    }
}
