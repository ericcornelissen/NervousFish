package com.nervousfish.nervousfish.modules.pairing;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;

import com.nervousfish.nervousfish.events.BluetoothAlmostConnectedEvent;
import com.nervousfish.nervousfish.events.BluetoothConnectedEvent;
import com.nervousfish.nervousfish.events.BluetoothConnectingEvent;
import com.nervousfish.nervousfish.events.BluetoothConnectionFailedEvent;
import com.nervousfish.nervousfish.events.BluetoothConnectionLostEvent;
import com.nervousfish.nervousfish.events.BluetoothListeningEvent;
import com.nervousfish.nervousfish.service_locator.IServiceLocator;
import com.nervousfish.nervousfish.service_locator.ModuleWrapper;

import org.greenrobot.eventbus.Subscribe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;

import static org.junit.Assert.assertNotNull;

/**
 * This Bluetooth service class establishes and manages a bluetooth connection.
 */

@SuppressWarnings({"checkstyle:classdataabstractioncoupling", "PMD.NullAssignment", "PMD.TooManyMethods"})
// 1) A logical consequence of using an EventBus. No problem, because it are just (empty) POJO's.
// 2) Suggested use by Android Bluetooth Manual
// 3) explained where used
public final class AndroidBluetoothHandler extends APairingHandler implements IBluetoothHandler {
    private static final long serialVersionUID = 7340362791131903553L;
    private static final Logger LOGGER = LoggerFactory.getLogger("GsonDatabaseAdapter");
    private transient static AndroidAcceptThread acceptThread;
    private transient static AndroidConnectThread connectThread;
    private transient static AndroidConnectedThread connectedThread;
    private transient static BluetoothState mBluetoothState;

    /**
     * Constructor for the Bluetooth service which manages the connection.
     *
     * @param serviceLocator The object responsible for creating the service locator
     */
    private AndroidBluetoothHandler(final IServiceLocator serviceLocator) {
        super(serviceLocator);
        mBluetoothState = BluetoothState.STATE_NONE;
        serviceLocator.registerToEventBus(this);
    }

    /**
     * Creates a new instance of itself and wraps it in a {@link ModuleWrapper} so that only an {@link IServiceLocator}
     * can access the new module to create the new {@link IServiceLocator}.
     *
     * @param serviceLocator The service locator bridge that creates the new service locator
     * @return A wrapper around a newly created instance of this class
     */
    public static ModuleWrapper<AndroidBluetoothHandler> newInstance(final IServiceLocator serviceLocator) {
        return new ModuleWrapper<>(new AndroidBluetoothHandler(serviceLocator));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void start() {
        LOGGER.info("Bluetooth Service started");

        synchronized (this) {
            // Cancel any thread attempting to make a connection
            if (connectThread != null) {
                connectThread.cancel();
                connectThread = null;
            }

            // Cancel any thread currently running a connection
            if (connectedThread != null) {
                connectedThread.cancel();
                connectedThread = null;
            }

            // Start the thread to listen on a BluetoothServerSocket
            if (acceptThread == null) {
                acceptThread = new AndroidAcceptThread(getServiceLocator());
                acceptThread.start();
            }

            this.mBluetoothState = BluetoothState.STATE_LISTEN;
            getServiceLocator().postOnEventBus(new BluetoothListeningEvent());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void connect(final BluetoothDevice device) {
        LOGGER.info("Connect Bluetooth thread initialized");

        synchronized (this) {
            // Cancel any thread attempting to make a connection
            if (mBluetoothState == BluetoothState.STATE_CONNECTING && connectThread != null) {
                connectThread.cancel();
                connectThread = null;
            }

            // Cancel any thread currently running a connection
            if (connectedThread != null) {
                connectedThread.cancel();
                connectedThread = null;
            }

            // Start the thread to connect with the given device
            connectThread = new AndroidConnectThread(this, device);
            connectThread.start();

            mBluetoothState = BluetoothState.STATE_CONNECTING;
            getServiceLocator().postOnEventBus(new BluetoothConnectingEvent());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void stop() {
        /*LOGGER.info("Bluetooth service stopped");
            mBluetoothState = BluetoothState.STATE_NONE;
            getServiceLocator().postOnEventBus(new BluetoothConnectionLostEvent());
        }*/
    }

    /**
     * Write to the AndroidConnectedThread in an unsynchronized manner
     *
     * @param output The bytes to write
     * @see AndroidConnectedThread#write(byte[])
     */
    @Override
    public synchronized void write(final byte[] output) {
        // Create temporary object
        final AndroidConnectedThread ready;
        // Synchronize a copy of the AndroidConnectedThread
        synchronized (this) {
            if (mBluetoothState != BluetoothState.STATE_CONNECTED) {
                return;
            }
            assertNotNull(connectedThread);
            ready = connectedThread;
            LOGGER.info("Writing bytes : " + Arrays.toString(output));
            ready.write(output);
        }
    }

    /**
     * Called by the eventbus when the device connected with another Bluetooth device
     *
     * @param event Data about the event
     */
    @Subscribe
    public synchronized void onBluetoothAlmostConnectedEvent(final BluetoothAlmostConnectedEvent event) {
        LOGGER.info("Almost Connected Bluetooth thread started");

        synchronized (this) {
            // Cancel the thread that completed the connection
            if (connectThread != null) {
                connectThread.cancel();
                connectThread = null;
            }

            // Cancel any thread currently running a connection
            if (connectedThread != null) {
                connectedThread.cancel();
                connectedThread = null;
            }

            // Cancel the accept thread because we only want to connect to one device
            if (acceptThread != null) {
                acceptThread.cancel();
                acceptThread = null;
            }

            // Start the thread to manage the connection and perform transmissions
            connectedThread = new AndroidConnectedThread(this, event.getSocket());
            connectedThread.start();

            mBluetoothState = BluetoothState.STATE_CONNECTED;
            getServiceLocator().postOnEventBus(new BluetoothConnectedEvent(connectedThread));
        }
    }

    /**
     * Called by the eventbus when Bluetooth connection with another device failed.
     *
     * @param event Data about the event
     */
    @Subscribe
    public synchronized void onBluetoothConnectionFailedEvent(final BluetoothConnectionFailedEvent event) {
        LOGGER.warn("Bluetooth connection failed event");

        synchronized (this) {
            mBluetoothState = BluetoothState.STATE_NONE;

            // Start the service over to restart listening mode
            this.start();
        }
    }

    /**
     * Called by the eventbus when the connection with another Bluetooth device is lost.
     *
     * @param event Datab about the event
     */
    @Subscribe
    public synchronized void onBluetoothConnectionLostEvent(final BluetoothConnectionLostEvent event) {
        LOGGER.warn("Bluetooth connection lost event");

        synchronized (this) {
            mBluetoothState = BluetoothState.STATE_NONE;

            // Start the service over to restart listening mode
            this.start();
        }
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

    /**
     * Ensure that the instance meets its class invariant
     *
     * @throws InvalidObjectException Thrown when the state of the class is unstbale
     */
    private void ensureClassInvariant() throws InvalidObjectException {
        // No checks to perform
    }
}