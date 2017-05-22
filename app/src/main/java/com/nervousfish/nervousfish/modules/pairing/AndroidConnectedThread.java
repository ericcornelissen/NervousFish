package com.nervousfish.nervousfish.modules.pairing;


import android.bluetooth.BluetoothSocket;

import com.nervousfish.nervousfish.events.BluetoothConnectionLostEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * This thread runs during a connection with a remote Bluetooth device.
 * It handles all incoming and outgoing transmissions.
 */
public class AndroidConnectedThread extends Thread {
    private static final Logger LOGGER = LoggerFactory.getLogger("AndroidConnectedThread");
    private static final int BUFFER_SIZE_IN_BYTES = 1024;
    private final BluetoothSocket socket;
    private final InputStream inputStream;
    private final OutputStream outputStream;
    private final AndroidBluetoothHandler bluetoothHandler;

    /**
     * Constructs a new thread that runs during a connection with a remote device.
     * It handles all incoming and outgoing transmissions.
     *
     * @param bluetoothHandler The {@link AndroidBluetoothHandler} that created this thread
     * @param socket           The socket over which can be communicated to the target
     */
    AndroidConnectedThread(final AndroidBluetoothHandler bluetoothHandler, final BluetoothSocket socket) {
        super();

        this.bluetoothHandler = bluetoothHandler;
        this.socket = socket;
        InputStream tmpIn = null;
        OutputStream tmpOut = null;

        synchronized (this) {
            // Get the BluetoothSocket input and output streams
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (final IOException e) {
                LOGGER.error("Failed to create a temp socket");
            }
            inputStream = tmpIn;
            outputStream = tmpOut;
        }
        LOGGER.info("Connected Bluetooth thread created");
    }

    /**
     * Should not be called by the user; runs the thread
     */
    public void run() {
        LOGGER.info("Connected Bluetooth thread begin");
        setName("Android Connected Thread");
        final byte[] buffer = new byte[BUFFER_SIZE_IN_BYTES];

        // Keep listening to the InputStream while connected
        synchronized (this) {
            try {
                // Read from the InputStream
                final int size = inputStream.read(buffer);
                if (size < 0) {
                    LOGGER.warn("Bluetooth stream damaged");
                }

                bluetoothHandler.saveContact(buffer);

            } catch (final IOException e) {
                LOGGER.warn("Disconnected from the paired device");
                bluetoothHandler.getServiceLocator().postOnEventBus(new BluetoothConnectionLostEvent());
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

    /**
     * Cancels the thread
     */
    void cancel() {
        try {
            socket.close();
        } catch (final IOException e) {
            LOGGER.error("Closing socket");
        }
    }
}