package com.nervousfish.nervousfish.modules.pairing;

import android.bluetooth.BluetoothSocket;

import com.nervousfish.nervousfish.exceptions.SocketCreationException;
import com.nervousfish.nervousfish.modules.pairing.events.BluetoothConnectionLostEvent;
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
public final class AndroidBluetoothConnectedThread implements IBluetoothThread {
    private static final Logger LOGGER = LoggerFactory.getLogger("AndroidBluetoothConnectedThread");
    private static final int BUFFER_SIZE = 4096;

    private final BluetoothSocket socket;
    private final OutputStream outStream;
    private final Thread thread;

    /**
     * Constructs the thread than runs during the connection with the remote device
     *
     * @param serviceLocator The service locator used
     * @param socket         The socket over which is communicated
     */
    AndroidBluetoothConnectedThread(final IServiceLocator serviceLocator, final BluetoothSocket socket) {
        LOGGER.info("Connected Bluetooth thread created");
        this.socket = socket;

        // Get the BluetoothSocket input and output streams
        final InputStream inputStream;
        try (InputStream tmpIn = socket.getInputStream();
             OutputStream tmpOut = socket.getOutputStream()) {
            inputStream = tmpIn;
            this.outStream = tmpOut;
        } catch (final IOException e) {
            LOGGER.error("Failed to create a temp socket", e);
            throw new SocketCreationException(e);
        }

        final IBluetoothHandler bluetoothHandler = serviceLocator.getBluetoothHandler();
        final IDataReceiver dataReceiver = (IDataReceiver) bluetoothHandler.getDataReceiver().get();
        this.thread = new Thread(new AndroidBluetoothConnectedThread.ConnectedThread(serviceLocator, dataReceiver, inputStream));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void start() {
        this.thread.start();
    }

    /**
     * Write to the connected OutStream.
     *
     * @param buffer The bytes to write
     */
    public void write(final byte[] buffer) {
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Writing the bytes {} to the outputstream", Arrays.toString(buffer));
        }
        try {
            this.outStream.write(buffer);
        } catch (final IOException e) {
            LOGGER.error("Exception during writing", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void cancel(final boolean closeSocket) {
        LOGGER.warn("Cancelled!");
        if (closeSocket) {
            try {
                this.socket.close();
                LOGGER.info("Socket closed");
            } catch (final IOException e) {
                LOGGER.error("Closing socket", e);
            }
        }
    }

    private static final class ConnectedThread implements Runnable {
        private final IServiceLocator serviceLocator;
        private final IDataReceiver dataReceiver;
        private final InputStream inputStream;

        ConnectedThread(final IServiceLocator serviceLocator, final IDataReceiver dataReceiver, final InputStream inputStream) {
            super();
            this.serviceLocator = serviceLocator;
            this.dataReceiver = dataReceiver;
            this.inputStream = inputStream;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void run() {
            LOGGER.info("Connected Bluetooth thread begin");
            try {
                // Read from the InputStream
                final byte[] buffer = new byte[BUFFER_SIZE];
                //noinspection InfiniteLoopStatement because the cancel method stops the while loop by closing the socket
                while (true) {
                    final int bytes = this.inputStream.read(buffer);
                    LOGGER.info("Read {} bytes", bytes);

                    this.dataReceiver.dataReceived(buffer);
                }
            } catch (final IOException e) {
                LOGGER.warn("Disconnected from the paired device", e);
                this.serviceLocator.postOnEventBus(new BluetoothConnectionLostEvent());
            }
        }
    }
}