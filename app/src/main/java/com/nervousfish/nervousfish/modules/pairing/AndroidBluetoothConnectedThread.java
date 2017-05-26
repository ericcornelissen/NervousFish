package com.nervousfish.nervousfish.modules.pairing;

import android.bluetooth.BluetoothSocket;

import com.nervousfish.nervousfish.events.BluetoothConnectionLostEvent;
import com.nervousfish.nervousfish.events.SerializedBufferReceivedEvent;
import com.nervousfish.nervousfish.service_locator.IServiceLocator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

/**
 * This thread runs during a connection with a remote device.
 * It handles all incoming and outgoing transmissions.
 */
public class AndroidBluetoothConnectedThread extends Thread {
    private static final Logger LOGGER = LoggerFactory.getLogger("AndroidBluetoothConnectedThread");
    private final BluetoothSocket mmSocket;
    private final InputStream mmInStream;
    private final OutputStream mmOutStream;
    private final IServiceLocator serviceLocator;

    AndroidBluetoothConnectedThread(final IServiceLocator serviceLocator, final BluetoothSocket socket) {
        super();

        LOGGER.info("Connected Bluetooth thread created");
        mmSocket = socket;
        InputStream tmpIn = null;
        OutputStream tmpOut = null;

        // Get the BluetoothSocket input and output streams
        try {
            tmpIn = socket.getInputStream();
            tmpOut = socket.getOutputStream();
        } catch (final IOException e) {
            LOGGER.error("Failed to create a temp socket");
        }

        mmInStream = tmpIn;
        mmOutStream = tmpOut;
        this.serviceLocator = serviceLocator;
    }

    public void run() {
        LOGGER.info("Connected Bluetooth thread begin");
        final byte[] buffer = new byte[4096];
        try {
            // Read from the InputStream
            final int bytes = mmInStream.read(buffer);
            LOGGER.info(" Read " + bytes + " bytes");

            this.serviceLocator.postOnEventBus(new SerializedBufferReceivedEvent(buffer));
        } catch (final IOException e) {
            LOGGER.warn("Disconnected from the paired device");
            this.serviceLocator.postOnEventBus(new BluetoothConnectionLostEvent());
        }
    }

    /**
     * Write to the connected OutStream.
     *
     * @param buffer The bytes to write
     */
    public void write(final byte[] buffer) {
        LOGGER.info("Writing the bytes " + Arrays.toString(buffer) + "to the outputstream");
        try {
            mmOutStream.write(buffer);
        } catch (final IOException e) {
            LOGGER.error("Exception during writing");
        }
    }


    void cancel() {
        try {
            mmSocket.close();
        } catch (final IOException e) {
            LOGGER.error("Closing socket");
        }
    }
}