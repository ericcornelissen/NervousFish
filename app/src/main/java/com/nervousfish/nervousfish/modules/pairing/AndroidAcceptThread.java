package com.nervousfish.nervousfish.modules.pairing;


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;

import com.nervousfish.nervousfish.events.BluetoothConnectedEvent;
import com.nervousfish.nervousfish.modules.constants.IConstants;
import com.nervousfish.nervousfish.service_locator.IServiceLocator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * This thread runs while listening for incoming connections over Bluetooth. It behaves
 * like a server-side Bluetooth client. It runs until a connection is accepted
 * (or until cancelled).
 */
class AndroidAcceptThread extends Thread {
    private static final Logger LOGGER = LoggerFactory.getLogger("AndroidAcceptThread");
    // The local server socket
    private final BluetoothServerSocket serverSocket;
    private final IServiceLocator serviceLocator;

    /**
     * Constructs a new AndroidAcceptThread that runs while listening for incoming connections. It behaves
     * like a server-side client. It runs until a connection is accepted
     * (or until cancelled).
     *
     * @param serviceLocator The service locator to be used
     */
    AndroidAcceptThread(final IServiceLocator serviceLocator) {
        super();
        this.serviceLocator = serviceLocator;
        final IConstants constants = serviceLocator.getConstants();
        BluetoothServerSocket tmp = null;

        synchronized (this) {
            // Create a new listening server socket
            try {
                tmp = BluetoothAdapter.getDefaultAdapter().listenUsingRfcommWithServiceRecord(constants.getSDPRecord(),
                        constants.getUuid());

            } catch (final IOException e) {
                LOGGER.error("Couldn't setup an rfcomm channel");

            }
            serverSocket = tmp;
        }
    }

    /**
     * Should not be called by the user; runs the thread
     */
    public void run() {
        LOGGER.info("Start listening on a rfcomm channel");
        setName("Android Accept Thread");

        synchronized (this) {
            final BluetoothSocket socket;
            try {
                // This is a blocking call and will only return on a
                // successful connection or an exception
                socket = serverSocket.accept();
            } catch (final IOException e) {
                LOGGER.error("Listening on the socket failed");
                return;
            }

            serviceLocator.postOnEventBus(new BluetoothConnectedEvent(socket));
        }
        LOGGER.info("End accept thread");
    }

    /**
     * Cancels the thread
     */
    void cancel() {
        LOGGER.info("Cancel accept thread");
        try {
            serverSocket.close();
        } catch (final IOException e) {
            LOGGER.warn("Server failed/closed");
        }
    }
}