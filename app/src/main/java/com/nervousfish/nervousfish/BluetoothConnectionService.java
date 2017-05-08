package com.nervousfish.nervousfish;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.util.Log;

import com.nervousfish.nervousfish.events.SLReadyEvent;
import com.nervousfish.nervousfish.modules.pairing.IBluetoothHandler;
import com.nervousfish.nervousfish.service_locator.IServiceLocator;
import com.nervousfish.nervousfish.service_locator.IServiceLocatorCreator;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

/**
 * Created by smironov on 2/05/2017.
 * This Bluetooth service class establishes and manages a bluetooth connection.
 */

@SuppressWarnings({"PMD.UnusedLocalVariable", "PMD.UnusedPrivateField", "PMD.SingularField",
        "PMD.NullAssignment", "PMD.AvoidFinalLocalVariable", "PMD.AvoidDuplicateLiterals"})
// 1 + 2 + 3)because it's used for storing, later it will also be accessed
// 4) Suggested use by Android Bluetooth Manual
// 5) explained where used

public class BluetoothConnectionService implements IBluetoothHandler {

    // Constants that indicate the current connection state
    public static final int STATE_NONE = 0;       // we're doing nothing
    public static final int STATE_LISTEN = 1;     // now listening for incoming connections
    public static final int STATE_CONNECTING = 2; // now initiating an outgoing connection
    public static final int STATE_CONNECTED = 3;  // now connected to a remote device
    // Unique UUID for this application (this one is a filler)
    private static final UUID MY_UUID_SECURE =
            UUID.fromString("fa87c0d0-afac-11de-8a39-0800200c9a66");
    // Name for the SDP record when creating server socket
    private static final String NAME_SECURE = "BluetoothChatSecure";
    private static final String TAG = "MY_APP_DEBUG_TAG";
    @SuppressWarnings("PMD.SingularField")
    private final IServiceLocatorCreator serviceLocatorCreator;
    private final BluetoothAdapter bluetoothAdapter;
    private final Handler mHandler; // handler that gets info from Bluetooth service;
    private AcceptThread secureAcceptThread;
    private ConnectThread connectThread;
    private ConnectedThread connectedThread;
    private int mState;
    private final int mNewState; // temp final for pmd


    /**
     * Constructor for the Bluetooth service which manages the connection.
     *
     * @param handler               A Handler to send messages back to the UI Activity
     * @param serviceLocatorCreator The object responsible for creating the service locator
     */
    public BluetoothConnectionService(final Handler handler, final IServiceLocatorCreator serviceLocatorCreator) {
        mHandler = handler;
        mState = STATE_NONE;
        mNewState = mState;
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        this.serviceLocatorCreator = serviceLocatorCreator;
        this.serviceLocatorCreator.registerToEventBus(this);
    }

    /**
     * Start the chat service. Specifically start AcceptThread to begin a
     * session in listening (server) mode. Called by the Activity onResume()
     */
    public void start() {
        Log.d(TAG, "start");

        synchronized(this) {
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
            // TODO: Update UI title
        }
    }


    /**
     * Start the ConnectThread to initiate a connection to a remote device.
     *
     * @param device The BluetoothDevice to connect
     */
    public void connect(final BluetoothDevice device) {
        Log.d(TAG, "connect to: " + device);

        synchronized(this) {
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
            // TODO: Update UI title
        }
    }

    /**
     * Start the ConnectedThread to begin managing a Bluetooth connection
     *
     * @param socket The BluetoothSocket on which the connection was made
     * @param device The BluetoothDevice that has been connected
     */
    public  void connected(final BluetoothSocket socket, final BluetoothDevice
            device) {
        Log.d(TAG, "connected, Socket Type:" + "Secure");

        synchronized(this) {
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

            // TODO: Send the name of the connected device back to the UI Activity using the handler

            // TODO: Update UI title
        }
    }

    /**
     * Stop all threads
     */
    public void stop() {
        Log.d(TAG, "stop");

        synchronized(this) {
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
            // TODO: Update UI title
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
        ready.write(output);
    }

    /**
     * Indicate that the connection attempt failed and notify the UI Activity.
     */
    private void connectionFailed() {
        // TODO: Send a failure message back to the Activity using the handler

        mState = STATE_NONE;
        // TODO: Update UI title

        // Start the service over to restart listening mode
        this.start();
    }

    /**
     * Indicate that the connection was lost and notify the UI Activity.
     */
    private void connectionLost() {
        // TODO: Send a failure message back to the Activity using the handler

        mState = STATE_NONE;
        // TODO: Update UI title

        // Start the service over to restart listening mode
        this.start();
    }

    @Override
    public void onSLReadyEvent(final SLReadyEvent event) {
        final IServiceLocator serviceLocator = serviceLocatorCreator.getServiceLocator();
        // TODO: finish this method
    }

    /**
     * Return the current connection state.
     */
    public int getState() {
        synchronized (this){return mState;}
    }

    /**
     * This thread runs while listening for incoming connections. It behaves
     * like a server-side client. It runs until a connection is accepted
     * (or until cancelled).
     */
    private class AcceptThread extends Thread {
        // The local server socket
        private final BluetoothServerSocket serverSocket;

        public AcceptThread() {
            super();
            BluetoothServerSocket tmp = null;


            // Create a new listening server socket
            try {
                tmp = bluetoothAdapter.listenUsingRfcommWithServiceRecord(NAME_SECURE,
                        MY_UUID_SECURE);

            } catch (final IOException e) {
                Log.e(TAG, "Socket Type: " + "Secure" + "listen() failed", e);
            }
            serverSocket = tmp;
            mState = STATE_LISTEN;
        }

        public void run() {
            Log.d(TAG, "Socket Type: " + "Secure"
                    + "BEGIN mAcceptThread" + this);
            setName("AcceptThread" + "Secure");

            BluetoothSocket socket = null;

            // Listen to the server socket if we're not connected
            while (mState != STATE_CONNECTED) {
                try {
                    // This is a blocking call and will only return on a
                    // successful connection or an exception
                    socket = serverSocket.accept();
                } catch (final IOException e) {
                    Log.e(TAG, "Socket Type: " + "Secure" + "accept() failed", e);
                    break;
                }

                // If a connection was accepted
                if (socket != null) {
                    synchronized (BluetoothConnectionService.this) {
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
                                    Log.e(TAG, "Could not close unwanted socket", e);
                                }
                                break;
                            default: //nothing
                                break;
                        }
                    }
                }
            }
            Log.i(TAG, "END mAcceptThread, socket Type: " + "Secure");

        }

        public void cancel() {
            Log.d(TAG, "Socket Type" + "Secure" + "cancel " + this);
            try {
                serverSocket.close();
            } catch (final IOException e) {
                Log.e(TAG, "Socket Type" + "Secure" + "close() of server failed", e);
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

        public ConnectThread(final BluetoothDevice device) {
            super();

            mmDevice = device;
            BluetoothSocket tmp = null;

            // Get a BluetoothSocket for a connection with the
            // given BluetoothDevice
            try {
                tmp = device.createRfcommSocketToServiceRecord(
                        MY_UUID_SECURE);
            } catch (final IOException e) {
                Log.e(TAG, "Socket Type: " + "Secure" + "create() failed", e);
            }
            mmSocket = tmp;
            mState = STATE_CONNECTING;
        }

        public void run() {
            Log.i(TAG, "BEGIN connectThread SocketType:" + "Secure");
            setName("ConnectThread" + "Secure");

            // Always cancel discovery because it will slow down a connection
            bluetoothAdapter.cancelDiscovery();

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
                    Log.e(TAG, "unable to close() " + "Secure"
                                    + " socket during connection failure",
                            e2);
                }
                connectionFailed();
                return;
            }

            // Reset the ConnectThread because we're done
            synchronized (BluetoothConnectionService.this) {
                connectThread = null;
            }

            // Start the connected thread
            connected(mmSocket, mmDevice);
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (final IOException e) {
                Log.e(TAG, "close() of connect " + "Secure" + " socket failed", e);
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

            Log.d(TAG, "create ConnectedThread: " + "Secure");
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the BluetoothSocket input and output streams
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (final IOException e) {
                Log.e(TAG, "temp sockets not created", e);
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
            mState = STATE_CONNECTED;
        }

        public void run() {
            Log.i(TAG, "BEGIN connectedThread");
            final byte[] buffer = new byte[1024];
            int bytes;

            // Keep listening to the InputStream while connected
            while (mState == STATE_CONNECTED) {
                try {
                    // Read from the InputStream
                    bytes = mmInStream.read(buffer);

                    // TODO: Send the obtained bytes to the UI Activity

                } catch (final IOException e) {
                    Log.e(TAG, "disconnected", e);
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
            try {
                mmOutStream.write(buffer);

                // TODO: Share the sent message back to the UI Activity

            } catch (final IOException e) {
                Log.e(TAG, "Exception during write", e);
            }
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (final IOException e) {
                Log.e(TAG, "close() of connect socket failed", e);
            }
        }
    }
}