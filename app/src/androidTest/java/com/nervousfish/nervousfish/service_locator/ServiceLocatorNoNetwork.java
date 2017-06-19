package com.nervousfish.nervousfish.service_locator;

import com.nervousfish.nervousfish.annotations.DesignedForExtension;
import com.nervousfish.nervousfish.modules.constants.IConstants;
import com.nervousfish.nervousfish.modules.cryptography.IEncryptor;
import com.nervousfish.nervousfish.modules.cryptography.IKeyGenerator;
import com.nervousfish.nervousfish.modules.database.IDatabase;
import com.nervousfish.nervousfish.modules.filesystem.IFileSystem;
import com.nervousfish.nervousfish.modules.pairing.DummyBluetoothHandler;
import com.nervousfish.nervousfish.modules.pairing.IBluetoothHandler;
import com.nervousfish.nervousfish.modules.pairing.INfcHandler;
import com.nervousfish.nervousfish.modules.pairing.IQRHandler;
import com.nervousfish.nervousfish.modules.pairing.NFCHandler;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import static org.junit.Assert.assertNotNull;

/**
 * Manages all modules and provides access to them.
 * It disables Bluetooth and NFC because that cannot be used on an emulator
 */
public class ServiceLocatorNoNetwork extends ServiceLocator {

    private static final long serialVersionUID = 4499096670975222223L;

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

    /**
     * {@inheritDoc}
     */
    @Override
    @DesignedForExtension
    IBluetoothHandler initBluetoothHandler() {
        return DummyBluetoothHandler.newInstance(this).getModule();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @DesignedForExtension
    INfcHandler initNfcHandler() {
        return NFCHandler.newInstance(this).getModule();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @DesignedForExtension
    public void registerToEventBus(final Object object) {
        // Do nothing
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @DesignedForExtension
    public void unregisterFromEventBus(final Object object) {
        // Do nothing
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @DesignedForExtension
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
        this.ensureClassInvariant();
    }

    /**
     * Used to improve performance / efficiency
     *
     * @param stream The stream to which this object should be serialized to
     */
    private void writeObject(final ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
    }

    /**
     * Ensure that the instance meets its class invariant
     *
     * @throws InvalidObjectException Thrown when the state of the class is unstable
     */
    private void ensureClassInvariant() {
        assertNotNull(this.getAndroidFilesDir());
    }
}
