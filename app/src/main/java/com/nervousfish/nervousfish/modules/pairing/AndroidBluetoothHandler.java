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
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.UUID;

import static com.nervousfish.nervousfish.modules.pairing.AndroidBluetoothHandler.State.STATE_CONNECTED;
import static com.nervousfish.nervousfish.modules.pairing.AndroidBluetoothHandler.State.STATE_CONNECTING;
import static com.nervousfish.nervousfish.modules.pairing.AndroidBluetoothHandler.State.STATE_LISTEN;
import static com.nervousfish.nervousfish.modules.pairing.AndroidBluetoothHandler.State.STATE_NONE;

/**
 * This Bluetooth service class establishes and manages a bluetooth connection.
 */

@SuppressWarnings({"PMD.NullAssignment", "PMD.AvoidFinalLocalVariable"})
// 4) Suggested use by Android Bluetooth Manual
// 5) explained where used
public final class AndroidBluetoothHandler extends APairingHandler implements IBluetoothHandler {
    private static final long serialVersionUID = 7340362791131903553L;

    enum State { STATE_NONE, STATE_LISTEN, STATE_CONNECTING, STATE_CONNECTED }

    // Unique UUID for this application
    private static final UUID MY_UUID_SECURE =
            UUID.fromString("2d7c6682-3b84-4d00-9e61-717bac0b2643");
    // Name for the SDP record when creating server socket
    private static final String NAME_SECURE = "BluetoothChatSecure";
    private static final Logger LOGGER = LoggerFactory.getLogger("GsonDatabaseAdapter");
    private transient AcceptThread secureAcceptThread;
    private transient ConnectThread connectThread;
    private transient ConnectedThread connectedThread;
    private State mState;

    /**
     * Constructor for the Bluetooth service which manages the connection.
     *
     * @param serviceLocator The object responsible for creating the service locator
     */
    private AndroidBluetoothHandler(final IServiceLocator serviceLocator) {
        super(serviceLocator);
        synchronized (this) {
            mState = STATE_NONE;
            getServiceLocator().postOnEventBus(new BluetoothDisconnectedEvent());
        }
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
            if (secureAcceptThread == null) {
                secureAcceptThread = new AcceptThread();
                secureAcceptThread.start();
            }
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
        }
    }

    /**
     * Write to the ConnectedThread in an unsynchronized manner
     *
     * @param output The bytes to write
     * @see ConnectedThread#write(byte[])
     */
    @Override
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
        ready.write(output);
    }

    /**
     * Indicate that the connection attempt failed and notify the UI Activity.
     */
    private void connectionFailed() {
        synchronized (this) {
            mState = STATE_NONE;
            getServiceLocator().postOnEventBus(new BluetoothDisconnectedEvent());

            // Start the service over to restart listening mode
            this.start();
        }
    }

    /**
     * Indicate that the connection was lost and notify the UI Activity.
     */
    private void connectionLost() {
        synchronized (this) {
            mState = STATE_NONE;
            getServiceLocator().postOnEventBus(new BluetoothDisconnectedEvent());

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

            synchronized (AndroidBluetoothHandler.this) {
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
        }

        public void run() {
            LOGGER.info("Start listening on a rfcomm channel");
            setName("AcceptThread" + "Secure");

            BluetoothSocket socket;

            synchronized (AndroidBluetoothHandler.this) {
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
                        switch (mState) {
                            case STATE_LISTEN:
                            case STATE_CONNECTING:
                                // Situation normal. Start the connected thread.
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
        private final BluetoothSocket socket;
        private final BluetoothDevice device;

        ConnectThread(final BluetoothDevice device) {
            super();

            this.device = device;
            BluetoothSocket tmp = null;


            synchronized (AndroidBluetoothHandler.this) {
                // Get a BluetoothSocket for a connection with the
                // given BluetoothDevice
                try {
                    tmp = device.createRfcommSocketToServiceRecord(
                            MY_UUID_SECURE);
                } catch (final IOException e) {
                    LOGGER.error("Connection failed");
                }
                socket = tmp;
                mState = STATE_CONNECTING;
                getServiceLocator().postOnEventBus(new BluetoothConnectingEvent());
            }
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
                socket.connect();
            } catch (final IOException e) {
                // Close the socket
                try {
                    socket.close();
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
            connected(socket, device);
        }

        void cancel() {
            try {
                socket.close();
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
        private final BluetoothSocket socket;
        private final InputStream inputStream;
        private final OutputStream outputStream;

        ConnectedThread(final BluetoothSocket socket) {
            super();

            LOGGER.info("Connected Bluetooth thread created");
            this.socket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            synchronized (AndroidBluetoothHandler.this) {
                // Get the BluetoothSocket input and output streams
                try {
                    tmpIn = socket.getInputStream();
                    tmpOut = socket.getOutputStream();
                } catch (final IOException e) {
                    LOGGER.error("Failed to create a temp socket");
                }

                inputStream = tmpIn;
                outputStream = tmpOut;
                mState = STATE_CONNECTED;
                getServiceLocator().postOnEventBus(new BluetoothConnectedEvent());
            }
        }

        public void run() {
            LOGGER.info("Connected Bluetooth thread begin");
            final byte[] buffer = new byte[1024];

            // Keep listening to the InputStream while connected
            synchronized (AndroidBluetoothHandler.this) {
                while (mState == STATE_CONNECTED) {
                    try {
                        // Read from the InputStream
                        final int size = inputStream.read(buffer);
                        if (size < 0) {
                            LOGGER.warn("Bluetooth stream damaged");
                        }

                        saveContact(buffer);

                    } catch (final IOException e) {
                        LOGGER.warn("Disconnected from the paired device");
                        connectionLost();
                        break;
                    }
                }
            }
        }

        /**
         * Write to the connected OutStream.
         *
         * @param buffer The bytes to write
         */
        public void write(final byte[] buffer) {
            try {
                outputStream.write(buffer);
            } catch (final IOException e) {
                LOGGER.error("Exception during writing");
            }
        }


        void cancel() {
            try {
                socket.close();
            } catch (final IOException e) {
                LOGGER.error("Closing socket");
            }
        }
    }
}