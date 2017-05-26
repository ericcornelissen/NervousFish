package com.nervousfish.nervousfish.modules.pairing;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;

import com.nervousfish.nervousfish.events.BluetoothConnectedEvent;
import com.nervousfish.nervousfish.events.BluetoothConnectingEvent;
import com.nervousfish.nervousfish.events.BluetoothDisconnectedEvent;
import com.nervousfish.nervousfish.events.BluetoothListeningEvent;
import com.nervousfish.nervousfish.service_locator.IServiceLocator;
import com.nervousfish.nervousfish.service_locator.ModuleWrapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.UUID;

/**
 * This Bluetooth service class establishes and manages a bluetooth connection.
 */

@SuppressWarnings({"PMD.NullAssignment", "PMD.AvoidFinalLocalVariable"})
// 4) Suggested use by Android Bluetooth Manual
// 5) explained where used

public final class AndroidBluetoothHandler extends APairingHandler implements IBluetoothHandler {

    // Constants that indicate the current connection state
    static final int STATE_NONE = 0;       // we're doing nothing
    static final int STATE_LISTEN = 1;     // now listening for incoming connections
    static final int STATE_CONNECTING = 2; // now initiating an outgoing connection
    static final int STATE_CONNECTED = 3;  // now connected to a remote device
    // Unique UUID for this application
    static final UUID MY_UUID_SECURE =
            UUID.fromString("2d7c6682-3b84-4d00-9e61-717bac0b2643");
    // Name for the SDP record when creating server socket
    static final String NAME_SECURE = "BluetoothChatSecure";
    private static final Logger LOGGER = LoggerFactory.getLogger("AndroidBluetoothHandler");
    private AcceptThread secureAcceptThread;
    ConnectThread connectThread;
    private ConnectedThread connectedThread;
    int mState;

    /**
     * Constructor for the Bluetooth service which manages the connection.
     *
     * @param serviceLocator The object responsible for creating the service locator
     */
    private AndroidBluetoothHandler(final IServiceLocator serviceLocator) {
        super(serviceLocator);
        mState = STATE_NONE;
        getServiceLocator().postOnEventBus(new BluetoothDisconnectedEvent());
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
     * Update UI title according to the current state of the chat connection
     */
    private void updateUserInterfaceTitle() {
        synchronized (this) {
            mState = getState();
        }
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
            if (secureAcceptThread == null) {
                secureAcceptThread = new AcceptThread(this, this.getServiceLocator());
                secureAcceptThread.start();
            }
            updateUserInterfaceTitle();
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
            if (mState == STATE_CONNECTING && connectThread != null) {
                connectThread.cancel();
                connectThread = null;
            }

            // Cancel any thread currently running a connection
            if (connectedThread != null) {
                connectedThread.cancel();
                connectedThread = null;
            }

            // Start the thread to connect with the given device
            connectThread = new ConnectThread(this, device);
            connectThread.start();
            updateUserInterfaceTitle();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void connected(final BluetoothSocket socket, final BluetoothDevice device) {
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
            if (secureAcceptThread != null) {
                secureAcceptThread.cancel();
                secureAcceptThread = null;
            }

            // Start the thread to manage the connection and perform transmissions
            connectedThread = new ConnectedThread(this, socket);
            connectedThread.start();
            getServiceLocator().postOnEventBus(new BluetoothConnectedEvent());

            updateUserInterfaceTitle();
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

            if (secureAcceptThread != null) {
                secureAcceptThread.cancel();
                secureAcceptThread = null;
            }

            mState = STATE_NONE;
            getServiceLocator().postOnEventBus(new BluetoothDisconnectedEvent());
            updateUserInterfaceTitle();
        }
    }

    /**
     * Write to the ConnectedThread in an unsynchronized manner
     *
     * @param output The bytes to write
     * @see ConnectedThread#write(byte[])
     */
    public void write(final byte[] output) {
        // Create temporary object
        final ConnectedThread ready;
        // Synchronize a copy of the ConnectedThread
        synchronized (this) {
            if (mState != STATE_CONNECTED) {
                return;
            }
            ready = connectedThread;
        }
        // Perform the write unsynchronized
        LOGGER.info("Write bytes : " + Arrays.toString(output));
        ready.write(output);
    }

    /**
     * Indicate that the connection attempt failed and notify the UI Activity.
     */
    void connectionFailed() {

        mState = STATE_NONE;
        getServiceLocator().postOnEventBus(new BluetoothDisconnectedEvent());
        updateUserInterfaceTitle();

        // Start the service over to restart listening mode
        this.start();
    }

    /**
     * Indicate that the connection was lost and notify the UI Activity.
     */
    void connectionLost() {

        mState = STATE_NONE;
        getServiceLocator().postOnEventBus(new BluetoothDisconnectedEvent());
        updateUserInterfaceTitle();

        // Start the service over to restart listening mode
        this.start();
    }

    /**
     * Return the current connection state.
     */
    public int getState() {
        synchronized (this) {
            return mState;
        }
    }
}