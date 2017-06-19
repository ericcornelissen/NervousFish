package com.nervousfish.nervousfish.modules.pairing;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import com.nervousfish.nervousfish.modules.pairing.events.BluetoothAlmostConnectedEvent;
import com.nervousfish.nervousfish.modules.pairing.events.BluetoothConnectingEvent;
import com.nervousfish.nervousfish.modules.pairing.events.BluetoothConnectionFailedEvent;
import com.nervousfish.nervousfish.service_locator.IServiceLocator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static com.nervousfish.nervousfish.modules.pairing.AndroidBluetoothService.MY_UUID_SECURE;

/**
 * This thread runs while attempting to make an outgoing connection
 * with a device. It runs straight through; the connection either
 * succeeds or fails.
 */
class AndroidBluetoothConnectThread extends Thread implements IBluetoothThread {
    private static final Logger LOGGER = LoggerFactory.getLogger("AndroidBluetoothConnectThread");
    private final BluetoothSocket socket;
    private final IServiceLocator serviceLocator;

    /**
     * Constructs the thread that initiates a new connection with another user
     * @param serviceLocator The servicelocator that can be used
     * @param device The device to connect to
     */
    AndroidBluetoothConnectThread(final IServiceLocator serviceLocator, final BluetoothDevice device) {
        super();

        this.serviceLocator = serviceLocator;
        BluetoothSocket tmp = null;

        // Get a BluetoothSocket for a connection with the
        // given BluetoothDevice
        try {
            tmp = device.createRfcommSocketToServiceRecord(MY_UUID_SECURE);
        } catch (final IOException e) {
            LOGGER.error("Connection failed", e);
        }
        this.socket = tmp;
        serviceLocator.postOnEventBus(new BluetoothConnectingEvent());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void run() {
        LOGGER.info("Connect Bluetooth thread started");
        this.setName("AndroidBluetoothConnectThread thread");

        // Always cancel discovery because it will slow down a connection
        BluetoothAdapter.getDefaultAdapter().cancelDiscovery();

        // Make a connection to the BluetoothSocket
        try {
            // This is a blocking call and will only return on a
            // successful connection or an exception
            this.socket.connect();
        } catch (final IOException e) {
            LOGGER.warn("Exception while connecting over Bluetooth", e);
            try {
                this.socket.close();
            } catch (final IOException esocketCloseException) {
                LOGGER.error("Connection failed/couldn't close the socket", esocketCloseException);
            }
            this.serviceLocator.postOnEventBus(new BluetoothConnectionFailedEvent());
            return;
        }

        this.serviceLocator.postOnEventBus(new BluetoothAlmostConnectedEvent(this.socket));
    }

    /**
     * Cancels the connect thread and closes the socket
     */
    @Override
    public void cancel() {
        LOGGER.warn("Cancelled!");
        try {
            this.socket.close();
        } catch (final IOException e) {
            LOGGER.error("Closing socket", e);
        }
    }
}
