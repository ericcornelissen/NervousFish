package com.nervousfish.nervousfish.modules.pairing;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import com.nervousfish.nervousfish.events.BluetoothAlmostConnectedEvent;
import com.nervousfish.nervousfish.events.BluetoothConnectingEvent;
import com.nervousfish.nervousfish.events.BluetoothConnectionFailedEvent;
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
class AndroidBluetoothConnectThread extends Thread {
    private static final Logger LOGGER = LoggerFactory.getLogger("AndroidBluetoothConnectThread");
    private final BluetoothSocket socket;
    private final BluetoothDevice mmDevice;
    private final IServiceLocator serviceLocator;

    AndroidBluetoothConnectThread(final IServiceLocator serviceLocator, final BluetoothDevice device) {
        super();

        this.serviceLocator = serviceLocator;
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
        socket = tmp;
        serviceLocator.postOnEventBus(new BluetoothConnectingEvent());
    }

    public void run() {
        LOGGER.info("Connect Bluetooth thread started");
        setName("AndroidBluetoothConnectThread" + "Secure");

        // Always cancel discovery because it will slow down a connection
        BluetoothAdapter.getDefaultAdapter().cancelDiscovery();

        // Make a connection to the BluetoothSocket
        try {
            // This is a blocking call and will only return on a
            // successful connection or an exception
            socket.connect();
        } catch (final IOException e) {
            LOGGER.warn("Exception while connecting over Bluetooth", e);
            try {
                socket.close();
            } catch (final IOException e2) {
                LOGGER.error("Connection failed/couldn't close the socket", e2);
            }
            serviceLocator.postOnEventBus(new BluetoothConnectionFailedEvent());
            return;
        }

        serviceLocator.postOnEventBus(new BluetoothAlmostConnectedEvent(socket));
    }

    void cancel() {
        try {
            socket.close();
        } catch (final IOException e) {
            LOGGER.error("Closing socket failed", e);
        }
    }
}