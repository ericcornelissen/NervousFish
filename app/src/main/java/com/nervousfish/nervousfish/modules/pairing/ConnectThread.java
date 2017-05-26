package com.nervousfish.nervousfish.modules.pairing;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import com.nervousfish.nervousfish.events.BluetoothConnectingEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static com.nervousfish.nervousfish.modules.pairing.AndroidBluetoothHandler.MY_UUID_SECURE;
import static com.nervousfish.nervousfish.modules.pairing.AndroidBluetoothHandler.STATE_CONNECTING;

/**
 * This thread runs while attempting to make an outgoing connection
 * with a device. It runs straight through; the connection either
 * succeeds or fails.
 */
class ConnectThread extends Thread {
    private static final Logger LOGGER = LoggerFactory.getLogger("ConnectThread");
    private final BluetoothSocket mmSocket;
    private final BluetoothDevice mmDevice;
    private final AndroidBluetoothHandler handler;

    ConnectThread(final AndroidBluetoothHandler handler, final BluetoothDevice device) {
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
        handler.mState = STATE_CONNECTING;
        handler.getServiceLocator().postOnEventBus(new BluetoothConnectingEvent());
        this.handler = handler;
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
            handler.connectionFailed();
            return;
        }

        // Reset the ConnectThread because we're done
        synchronized (handler) {
            handler.connectThread = null;
        }

        // Start the connected thread
        handler.connected(mmSocket, mmDevice);
    }

    void cancel() {
        try {
            mmSocket.close();
        } catch (final IOException e) {
            LOGGER.error("Closing socket failed");
        }
    }
}