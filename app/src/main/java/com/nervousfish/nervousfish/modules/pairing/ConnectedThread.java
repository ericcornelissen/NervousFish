package com.nervousfish.nervousfish.modules.pairing;

import android.bluetooth.BluetoothSocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

import static com.nervousfish.nervousfish.modules.pairing.AndroidBluetoothService.STATE_CONNECTED;

/**
 * This thread runs during a connection with a remote device.
 * It handles all incoming and outgoing transmissions.
 */
class ConnectedThread extends Thread {
    private static final Logger LOGGER = LoggerFactory.getLogger("ConnectedThread");
    private final BluetoothSocket mmSocket;
    private final InputStream mmInStream;
    private final OutputStream mmOutStream;
    private final AndroidBluetoothService handler;

    ConnectedThread(final AndroidBluetoothService handler, final BluetoothSocket socket) {
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
        handler.mState = STATE_CONNECTED;
        this.handler = handler;
    }

    public void run() {
        LOGGER.info("Connected Bluetooth thread begin");
        final byte[] buffer = new byte[4096];

        // Keep listening to the InputStream while connected
        while (handler.mState == STATE_CONNECTED) {
            try {
                // Read from the InputStream
                final int bytes = mmInStream.read(buffer);
                LOGGER.info(" Read " + bytes + " bytes");

                handler.saveContact(buffer);
                //getServiceLocator().postOnEventBus(new ContactReceivedEvent(contact));
            } catch (final IOException e) {
                LOGGER.warn("Disconnected from the paired device");
                handler.connectionLost();
                break;
            }
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