package com.nervousfish.nervousfish.modules.pairing;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;

import com.nervousfish.nervousfish.exceptions.NoBluetoothException;
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
final class AndroidBluetoothAcceptThread {
    private static final Logger LOGGER = LoggerFactory.getLogger("AndroidBluetoothAcceptThread");
    // The local server socket
    private final BluetoothServerSocket serverSocket;
    private final Thread thread;

    /**
     * Constructs a new thread that runs while listening for incoming connections. It behaves
     * like a server-side client. It runs until a connection is accepted
     *
     * @param serviceLocator The service locator used
     */
    AndroidBluetoothAcceptThread(final IServiceLocator serviceLocator) throws IOException {
        final BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            throw new NoBluetoothException("No Bluetooth available");
        }

        this.serverSocket = bluetoothAdapter.listenUsingRfcommWithServiceRecord(NAME_SECURE, MY_UUID_SECURE);
        this.thread = new Thread(new AndroidBluetoothAcceptThread.AcceptThread(serviceLocator, this.serverSocket));
    }

    /**
     * Starts the thread.
     */
    void start() {
        this.thread.start();
    }

    /**
     * Cancels the current accepting of the pairing request of other Bluetooth devices
     */
    void cancel() {
        LOGGER.warn("Cancelled!");
        try {
            this.serverSocket.close();
        } catch (final IOException e) {
            LOGGER.warn("Server failed or closed", e);
        }
    }

    private static final class AcceptThread implements Runnable {
        private final IServiceLocator serviceLocator;
        private final BluetoothServerSocket serverSocket;

        AcceptThread(final IServiceLocator serviceLocator, final BluetoothServerSocket socket) {
            this.serviceLocator = serviceLocator;
            this.serverSocket = socket;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void run() {
            LOGGER.info("Start listening on a rfcomm channel");

            try (BluetoothSocket socket = this.serverSocket.accept()) {
                LOGGER.info("Start connecting with {}", socket.getRemoteDevice().getName());
                this.serviceLocator.postOnEventBus(new BluetoothAlmostConnectedEvent(socket));
            } catch (final IOException e) {
                LOGGER.error("Listening on the socket failed", e);
                return;
            }

            LOGGER.info("End accept thread");
        }
    }
}