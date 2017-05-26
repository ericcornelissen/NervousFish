package com.nervousfish.nervousfish.modules.pairing;

import android.bluetooth.BluetoothDevice;

import com.nervousfish.nervousfish.data_objects.Contact;
import com.nervousfish.nervousfish.events.NewContactsReceivedEvent;
import com.nervousfish.nervousfish.events.SerializedBufferReceivedEvent;
import com.nervousfish.nervousfish.exceptions.DeserializationException;
import com.nervousfish.nervousfish.modules.database.DatabaseException;
import com.nervousfish.nervousfish.service_locator.IServiceLocator;
import com.nervousfish.nervousfish.service_locator.ModuleWrapper;
import com.nervousfish.nervousfish.service_locator.NervousFish;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

/**
 * Helper class for {@link AndroidBluetoothService}
 */
public class AndroidBluetoothHandler extends APairingHandler implements IBluetoothHandler {
    private static final long serialVersionUID = -6465987636766819498L;
    private static final Logger LOGGER = LoggerFactory.getLogger("AndroidBluetoothHandler");
    private final IServiceLocator serviceLocator;

    /**
     * Prevents construction from outside the class.
     *
     * @param serviceLocator Can be used to get access to other modules
     */
    private AndroidBluetoothHandler(final IServiceLocator serviceLocator) {
        super(serviceLocator);
        this.serviceLocator = serviceLocator;
    }

    /**
     * Creates a new instance of itself and wraps it in a {@link ModuleWrapper} so that only an
     * {@link IServiceLocator}
     *
     * @param serviceLocator The new service locator
     * @return A wrapper around a newly created instance of this class
     */
    public static ModuleWrapper<AndroidBluetoothHandler> newInstance(final IServiceLocator serviceLocator) {
        return new ModuleWrapper<>(new AndroidBluetoothHandler(serviceLocator));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void send(final byte[] buffer) {
        //dummy
    }

    /**
     * Deserialize the instance using readObject to ensure invariants and security.
     *
     * @param stream The serialized object to be deserialized
     */
    private void readObject(final ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        ensureClassInvariant();
    }

    /**
     * Used to improve performance / efficiency
     *
     * @param stream The stream to which this object should be serialized to
     */
    private void writeObject(final ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
    }

    /**
     * Ensure that the instance meets its class invariant
     *
     * @throws InvalidObjectException Thrown when the state of the class is unstbale
     */
    private void ensureClassInvariant() throws InvalidObjectException {
        // No checks to perform
    }

    @Override
    public void start() {
        this.getService().start();
    }

    @Override
    public void connect(BluetoothDevice device) {
        this.getService().connect(device);
    }

    @Override
    public void stop() {
        this.getService().stop();
    }

    /**
     * {@inheritDoc}
     */
    public void writeAllContacts() throws IOException {
        final List<Contact> list = this.getServiceLocator().getDatabase().getAllContacts();
        for (final Contact e : list) {
            sendContact(e);
        }
    }

    /**
     * Serializes a contact object and writes it, which is implemented in the specific subclass.
     *
     * @param contact contact to serialize
     * @throws IOException When deserialization doesn't go well.
     */
    @Override
    void sendContact(final Contact contact) throws IOException {
        LOGGER.info("Begin writing contact: " + contact.getName());
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
        this.getService().write(bytes);
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
        final List<Contact> list = this.serviceLocator.getDatabase().getAllContacts();
        for (final Contact e : list) {
            if (e.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSerializedBufferReceivedEvent(final SerializedBufferReceivedEvent event) {
        this.newContact(event.getBuffer());
    }

    /**
     * Deserializes a contact through a byte array and sends it to the database.
     *
     * @param bytes byte array representing a contact
     * @return Whether or not the process finished successfully
     */
    Contact newContact(final byte[] bytes) {
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
                this.serviceLocator.getDatabase().addContact(contact);
            }
        } catch (final IOException e) {
            LOGGER.warn("DB issued an error while saving contact");
            e.printStackTrace();
            throw new DatabaseException("DB issued an error while saving contact description: " + e.toString());
        }
        serviceLocator.postOnEventBus(new NewContactsReceivedEvent());
        return contact;
    }

    private IBluetoothHandlerService getService() {
        return ((NervousFish) NervousFish.getContext()).getBluetoothService();
    }
}
