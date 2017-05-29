package com.nervousfish.nervousfish.modules.pairing;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.support.annotation.Nullable;

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
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;

/**
 * This Bluetooth service class establishes and manages a bluetooth connection.
 */

@SuppressWarnings({"checkstyle:classdataabstractioncoupling", "PMD.NullAssignment", "PMD.TooManyMethods", "LawOfDemeter"})
// 1) A logical consequence of using an EventBus. No problem, because it are just (empty) POJO's.
// 2) Suggested use by Android Bluetooth Manual
// 3) explained where used
// 4) getServiceLocator() is defined in the superclass to retrieve the service locator. Refractoring the variable into every subclass
//    would add a duplicate variable / initializer in all subclasses
public final class AndroidBluetoothHandler extends APairingHandler implements IBluetoothHandler {
    private static final long serialVersionUID = 7340362791131903553L;
    private static final Logger LOGGER = LoggerFactory.getLogger("AndroidBluetoothHandler");
    private final transient Object lock = new Object();
    @Nullable
    private transient AndroidAcceptThread acceptThread;
    @Nullable
    private transient AndroidConnectThread connectThread;
    @Nullable
    private transient AndroidConnectedThread connectedThread;
    private transient BluetoothState mBluetoothState = BluetoothState.STATE_NONE;

    /**
     * Constructor for the Bluetooth service which manages the connection.
     *
     * @param serviceLocator The object responsible for creating the service locator
     */
    private AndroidBluetoothHandler(final IServiceLocator serviceLocator) {
        super(serviceLocator);
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
        LOGGER.info("Bluetooth Service started");

        final IServiceLocator serviceLocator = this.getServiceLocator();
        serviceLocator.registerToEventBus(this);

        synchronized (this.lock) {
            this.cancelConnectThreads();

            // Start the thread to listen on a BluetoothServerSocket
            this.acceptThread = new AndroidAcceptThread(this, this.getServiceLocator());
            this.acceptThread.start();

            this.mBluetoothState = BluetoothState.STATE_LISTEN;
            serviceLocator.postOnEventBus(new BluetoothListeningEvent());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void stop() {
        this.getServiceLocator().unregisterFromEventBus(this);
        this.cancelConnectThreads();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void connect(final BluetoothDevice device) {
        LOGGER.info("Connect Bluetooth thread initialized");

        synchronized (this.lock) {
            this.cancelConnectThreads();

            // Start the thread to connect with the given device
            this.connectThread = new AndroidConnectThread(this, device);
            this.connectThread.start();

            this.mBluetoothState = BluetoothState.STATE_CONNECTING;
            this.getServiceLocator().postOnEventBus(new BluetoothConnectingEvent());
        }
    }

    /**
     * Write to the AndroidConnectedThread in an asynchronous manner.
     *
     * @param buffer The bytes to send
     * @see AndroidConnectedThread#write(byte[])
     */
    @Override
    public void send(final byte[] buffer) {
        // Synchronize a copy of the AndroidConnectedThread
        synchronized (this.lock) {
            if (this.connectedThread == null) {
                LOGGER.error("Trying to send data before connectedThread is initialized");
            } else if (this.mBluetoothState == BluetoothState.STATE_CONNECTED) {
                LOGGER.info("Writing bytes: {}", Arrays.toString(buffer));
                this.connectedThread.write(buffer);
            }
        }
    }


    /**
     * Notify the bluetooth handler that the device as a server got a client.
     *
     * @param socket The socket over which the communication should happen
     */
    void notifyAlmostConnected(final BluetoothSocket socket) {
        LOGGER.info("Almost Connected Bluetooth thread started");

        synchronized (this.lock) {
            this.cancelConnectThreads();

            // Start the thread to manage the connection and perform transmissions
            this.connectedThread = new AndroidConnectedThread(this, socket);
            this.connectedThread.start();

            this.mBluetoothState = BluetoothState.STATE_CONNECTED;
            this.getServiceLocator().postOnEventBus(new BluetoothConnectedEvent(this.connectedThread));
        }
    }

    /**
     * Called by the eventBus when Bluetooth connection with another device failed.
     *
     * @param event Data about the event
     */
    @Subscribe
    public void onBluetoothConnectionFailedEvent(final BluetoothConnectionFailedEvent event) {
        LOGGER.warn("Bluetooth connection failed event");

        synchronized (this.lock) {
            this.mBluetoothState = BluetoothState.STATE_NONE;

            // Start the service over to restart listening mode
            this.start();
        }
    }

    /**
     * Called by the eventBus when the connection with another Bluetooth device is lost.
     *
     * @param event Data about the event
     */
    @Subscribe
    public void onBluetoothConnectionLostEvent(final BluetoothConnectionLostEvent event) {
        LOGGER.warn("Bluetooth connection lost event");

        synchronized (this.lock) {
            this.mBluetoothState = BluetoothState.STATE_NONE;

            // Start the service over to restart listening mode
            this.start();
        }
    }

    /**
     * Cancel any accept, connect and connected threads.
     */
    private void cancelConnectThreads() {
        // Cancel any accept thread
        if (this.acceptThread != null) {
            this.acceptThread.cancel();
            this.acceptThread = null;
        }

        // Cancel any thread attempting to make a connection
        if (this.connectThread != null) {
            this.connectThread.cancel();
            this.connectThread = null;
        }

        // Cancel any thread currently running a connection
        if (this.connectedThread != null) {
            this.connectedThread.cancel();
            this.connectedThread = null;
        }
    }

    /**
     * Deserialize the instance using readObject to ensure invariants and security.
     *
     * @param stream The serialized object to be dematerialized
     */
    private void readObject(final ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.ensureClassInvariant();
    }

    /**
     * Used to improve performance & efficiency.
     *
     * @param stream The stream to which this object should be serialized to
     */
    private void writeObject(final ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
    }

    /**
     * Ensure that the instance meets its class invariant
     */
    private void ensureClassInvariant() {
        // No checks to perform
    }

}