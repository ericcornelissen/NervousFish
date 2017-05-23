package com.nervousfish.nervousfish.modules.pairing;

import com.nervousfish.nervousfish.data_objects.Contact;
import com.nervousfish.nervousfish.exceptions.DeserializationException;
import com.nervousfish.nervousfish.modules.database.DatabaseException;
import com.nervousfish.nervousfish.modules.database.IDatabase;
import com.nervousfish.nervousfish.modules.filesystem.IFileSystem;
import com.nervousfish.nervousfish.service_locator.IServiceLocator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.util.List;

/**
 * Contains common methods shared by all pairing modules.
 */
abstract class APairingHandler implements IPairingHandler, Serializable {

    private static final Logger LOGGER = LoggerFactory.getLogger("APairingHandler");
    private static final long serialVersionUID = 1656974573024980860L;

    private final IServiceLocator serviceLocator;
    private final IDatabase database;
    private final IFileSystem fileSystem;

    /**
     * Prevent instantiation by other classes outside it's package
     *
     * @param serviceLocator the serviceLocator which will provide means to access other modules
     */
    APairingHandler(final IServiceLocator serviceLocator) {
        this.serviceLocator = serviceLocator;
        this.database = serviceLocator.getDatabase();
        this.fileSystem = serviceLocator.getFileSystem();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sendAllContacts() throws IOException {
        final List<Contact> list = database.getAllContacts();
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
    void sendContact(final Contact contact) throws IOException {
        LOGGER.info("Begin writing contact :" + contact.getName());
        final byte[] bytes;
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(bos)) {
            oos.writeObject(contact);
            oos.flush();
            bytes = bos.toByteArray();
        }
        send(bytes);
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
        LOGGER.info("Saving these bytes: %s", bytes);
        final Contact contact;
        try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
             ObjectInputStream ois = new ObjectInputStream(bis)) {
            contact = (Contact) ois.readObject();
        } catch (final ClassNotFoundException | IOException e) {
            LOGGER.error(" Couldn't start deserialization!", e);
            throw (DeserializationException) new DeserializationException("The contact could not be deserialized").initCause(e);
        }
        try {
            database.addContact(contact);
        } catch (IOException e) {
            LOGGER.warn("Contact already existed", e);
            throw (DatabaseException) new DatabaseException("Contact already existed in the database").initCause(e);
        }
        return contact;
    }

    void sendContactFile() throws IOException {
        //get the path via Ifilesystem, for now use a dummy
        RandomAccessFile f = new RandomAccessFile("temp", "rw"); //to ensure that the file is not modified during read
        final byte[] bytes = new byte[(int)f.length()];
        f.readFully(bytes);
        send(bytes);
    }

    protected IServiceLocator getServiceLocator() {
        return this.serviceLocator;
    }

    /**
     * Write the buffer to the world
     *
     * @param buffer The bytes to send
     */
    abstract void send(byte[] buffer);
}
