package com.nervousfish.nervousfish.service_locator;

import com.nervousfish.nervousfish.ConstantKeywords;
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
import com.nervousfish.nervousfish.modules.pairing.AndroidBluetoothHandler;
import com.nervousfish.nervousfish.modules.pairing.DummyNFCHandler;
import com.nervousfish.nervousfish.modules.pairing.DummyQRHandler;
import com.nervousfish.nervousfish.modules.pairing.IBluetoothHandler;
import com.nervousfish.nervousfish.modules.pairing.INfcHandler;
import com.nervousfish.nervousfish.modules.pairing.IQRHandler;

import org.greenrobot.eventbus.EventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;

/**
 * Manages all modules and provides access to them.
 */
public final class ServiceLocator implements IServiceLocator {
    private static final Logger LOGGER = LoggerFactory.getLogger("ServiceLocator");
    private static final long serialVersionUID = 1408616442873653749L;

    private final String androidFilesDir;
    private final IDatabase database;
    private final IKeyGenerator keyGenerator;
    private final IEncryptor encryptor;
    private final IFileSystem fileSystem;
    private final IConstants constants;
    private final IBluetoothHandler bluetoothHandler;
    private final INfcHandler nfcHandler;
    private final IQRHandler qrHandler;

    /**
     * Package-private constructor of the service locator
     *
     * @param androidFilesDir The directory of the Android-specific files
     */
    ServiceLocator(final String androidFilesDir) {
        this.androidFilesDir = androidFilesDir;
        this.constants = Constants.newInstance(this).getModule();
        this.fileSystem = AndroidFileSystemAdapter.newInstance(this).getModule();
        this.database = GsonDatabaseAdapter.newInstance(this).getModule();
        this.keyGenerator = KeyGeneratorAdapter.newInstance(this).getModule();
        this.encryptor = EncryptorAdapter.newInstance(this).getModule();
        this.bluetoothHandler = AndroidBluetoothHandler.newInstance(this).getModule();
        this.nfcHandler = DummyNFCHandler.newInstance(this).getModule();
        this.qrHandler = DummyQRHandler.newInstance(this).getModule();
    }

    /**
     * Private constructor for deserializing the service locator
     *
     * @param androidFilesDir The directory of the Android-specific files
     */
    // We suppress parameternumber and javadocmethod, because this method isn't meant to be used outside
    // this class and it's needed for the serialization proxy
    @SuppressWarnings({"checkstyle:parameternumber", "checkstyle:javadocmethod"})
    ServiceLocator(final String androidFilesDir,
                           final IDatabase database,
                           final IKeyGenerator keyGenerator,
                           final IEncryptor encryptor,
                           final IFileSystem fileSystem,
                           final IConstants constants,
                           final IBluetoothHandler bluetoothHandler,
                           final INfcHandler nfcHandler,
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
        this.assertExists(this.androidFilesDir, "androidFilesDir");
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
    public INfcHandler getNFCHandler() {
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
     * {@inheritDoc}
     */
    @Override
    public void postOnEventBus(final Object object) {
        EventBus.getDefault().post(object);
    }

    /**
     * Checks if the module is initialized and throws an error otherwise.
     *
     * @param object The object to check
     * @param name   The name of the object
     */
    private void assertExists(final Object object, final String name) {
        if (object == null) {
            LOGGER.error("The module \"%s\" is used before it is defined", name);
            throw new ModuleNotFoundException("The module \"" + name + "\" is used before it is defined");
        }
    }

    /**
     * Thrown when a module was called before it was initialized.
     */
    static class ModuleNotFoundException extends RuntimeException {

        private static final long serialVersionUID = -2889621076876351934L;

        /**
         * Constructs a new exception to make clear that a module was requested before it was initialized.
         *
         * @param message A message describing in more detail what happened
         */
        ModuleNotFoundException(final String message) {
            super(message);
        }

        /**
         * Serialize the created proxy instead of the {@link ModuleNotFoundException} instance.
         */
        private Object writeReplace() {
            return new SerializationProxy(this);
        }

        /**
         * Ensure no instance of {@link ModuleNotFoundException} is created when present in the stream.
         */
        private void readObject(final ObjectInputStream stream) throws InvalidObjectException {
            throw new InvalidObjectException(ConstantKeywords.PROXY_REQUIRED);
        }

        /**
         * A proxy representing the logical state of {@link ModuleNotFoundException}. Copies the data
         * from that class without any consistency checking or defensive copying.
         */
        private static final class SerializationProxy implements Serializable {

            private static final long serialVersionUID = -1930759144828515311L;

            private final String message;
            private final Throwable throwable;

            /**
             * Constructs a new SerializationProxy
             *
             * @param exception The current instance of the proxy
             */
            SerializationProxy(final ModuleNotFoundException exception) {
                this.message = exception.getMessage();
                this.throwable = exception.getCause();
            }

            /**
             * Not to be called by the user - resolves a new object of this proxy
             *
             * @return The object resolved by this proxy
             */
            private Object readResolve() {
                return new ModuleNotFoundException(this.message).initCause(this.throwable);
            }
        }

    }

}
