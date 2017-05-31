package com.nervousfish.nervousfish.modules.pairing;

import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.nervousfish.nervousfish.modules.pairing.events.BluetoothAlmostConnectedEvent;
import com.nervousfish.nervousfish.modules.pairing.events.BluetoothConnectedEvent;
import com.nervousfish.nervousfish.modules.pairing.events.BluetoothConnectingEvent;
import com.nervousfish.nervousfish.modules.pairing.events.BluetoothConnectionFailedEvent;
import com.nervousfish.nervousfish.modules.pairing.events.BluetoothConnectionLostEvent;
import com.nervousfish.nervousfish.modules.pairing.events.BluetoothListeningEvent;
import com.nervousfish.nervousfish.service_locator.IServiceLocator;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.UUID;

import static com.nervousfish.nervousfish.modules.pairing.BluetoothState.STATE_CONNECTED;
import static com.nervousfish.nervousfish.modules.pairing.BluetoothState.STATE_CONNECTING;
import static com.nervousfish.nervousfish.modules.pairing.BluetoothState.STATE_LISTEN;
import static com.nervousfish.nervousfish.modules.pairing.BluetoothState.STATE_NONE;

/**
 * Runs on the background and accepts incoming Bluetooth pairing requests
 */
// Suppressed because we cannot reduce the threads and events that it needs to run
// NullAssignment suppressed, because assigning null to bluetooth threads is the recommended way
//  (see https://developer.android.com/samples/BluetoothChat/src/com.example.android.bluetoothchat/BluetoothChatService.html)
@SuppressWarnings({"checkstyle:classdataabstractioncoupling", "PMD.NullAssignment"})
public final class AndroidBluetoothService extends Service implements IBluetoothHandlerService {
    // Unique UUID for this application
    static final UUID MY_UUID_SECURE =
            UUID.fromString("2d7c6682-3b84-4d00-9e61-717bac0b2643");
    // Name for the SDP record when creating server socket
    static final String NAME_SECURE = "BluetoothChatSecure";
    private static final Logger LOGGER = LoggerFactory.getLogger("AndroidBluetoothHandler");
    // Binder given to clients
    private final IBinder mBinder = new LocalBinder();
    private AndroidBluetoothConnectThread connectThread;
    private BluetoothState state = STATE_NONE;
    private AndroidBluetoothAcceptThread acceptThread;
    private AndroidBluetoothConnectedThread connectedThread;
    private IServiceLocator serviceLocator;

    /**
     * @param serviceLocator The service locator that the service can use
     */
    public void setServiceLocator(final IServiceLocator serviceLocator) {
        synchronized (this) {
            this.serviceLocator = serviceLocator;
            this.serviceLocator.registerToEventBus(this);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IBinder onBind(final Intent intent) {
        return mBinder;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void start() throws IOException {
        LOGGER.info("Bluetooth service starting...");

        synchronized (this) {
            // Cancel any thread attempting to make a connection
            if (connectThread != null) {
                connectThread.cancel();
                connectThread = null;
            }

            // Cancel any thread currently running a connection
            if (connectedThread != null) {
                connectedThread.cancel();
                connectedThread = null;
            }

            // Start the thread to listen on a BluetoothServerSocket
            if (acceptThread == null) {
                this.state = STATE_LISTEN;
                acceptThread = new AndroidBluetoothAcceptThread(this.serviceLocator);
                acceptThread.start();
                this.serviceLocator.postOnEventBus(new BluetoothListeningEvent());
            }
        }

        LOGGER.info("Bluetooth service started");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void connect(final BluetoothDevice device) {
        LOGGER.info("Connect Bluetooth thread initialized");

        synchronized (this) {
            // Cancel any thread attempting to make a connection
            if (state == STATE_CONNECTING && connectThread != null) {
                connectThread.cancel();
                connectThread = null;
            }

            // Cancel any thread currently running a connection
            if (connectedThread != null) {
                connectedThread.cancel();
                connectedThread = null;
            }

            this.state = STATE_CONNECTING;
            // Start the thread to connect with the given device
            connectThread = new AndroidBluetoothConnectThread(this.serviceLocator, device);
            connectThread.start();
            this.serviceLocator.postOnEventBus(new BluetoothConnectingEvent());
        }
    }

    /**
     * Called when device is almost connected over Bluetooth
     *
     * @param event Contains additional data over the event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBluetoothAlmostConnectedEvent(final BluetoothAlmostConnectedEvent event) {
        LOGGER.info("onBluetoothAlmostConnectedEvent called");

        synchronized (this) {
            // Cancel the thread that completed the connection
            if (connectThread != null) {
                connectThread.cancel();
                connectThread = null;
            }

            // Cancel any thread currently running a connection
            if (connectedThread != null) {
                connectedThread.cancel();
                connectedThread = null;
            }

            // Cancel the accept thread because we only want to connect to one device
            if (acceptThread != null) {
                acceptThread.cancel();
                acceptThread = null;
            }

            this.state = STATE_CONNECTED;
            // Start the thread to manage the connection and perform transmissions
            connectedThread = new AndroidBluetoothConnectedThread(this.serviceLocator, event.getSocket());
            connectedThread.start();
            LOGGER.info("Connected Bluetooth thread started");
            this.serviceLocator.postOnEventBus(new BluetoothConnectedEvent(connectedThread));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void stop() {
        LOGGER.info("Bluetooth service stopped");

        synchronized (this) {
            if (connectThread != null) {
                connectThread.cancel();
                connectThread = null;
            }

            if (connectedThread != null) {
                connectedThread.cancel();
                connectedThread = null;
            }

            if (acceptThread != null) {
                acceptThread.cancel();
                acceptThread = null;
            }

            this.serviceLocator.postOnEventBus(new BluetoothConnectionLostEvent());
            state = STATE_NONE;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void write(final byte[] output) {
        // Create temporary object
        final AndroidBluetoothConnectedThread ready;
        // Synchronize a copy of the AndroidBluetoothConnectedThread
        synchronized (this) {
            if (state != STATE_CONNECTED) {
                return;
            }
            ready = connectedThread;
        }
        // Perform the write unsynchronized
        LOGGER.info("Write bytes: {}", Arrays.toString(output));
        ready.write(output);
    }

    /**
     * Return the current connection state.
     *
     * @return The current state
     */
    public BluetoothState getState() {
        synchronized (this) {
            return state;
        }
    }

    /**
     * Called when the connection with the paired device is lost
     *
     * @param event Describes the event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBluetoothConnectionLostEvent(final BluetoothConnectionLostEvent event) {
        LOGGER.info("onBluetoothConnectionLostEvent called");
        synchronized (this) {
            this.state = STATE_NONE;
        }

        try {
            this.start();
        } catch (IOException e) {
            LOGGER.error("Cold not restart connection after connection was lost", e);
        }
    }

    /**
     * Called when the connecting procedure failed
     *
     * @param event Describes the event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBluetoothConnectionFailedEvent(final BluetoothConnectionFailedEvent event) {
        LOGGER.info("onBluetoothConnectionFailedEvent called");
        synchronized (this) {
            this.state = STATE_NONE;
        }

        try {
            this.start();
        } catch (IOException e) {
            LOGGER.error("Cold not restart connection after failed event", e);
        }
    }

    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public final class LocalBinder extends Binder {
        /**
         * @return The bluetooth service itself
         */
        public AndroidBluetoothService getService() {
            // Return this instance of LocalService so clients can call public methods
            return AndroidBluetoothService.this;
        }
    }
}
