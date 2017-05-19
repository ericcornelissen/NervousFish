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
    private static final int STATE_NONE = 0;       // we're doing nothing
    private static final int STATE_LISTEN = 1;     // now listening for incoming connections
    private static final int STATE_CONNECTING = 2; // now initiating an outgoing connection
    private static final int STATE_CONNECTED = 3;  // now connected to a remote device
    // Unique UUID for this application
    private static final UUID MY_UUID_SECURE =
            UUID.fromString("2d7c6682-3b84-4d00-9e61-717bac0b2643");
    // Name for the SDP record when creating server socket
    private static final String NAME_SECURE = "BluetoothChatSecure";
    private static final Logger LOGGER = LoggerFactory.getLogger("AndroidBluetoothHandler");
    private AcceptThread secureAcceptThread;
    private ConnectThread connectThread;
    private ConnectedThread connectedThread;
    private int mState;

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
                secureAcceptThread = new AcceptThread();
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
            connectThread = new ConnectThread(device);
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
            connectedThread = new ConnectedThread(socket);
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
    private void connectionFailed() {

        mState = STATE_NONE;
        getServiceLocator().postOnEventBus(new BluetoothDisconnectedEvent());
        updateUserInterfaceTitle();

        // Start the service over to restart listening mode
        this.start();
    }

    /**
     * Indicate that the connection was lost and notify the UI Activity.
     */
    private void connectionLost() {

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

    /**
     * This thread runs while listening for incoming connections. It behaves
     * like a server-side client. It runs until a connection is accepted
     * (or until cancelled).
     */
    private class AcceptThread extends Thread {
        // The local server socket
        private final BluetoothServerSocket serverSocket;

        AcceptThread() {
            super();
            BluetoothServerSocket tmp = null;


            // Create a new listening server socket
            try {
                tmp = BluetoothAdapter.getDefaultAdapter().listenUsingRfcommWithServiceRecord(NAME_SECURE,
                        MY_UUID_SECURE);

            } catch (final IOException e) {
                LOGGER.error("Couldn't setup an rfcomm channel");

            }
            serverSocket = tmp;
            mState = STATE_LISTEN;
            getServiceLocator().postOnEventBus(new BluetoothListeningEvent());
        }

        public void run() {
            LOGGER.info("Start listening on a rfcomm channel");
            setName("AcceptThread" + "Secure");

            BluetoothSocket socket = null;

            // Listen to the server socket if we're not connected
            while (mState != STATE_CONNECTED) {
                try {
                    // This is a blocking call and will only return on a
                    // successful connection or an exception
                    socket = serverSocket.accept();
                } catch (final IOException e) {
                    LOGGER.error("Listening on the socket failed");
                    break;
                }

                // If a connection was accepted
                if (socket != null) {
                    synchronized (AndroidBluetoothHandler.this) {
                        switch (mState) {
                            case STATE_LISTEN:
                            case STATE_CONNECTING:
                                // Situation normal. Start the connected thread.
                                LOGGER.info("Start connecting with" + socket.getRemoteDevice().getName());
                                connected(socket, socket.getRemoteDevice());
                                break;
                            case STATE_NONE:
                            case STATE_CONNECTED:
                                // Either not ready or already connected. Terminate new socket.
                                try {
                                    socket.close();
                                } catch (final IOException e) {
                                    LOGGER.info("Couldn't close unwanted socket");
                                }
                                break;
                            default: //nothing
                                break;
                        }
                    }
                }
            }
            LOGGER.info("End accept thread");
        }

        void cancel() {
            LOGGER.info("Cancel accept thread");
            try {
                serverSocket.close();
            } catch (final IOException e) {
                LOGGER.warn("Server failed/closed");
            }
        }
    }

    /**
     * This thread runs while attempting to make an outgoing connection
     * with a device. It runs straight through; the connection either
     * succeeds or fails.
     */
    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        ConnectThread(final BluetoothDevice device) {
            super();

            mmDevice = device;
            BluetoothSocket tmp = null;

            // Get a BluetoothSocket for a connection with the
            // given BluetoothDevice
            try {
                tmp = device.createRfcommSocketToServiceRecord(
                        MY_UUID_SECURE);
            } catch (final IOException e) {
                LOGGER.error("Connection failed");
            }
            mmSocket = tmp;
            mState = STATE_CONNECTING;
            getServiceLocator().postOnEventBus(new BluetoothConnectingEvent());
        }

        public void run() {
            LOGGER.info("Connect Bluetooth thread started");
            setName("ConnectThread" + "Secure");

            // Always cancel discovery because it will slow down a connection
            BluetoothAdapter.getDefaultAdapter().cancelDiscovery();

            // Make a connection to the BluetoothSocket
            try {
                // This is a blocking call and will only return on a
                // successful connection or an exception
                mmSocket.connect();
            } catch (final IOException e) {
                // Close the socket
                try {
                    mmSocket.close();
                } catch (final IOException e2) {
                    LOGGER.error("Connection failed/couldn't close the socket");
                }
                connectionFailed();
                return;
            }

            // Reset the ConnectThread because we're done
            synchronized (AndroidBluetoothHandler.this) {
                connectThread = null;
            }

            // Start the connected thread
            connected(mmSocket, mmDevice);
        }

        void cancel() {
            try {
                mmSocket.close();
            } catch (final IOException e) {
                LOGGER.error("Closing socket failed");
            }
        }
    }

    /**
     * This thread runs during a connection with a remote device.
     * It handles all incoming and outgoing transmissions.
     */
    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        ConnectedThread(final BluetoothSocket socket) {
            super();

            LOGGER.info("Connected Bluetooth thread created");
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the BluetoothSocket input and output streams
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (final IOException e) {
                LOGGER.error("Failed to create a temp socket");
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
            mState = STATE_CONNECTED;
        }

        public void run() {
            LOGGER.info("Connected Bluetooth thread begin");
            final byte[] buffer = new byte[1024];

            // Keep listening to the InputStream while connected
            while (mState == STATE_CONNECTED) {
                try {
                    // Read from the InputStream
                    final int bytes = mmInStream.read(buffer);
                    LOGGER.info(" Read " + bytes + " bytes");

                    saveContact(buffer);
                    //getServiceLocator().postOnEventBus(new ContactReceivedEvent(contact));
                } catch (final IOException e) {
                    LOGGER.warn("Disconnected from the paired device");
                    connectionLost();
                    break;
                }
            }
        }

        /**
         * Write to the connected OutStream.
         *
         * @param buffer The bytes to write
         */
        public void write(final byte[] buffer) {
            LOGGER.info("Writing the bytes " + Arrays.toString(buffer) + "to the outputstream");
            try {
                mmOutStream.write(buffer);
            } catch (final IOException e) {
                LOGGER.error("Exception during writing");
            }
        }


        void cancel() {
            try {
                mmSocket.close();
            } catch (final IOException e) {
                LOGGER.error("Closing socket");
            }
        }
    }
}