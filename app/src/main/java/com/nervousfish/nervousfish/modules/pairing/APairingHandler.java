package com.nervousfish.nervousfish.modules.pairing;

import com.nervousfish.nervousfish.data_objects.Contact;
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
import java.io.Serializable;
import java.util.List;

/**
 * Contains common methods shared by all pairing modules.
 */
abstract class APairingHandler implements IPairingHandler, Serializable {

    private static final Logger LOGGER = LoggerFactory.getLogger("APairingHandler");

    private final IServiceLocator serviceLocator;
    private final IDatabase database;

    /**
     * Prevent instantiation by other classes outside it's package
     *
     * @param serviceLocator the serviceLocator which will provide means to access other modules
     */
    APairingHandler(final IServiceLocator serviceLocator) {
        this.serviceLocator = serviceLocator;
        this.database = serviceLocator.getDatabase();
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
            throw new DeserializationException(" Couldn't start deserialization!");
        } finally {
            if (bis != null) {
                try {
                    bis.close();
                } catch (final IOException e) {
                    LOGGER.warn("Couldn't close the ByteArrayInputStream");
                }
            }
            if (ois != null) {
                try {
                    ois.close();
                } catch (final IOException e) {
                    LOGGER.warn("Couldn't close the ObjectInputStream");
                }
            }
        }
        try {
            LOGGER.info("Checking if the contact exists...");
            if (!checkExists(contact)) {
                LOGGER.info("Adding contact to database...");
                database.addContact(contact);
            } else {
                LOGGER.warn("Contact already existed...");
            }
        } catch (final IOException e) {
            LOGGER.warn("DB issued an error while saving contact");
            throw new DatabaseException("DB issued an error while saving contact");
        }
        return contact;
    }

    IServiceLocator getServiceLocator() {
        return this.serviceLocator;
    }

    /**
     * Write to the buffer to the world
     *
     * @param buffer The bytes to write
     */
    abstract void write(final byte[] buffer);
}
