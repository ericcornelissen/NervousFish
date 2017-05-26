package com.nervousfish.nervousfish.modules.pairing;

import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.nervousfish.nervousfish.data_objects.Contact;
import com.nervousfish.nervousfish.events.BluetoothConnectedEvent;
import com.nervousfish.nervousfish.events.BluetoothDisconnectedEvent;
import com.nervousfish.nervousfish.exceptions.DatabaseException;
import com.nervousfish.nervousfish.exceptions.DeserializationException;
import com.nervousfish.nervousfish.modules.database.IDatabase;
import com.nervousfish.nervousfish.service_locator.IServiceLocator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Created by jverb on 5/26/2017.
 */

public class AndroidBluetoothService extends Service {
    // Constants that indicate the current connection state
    static final int STATE_NONE = 0;       // we're doing nothing
    static final int STATE_LISTEN = 1;     // now listening for incoming connections
    static final int STATE_CONNECTING = 2; // now initiating an outgoing connection
    static final int STATE_CONNECTED = 3;  // now connected to a remote device
    // Unique UUID for this application
    static final UUID MY_UUID_SECURE =
            UUID.fromString("2d7c6682-3b84-4d00-9e61-717bac0b2643");
    // Name for the SDP record when creating server socket
    static final String NAME_SECURE = "BluetoothChatSecure";
    private static final Logger LOGGER = LoggerFactory.getLogger("AndroidBluetoothHandler");
    ConnectThread connectThread;
    int mState = STATE_NONE;
    private AcceptThread acceptThread;
    private ConnectedThread connectedThread;
    IServiceLocator serviceLocator;
    private IDatabase database;

    // Binder given to clients
    private final IBinder mBinder = new LocalBinder();

    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class LocalBinder extends Binder {
        public AndroidBluetoothService getService() {
            // Return this instance of LocalService so clients can call public methods
            return AndroidBluetoothService.this;
        }
    }

    public void setServiceLocator(final IServiceLocator serviceLocator) {
        this.serviceLocator = serviceLocator;
        this.database = serviceLocator.getDatabase();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    /**
     * {@inheritDoc}
     */
    public void start() {
        LOGGER.info("Bluetooth Service started");

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
                acceptThread = new AcceptThread(this, this.serviceLocator);
                acceptThread.start();
            }
        }
    }


    /**
     * {@inheritDoc}
     */
    public void connect(final BluetoothDevice device) {
        LOGGER.info("Connect Bluetooth thread initialized");

        synchronized (this) {
            // Cancel any thread attempting to make a connection
            if (mState == STATE_CONNECTING && connectThread != null) {
                connectThread.cancel();
                connectThread = null;
            }

            // Cancel any thread currently running a connection
            if (connectedThread != null) {
                connectedThread.cancel();
                connectedThread = null;
            }

            // Start the thread to connect with the given device
            connectThread = new ConnectThread(this, device);
            connectThread.start();
        }
    }

    /**
     * {@inheritDoc}
     */
    public void connected(final BluetoothSocket socket, final BluetoothDevice device) {
        LOGGER.info("Connected Bluetooth thread started");

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

            // Start the thread to manage the connection and perform transmissions
            connectedThread = new ConnectedThread(this, socket);
            connectedThread.start();
            this.serviceLocator.postOnEventBus(new BluetoothConnectedEvent());
        }
    }

    /**
     * {@inheritDoc}
     */
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

            mState = STATE_NONE;
            this.serviceLocator.postOnEventBus(new BluetoothDisconnectedEvent());
        }
    }

    /**
     * Write to the ConnectedThread in an unsynchronized manner
     *
     * @param output The bytes to write
     * @see ConnectedThread#write(byte[])
     */
    public void write(final byte[] output) {
        // Create temporary object
        final ConnectedThread ready;
        // Synchronize a copy of the ConnectedThread
        synchronized (this) {
            if (mState != STATE_CONNECTED) {
                return;
            }
            ready = connectedThread;
        }
        // Perform the write unsynchronized
        LOGGER.info("Write bytes : " + Arrays.toString(output));
        ready.write(output);
    }

    /**
     * Indicate that the connection attempt failed and notify the UI Activity.
     */
    void connectionFailed() {

        mState = STATE_NONE;
        this.serviceLocator.postOnEventBus(new BluetoothDisconnectedEvent());

        // Start the service over to restart listening mode
        this.start();
    }

    /**
     * Indicate that the connection was lost and notify the UI Activity.
     */
    void connectionLost() {

        mState = STATE_NONE;
        this.serviceLocator.postOnEventBus(new BluetoothDisconnectedEvent());

        // Start the service over to restart listening mode
        this.start();
    }

    /**
     * Return the current connection state.
     */
    public int getState() {
        synchronized (this) {
            return mState;
        }
    }













    /**
     * {@inheritDoc}
     */
    public void writeAllContacts() throws IOException {
        final List<Contact> list = database.getAllContacts();
        for (final Contact e : list) {
            writeContact(e);
        }
    }

    /**
     * Serializes a contact object and writes it, which is implemented in the specific subclass.
     *
     * @param contact contact to serialize
     * @throws IOException When deserialization doesn't go well.
     */
    void writeContact(final Contact contact) throws IOException {
        LOGGER.info("Begin writing contact :" + contact.getName());
        byte[] bytes = null;
        ByteArrayOutputStream bos = null;
        ObjectOutputStream oos = null;
        try {
            bos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(bos);
            oos.writeObject(contact);
            oos.flush();
            bytes = bos.toByteArray();
        } finally {
            if (oos != null) {
                oos.close();
            }
            if (bos != null) {
                bos.close();
            }
        }
        write(bytes);
    }

    /**
     * Checks if a name of a given contact exists in the database.
     *
     * @param contact A contact object
     * @return true when a contact with the same exists in the database
     * @throws IOException When database fails to respond
     */
    boolean checkExists(final Contact contact) throws IOException {
        final String name = contact.getName();
        final List<Contact> list = database.getAllContacts();
        for (final Contact e : list) {
            if (e.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Deserializes a contact through a byte array and sends it to the database.
     *
     * @param bytes byte array representing a contact
     * @return Whether or not the process finished successfully
     */
    Contact saveContact(final byte[] bytes) {
        LOGGER.info("Saving these bytes :" + bytes);
        Contact contact = null;
        ByteArrayInputStream bis = null;
        ObjectInputStream ois = null;
        try {
            bis = new ByteArrayInputStream(bytes);
            ois = new ObjectInputStream(bis);
            contact = (Contact) ois.readObject();
        } catch (final ClassNotFoundException | IOException e) {
            LOGGER.error(" Couldn't start deserialization!");
            e.printStackTrace();
            throw new DeserializationException(" Couldn't start deserialization! description: " + e.toString());
        } finally {
            if (bis != null) {
                try {
                    bis.close();
                } catch (final IOException e) {
                    e.printStackTrace();
                    LOGGER.warn("Couldn't close the ByteArrayInputStream");
                }
            }
            if (ois != null) {
                try {
                    ois.close();
                } catch (final IOException e) {
                    e.printStackTrace();
                    LOGGER.warn("Couldn't close the ObjectInputStream");
                }
            }
        }
        try {
            LOGGER.info("Checking if the contact exists...");
            if (checkExists(contact)) {
                LOGGER.warn("Contact already existed...");
            } else {
                LOGGER.info("Adding contact to database...");
                database.addContact(contact);
            }
        } catch (final IOException e) {
            LOGGER.warn("DB issued an error while saving contact");
            e.printStackTrace();
            throw new DatabaseException("DB issued an error while saving contact description: " + e.toString());
        }
        return contact;
    }
}
