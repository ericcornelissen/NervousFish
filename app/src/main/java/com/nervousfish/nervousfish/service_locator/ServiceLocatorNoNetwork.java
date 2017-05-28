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
import com.nervousfish.nervousfish.modules.pairing.DummyBluetoothHandler;
import com.nervousfish.nervousfish.modules.pairing.DummyNFCHandler;
import com.nervousfish.nervousfish.modules.pairing.DummyQRHandler;
import com.nervousfish.nervousfish.modules.pairing.IBluetoothHandler;
import com.nervousfish.nervousfish.modules.pairing.INfcHandler;
import com.nervousfish.nervousfish.modules.pairing.IQRHandler;

import org.greenrobot.eventbus.EventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Manages all modules and provides access to them.
 * It disables Bluetooth and NFC because that cannot be used on an emulator
 */
public class ServiceLocatorNoNetwork extends ServiceLocator implements IServiceLocator {
    private static final Logger LOGGER = LoggerFactory.getLogger("ServiceLocatorNoNetwork");

    /**
     * Package-private constructor of the service locator
     *
     * @param androidFilesDir The directory of the Android-specific files
     */
    ServiceLocatorNoNetwork(final String androidFilesDir) {
        super(androidFilesDir);
    }

    /**
     * Private constructor for deserializing the service locator
     *
     * @param androidFilesDir The directory of the Android-specific files
     */
    // We suppress parameternumber and javadocmethod, because this method isn't meant to be used outside
    // this class and it's needed for the serialization proxy
    @SuppressWarnings({"checkstyle:parameternumber", "checkstyle:javadocmethod"})
    ServiceLocatorNoNetwork(final String androidFilesDir,
                   final IDatabase database,
                   final IKeyGenerator keyGenerator,
                   final IEncryptor encryptor,
                   final IFileSystem fileSystem,
                   final IConstants constants,
                   final IBluetoothHandler bluetoothHandler,
                   final INfcHandler nfcHandler,
                   final IQRHandler qrHandler) {
        super(androidFilesDir, database, keyGenerator, encryptor, fileSystem, constants, bluetoothHandler, nfcHandler, qrHandler);
    }

    @Override
    IBluetoothHandler initBluetoothHandler() {
        return DummyBluetoothHandler.newInstance(this).getModule();
    }

    @Override
    INfcHandler initNfcHandler() {
        return DummyNFCHandler.newInstance(this).getModule();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void registerToEventBus(final Object object) {
        // Do nothing
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void unregisterFromEventBus(final Object object) {
        // Do nothing
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void postOnEventBus(final Object object) {
        // Do nothing
    }

    /**
     * Deserialize the instance using readObject to ensure invariants and security.
     *
     * @param stream The serialized object to be deserialized
     */
    private void readObject(final ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        ensureClassInvariant();
    }

    /**
     * Used to improve performance / efficiency
     *
     * @param stream The stream to which this object should be serialized to
     */
    private void writeObject(final ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
    }
}
