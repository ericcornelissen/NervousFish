package com.nervousfish.nervousfish.modules.pairing;

import android.bluetooth.BluetoothDevice;

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

import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;

/**
 * This Bluetooth service class establishes and manages a bluetooth connection.
 */

@SuppressWarnings({"PMD.NullAssignment", "PMD.AvoidFinalLocalVariable"})
// 4) Suggested use by Android Bluetooth Manual
// 5) explained where used
public final class AndroidBluetoothHandler extends APairingHandler implements IBluetoothHandler {
    private static final long serialVersionUID = 7340362791131903553L;
    private static final Logger LOGGER = LoggerFactory.getLogger("GsonDatabaseAdapter");
    private transient AndroidAcceptThread acceptThread;
    private transient AndroidConnectThread connectThread;
    private transient AndroidConnectedThread connectedThread;
    private State mState;

    /**
     * Constructor for the Bluetooth service which manages the connection.
     *
     * @param serviceLocator The object responsible for creating the service locator
     */
    private AndroidBluetoothHandler(final IServiceLocator serviceLocator) {
        super(serviceLocator);
        mState = State.STATE_NONE;
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
    public void start() {
        LOGGER.info("Bleutooth Service started");

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

            this.mState = State.STATE_LISTEN;
            getServiceLocator().postOnEventBus(new BluetoothListeningEvent());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void connect(final BluetoothDevice device) {
        LOGGER.info("Connect Bluetooth thread initialized");

        synchronized (this) {
            // Cancel any thread attempting to make a connection
            if (mState == State.STATE_CONNECTING && connectThread != null) {
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

            mState = State.STATE_CONNECTING;
            getServiceLocator().postOnEventBus(new BluetoothConnectingEvent());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void stop() {
        LOGGER.info("Bluetooth service stopped");

        synchronized (this) {
            if (connectThread != null) {
                connectThread.cancel();
                connectThread = null;
            }

            if (connectedThread != null) {
                connectedThread.cancel();
                connectedThread = null;
            }

            if (acceptThread != null) {
                acceptThread.cancel();
                acceptThread = null;
            }

            mState = State.STATE_NONE;
            getServiceLocator().postOnEventBus(new BluetoothConnectionLostEvent());
        }
    }

    /**
     * Write to the AndroidConnectedThread in an unsynchronized manner
     *
     * @param output The bytes to write
     * @see AndroidConnectedThread#write(byte[])
     */
    @Override
    public void write(final byte[] output) {
        // Create temporary object
        final AndroidConnectedThread ready;
        // Synchronize a copy of the AndroidConnectedThread
        synchronized (this) {
            if (mState != State.STATE_CONNECTED) {
                return;
            }
            ready = connectedThread;
        }
        // Perform the write unsynchronized
        ready.write(output);
    }

    @Subscribe
    public void onBluetoothConnectedEvent(final BluetoothConnectedEvent event) {
        LOGGER.info("Connected Bluetooth thread started");

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

            this.mState = State.STATE_CONNECTED;
        }
    }

    /**
     * Indicate that the connection attempt failed and notify the UI Activity.
     */
    @Subscribe
    public void onBluetoothConnectionFailedEvent(final BluetoothConnectionFailedEvent event) {
        LOGGER.warn("Bluetooth connection failed event");

        synchronized (this) {
            mState = State.STATE_NONE;

            // Start the service over to restart listening mode
            this.start();
        }
    }

    /**
     * Indicate that the connection was lost and notify the UI Activity.
     */
    @Subscribe
    public void onBluetoothConnectionLostEvent(final BluetoothConnectionLostEvent event) {
        LOGGER.warn("Bluetooth connection lost event");

        synchronized (this) {
            mState = State.STATE_NONE;

            // Start the service over to restart listening mode
            this.start();
        }
    }

    /**
     * Serialize the created proxy instead of this instance.
     */
    private Object writeReplace() {
        return new AndroidBluetoothHandler.SerializationProxy(this);
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
     * Used for the Serialization Proxy Pattern.
     * We suppress here the AccessorClassGeneration warning because the only alternative to this pattern -
     * ordinary serialization - is far more dangerous
     */
    @SuppressWarnings("PMD.AccessorClassGeneration")
    private static final class SerializationProxy implements Serializable {
        private static final long serialVersionUID = 7340362791131903553L;
        private final IServiceLocator serviceLocator;

        SerializationProxy(final AndroidBluetoothHandler handler) {
            this.serviceLocator = handler.getServiceLocator();
        }

        private Object readResolve() {
            return new AndroidBluetoothHandler(this.serviceLocator);
        }
    }
}