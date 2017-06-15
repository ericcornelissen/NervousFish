package com.nervousfish.nervousfish.modules.pairing;

import android.bluetooth.BluetoothSocket;

import com.nervousfish.nervousfish.modules.pairing.events.BluetoothConnectionLostEvent;
import com.nervousfish.nervousfish.service_locator.IServiceLocator;

import org.apache.commons.lang3.Validate;
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
public final class AndroidBluetoothConnectedThread extends Thread implements IBluetoothThread {
    private static final Logger LOGGER = LoggerFactory.getLogger("AndroidBluetoothConnectedThread");
    private static final int BUFFER_SIZE = 4096;

    private final BluetoothSocket socket;
    private final InputStream inStream;
    private final OutputStream outStream;
    private final IServiceLocator serviceLocator;
    private final IDataReceiver dataReceiver;

    /**
     * Constructs the thread than runs during the connection with the remote device
     * @param serviceLocator The service locator used
     * @param socket The socket over which is communicated
     */
    AndroidBluetoothConnectedThread(final IServiceLocator serviceLocator, final BluetoothSocket socket) {
        super();

        Validate.notNull(serviceLocator);
        Validate.notNull(socket);

        LOGGER.info("Connected Bluetooth thread created");
        this.socket = socket;
        InputStream tmpIn = null;
        OutputStream tmpOut = null;

        // Get the BluetoothSocket input and output streams
        try {
            tmpIn = socket.getInputStream();
            tmpOut = socket.getOutputStream();
        } catch (final IOException e) {
            LOGGER.error("Failed to create a temp socket", e);
        }

        this.inStream = tmpIn;
        this.outStream = tmpOut;
        this.serviceLocator = serviceLocator;
        this.dataReceiver = (IDataReceiver) serviceLocator.getBluetoothHandler().getDataReceiver().get();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void run() {
        LOGGER.info("Connected Bluetooth thread begin");
        this.setName("AndroidBluetoothConnectedThread thread");

        try {
            // Read from the InputStream
            final byte[] buffer = new byte[BUFFER_SIZE];
            while (true) {
                final int bytes = this.inStream.read(buffer);
                LOGGER.info("Read {} bytes", bytes);

                this.dataReceiver.dataReceived(buffer);
            }
        } catch (final IOException e) {
            LOGGER.warn("Disconnected from the paired device", e);
            this.serviceLocator.postOnEventBus(new BluetoothConnectionLostEvent());
        }
    }

    /**
     * Write to the connected OutStream.
     *
     * @param buffer The bytes to write. At least one byte is required
     */
    public void write(final byte[] buffer) {
        LOGGER.info("Writing the bytes {} to the outputstream", Arrays.toString(buffer));
        Validate.isTrue(buffer.length > 0);
        try {
            this.outStream.write(buffer);
        } catch (final IOException e) {
            LOGGER.error("Exception during writing", e);
        }
    }

    /**
     * Cancels the connected thread and closes the socket
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