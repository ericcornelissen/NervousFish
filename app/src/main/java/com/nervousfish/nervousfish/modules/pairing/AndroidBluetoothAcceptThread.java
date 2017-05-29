package com.nervousfish.nervousfish.modules.pairing;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;

import com.nervousfish.nervousfish.modules.pairing.events.BluetoothAlmostConnectedEvent;
import com.nervousfish.nervousfish.service_locator.IServiceLocator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static com.nervousfish.nervousfish.modules.pairing.AndroidBluetoothService.MY_UUID_SECURE;
import static com.nervousfish.nervousfish.modules.pairing.AndroidBluetoothService.NAME_SECURE;

/**
 * This thread runs while listening for incoming connections. It behaves
 * like a server-side client. It runs until a connection is accepted
 * (or until cancelled).
 */
class AndroidBluetoothAcceptThread extends Thread {
    private static final Logger LOGGER = LoggerFactory.getLogger("AndroidBluetoothAcceptThread");
    // The local server socket
    private final BluetoothServerSocket serverSocket;
    private final IServiceLocator serviceLocator;

    /**
     * Constructs a new thread that runs while listening for incoming connections. It behaves
     * like a server-side client. It runs until a connection is accepted
     * @param serviceLocator The service locator used
     */
    AndroidBluetoothAcceptThread(final IServiceLocator serviceLocator) {
        super();
        BluetoothServerSocket tmp = null;
        this.serviceLocator = serviceLocator;

        // Create a new listening server socket
        try {
            tmp = BluetoothAdapter.getDefaultAdapter().listenUsingRfcommWithServiceRecord(NAME_SECURE,
                    MY_UUID_SECURE);

        } catch (final IOException e) {
            LOGGER.error("Couldn't setup an rfcomm channel", e);

        }
        serverSocket = tmp;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void run() {
        LOGGER.info("Start listening on a rfcomm channel");
        setName("AndroidBluetoothAcceptThread thread");

        final BluetoothSocket socket;

        try {
            // This is a blocking call and will only return on a
            // successful connection or an exception
            socket = serverSocket.accept();
        } catch (final IOException e) {
            LOGGER.error("Listening on the socket failed", e);
            return;
        }

        LOGGER.info("Start connecting with {}", socket.getRemoteDevice().getName());
        this.serviceLocator.postOnEventBus(new BluetoothAlmostConnectedEvent(socket));
        LOGGER.info("End accept thread");
    }

    /**
     * Cancels the current accepting of the pairing request of other Bluetooth devices
     */
    void cancel() {
        LOGGER.warn("Cancelled!");
        LOGGER.info("Cancel accept thread");
        try {
            serverSocket.close();
        } catch (final IOException e) {
            LOGGER.warn("Server failed/closed", e);
        }
    }
}