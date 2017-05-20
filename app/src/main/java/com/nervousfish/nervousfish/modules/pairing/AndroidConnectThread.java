package com.nervousfish.nervousfish.modules.pairing;


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import com.nervousfish.nervousfish.events.BluetoothConnectedEvent;
import com.nervousfish.nervousfish.events.BluetoothConnectionFailedEvent;
import com.nervousfish.nervousfish.modules.constants.IConstants;
import com.nervousfish.nervousfish.service_locator.IServiceLocator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * This thread runs while attempting to make an outgoing connection
 * with a device. It runs straight through; the connection either
 * succeeds or fails.
 */
class AndroidConnectThread extends Thread {
    private static final Logger LOGGER = LoggerFactory.getLogger("AndroidConnectThread");
    private final IServiceLocator serviceLocator;
    private final BluetoothSocket socket;

    /**
     * Constructs a new thread that runs while attempting to make an outgoing connection
     * with a device. It runs straight through; the connection either
     * succeeds or fails.
     *
     * @param bluetoothHandler The bluetooth handler that created this class
     * @param device The device to connect with
     */
    AndroidConnectThread(final AndroidBluetoothHandler bluetoothHandler, final BluetoothDevice device) {
        super();

        this.serviceLocator = bluetoothHandler.getServiceLocator();
        final IConstants constants = serviceLocator.getConstants();
        BluetoothSocket tmp = null;

        synchronized (this) {
            // Get a BluetoothSocket for a connection with the
            // given BluetoothDevice
            try {
                tmp = device.createRfcommSocketToServiceRecord(constants.getUuid());
            } catch (final IOException e) {
                LOGGER.error("Connection failed");
            }
            socket = tmp;
        }
    }

    /**
     * Should not be called by the user; runs the thread
     */
    public void run() {
        LOGGER.info("Connect Bluetooth thread started");
        setName("Android Connect Thread");

        // Always cancel discovery because it will slow down a connection
        BluetoothAdapter.getDefaultAdapter().cancelDiscovery();

        // Make a connection to the BluetoothSocket
        try {
            // This is a blocking call and will only return on a
            // successful connection or an exception
            socket.connect();
        } catch (final IOException e) {
            try {
                socket.close();
            } catch (final IOException eclose) {
                LOGGER.error("Connection failed/couldn't close the socket");
            }
            this.serviceLocator.postOnEventBus(new BluetoothConnectionFailedEvent());
            return;
        }

        this.serviceLocator.postOnEventBus(new BluetoothConnectedEvent(socket));
    }

    /**
     * Cancels the thread
     */
    void cancel() {
        try {
            socket.close();
        } catch (final IOException e) {
            LOGGER.error("Closing socket failed");
        }
    }
}