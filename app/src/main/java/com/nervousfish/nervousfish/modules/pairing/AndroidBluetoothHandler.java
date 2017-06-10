package com.nervousfish.nervousfish.modules.pairing;

import android.bluetooth.BluetoothDevice;

import com.nervousfish.nervousfish.service_locator.IServiceLocator;
import com.nervousfish.nervousfish.service_locator.ModuleWrapper;
import com.nervousfish.nervousfish.service_locator.NervousFish;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;

/**
 * Helper class for {@link AndroidBluetoothService} and acts as the bridge between the client and
 * the service. It simplifies the whole Bluetooth functionality to the methods start, connect, stop and send
 */
// A logical consequence of using an EventBus. No problem, because it are just (empty) POJO's.
@SuppressWarnings("checkstyle:classdataabstractioncoupling")
public final class AndroidBluetoothHandler extends APairingHandler implements IBluetoothHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger("AndroidBluetoothHandler");
    private static final long serialVersionUID = -6465987636766819498L;

    /**
     * Prevents construction from outside the class.
     *
     * @param serviceLocator Can be used to get access to other modules
     */
    private AndroidBluetoothHandler(final IServiceLocator serviceLocator) {
        super(serviceLocator);
    }

    /**
     * Creates a new instance of itself and wraps it in a {@link ModuleWrapper} so that only an
     * {@link IServiceLocator}
     *
     * @param serviceLocator The new service locator
     * @return A wrapper around a newly created instance of this class
     */
    public static ModuleWrapper<AndroidBluetoothHandler> newInstance(final IServiceLocator serviceLocator) {
        return new ModuleWrapper<>(new AndroidBluetoothHandler(serviceLocator));
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
    private void ensureClassInvariant() throws InvalidObjectException {
        // No checks to perform
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void start() throws IOException {
        this.getService().start();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void connect(final BluetoothDevice device) {
        this.getService().connect(device);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void stop() {
        this.getService().stop();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void send(final byte[] bytes) {
        this.getService().write(bytes);

        LOGGER.info("Bytes written: " + Arrays.toString(bytes));
    }

    private IBluetoothHandlerService getService() {
        return ((NervousFish) NervousFish.getInstance()).getBluetoothService().get();
    }

}