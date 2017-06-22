package com.nervousfish.nervousfish.modules.pairing;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;

import com.nervousfish.nervousfish.exceptions.NoBluetoothException;
import com.nervousfish.nervousfish.modules.pairing.events.BluetoothAlmostConnectedEvent;
import com.nervousfish.nervousfish.service_locator.IServiceLocator;

import org.apache.commons.lang3.Validate;
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
final class AndroidBluetoothAcceptThread extends Thread implements IBluetoothThread {
    private static final Logger LOGGER = LoggerFactory.getLogger("AndroidBluetoothAcceptThread");
    // The local server socket
    private final BluetoothServerSocket serverSocket;
    private final IServiceLocator serviceLocator;

    /**
     * Constructs a new thread that runs while listening for incoming connections. It behaves
     * like a server-side client. It runs until a connection is accepted
     * @param serviceLocator The service locator used
     */
    AndroidBluetoothAcceptThread(final IServiceLocator serviceLocator) throws IOException {
        super();
        Validate.notNull(serviceLocator);
        this.serviceLocator = serviceLocator;

        final BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            throw new NoBluetoothException("No bluetooth available");
        }

        this.serverSocket = bluetoothAdapter.listenUsingRfcommWithServiceRecord(NAME_SECURE, MY_UUID_SECURE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void run() {
        LOGGER.info("Start listening on a rfcomm channel");
        this.setName("AndroidBluetoothAcceptThread thread");

        final BluetoothSocket socket;

        try {
            // This is a blocking call and will only return on a
            // successful connection or an exception
            socket = this.serverSocket.accept();
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
    @Override
    public void cancel() {
        LOGGER.warn("Cancelled!");
        try {
            this.serverSocket.close();
        } catch (final IOException e) {
            LOGGER.warn("Server failed or closed", e);
        }
    }
}