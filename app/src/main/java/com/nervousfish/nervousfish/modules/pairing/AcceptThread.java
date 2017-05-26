package com.nervousfish.nervousfish.modules.pairing;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;

import com.nervousfish.nervousfish.events.BluetoothListeningEvent;
import com.nervousfish.nervousfish.service_locator.IServiceLocator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static com.nervousfish.nervousfish.modules.pairing.AndroidBluetoothService.MY_UUID_SECURE;
import static com.nervousfish.nervousfish.modules.pairing.AndroidBluetoothService.NAME_SECURE;
import static com.nervousfish.nervousfish.modules.pairing.AndroidBluetoothService.STATE_CONNECTED;
import static com.nervousfish.nervousfish.modules.pairing.AndroidBluetoothService.STATE_CONNECTING;
import static com.nervousfish.nervousfish.modules.pairing.AndroidBluetoothService.STATE_LISTEN;
import static com.nervousfish.nervousfish.modules.pairing.AndroidBluetoothService.STATE_NONE;

/**
 * This thread runs while listening for incoming connections. It behaves
 * like a server-side client. It runs until a connection is accepted
 * (or until cancelled).
 */
class AcceptThread extends Thread {
    private static final Logger LOGGER = LoggerFactory.getLogger("AcceptThread");
    // The local server socket
    private final BluetoothServerSocket serverSocket;
    private final AndroidBluetoothService handler;

    AcceptThread(AndroidBluetoothService handler, IServiceLocator serviceLocator) {
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
        handler.mState = STATE_LISTEN;
        this.handler = handler;
    }

    public void run() {
        LOGGER.info("Start listening on a rfcomm channel");
        setName("AcceptThread" + "Secure");

        BluetoothSocket socket = null;

        // Listen to the server socket if we're not connected
        while (handler.mState != STATE_CONNECTED) {
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
                synchronized (handler) {
                    switch (handler.mState) {
                        case STATE_LISTEN:
                        case STATE_CONNECTING:
                            // Situation normal. Start the connected thread.
                            LOGGER.info("Start connecting with" + socket.getRemoteDevice().getName());
                            handler.connected(socket, socket.getRemoteDevice());
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