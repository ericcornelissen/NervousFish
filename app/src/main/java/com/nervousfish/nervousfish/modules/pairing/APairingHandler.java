package com.nervousfish.nervousfish.modules.pairing;

import com.nervousfish.nervousfish.data_objects.Contact;
import com.nervousfish.nervousfish.data_objects.communication.FileWrapper;
import com.nervousfish.nervousfish.data_objects.tap.DataWrapper;
import com.nervousfish.nervousfish.exceptions.DeserializationException;
import com.nervousfish.nervousfish.modules.constants.IConstants;
import com.nervousfish.nervousfish.modules.database.DatabaseException;
import com.nervousfish.nervousfish.modules.database.IDatabase;
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Contains common methods shared by all pairing modules.
 */
// default value is now 7, I have 9 with one probably gone soon
@SuppressWarnings("checkstyle:classdataabstractioncoupling")
abstract class APairingHandler implements Serializable {

    private static final Logger LOGGER = LoggerFactory.getLogger("APairingHandler");
    private static final long serialVersionUID = 1656974573024980860L;
    private static final String CONTACT_CLASS = "Contact";
    private final IServiceLocator serviceLocator;
    private final IDatabase database;
    private final IConstants constants;

    /**
     * Prevent instantiation by other classes outside it's package
     *
     * @param serviceLocator the serviceLocator which will provide means to access other modules
     */
    APairingHandler(final IServiceLocator serviceLocator) {
        this.serviceLocator = serviceLocator;
        this.database = serviceLocator.getDatabase();
        this.constants = serviceLocator.getConstants();
    }

    /**
     * A QOL mehtod that will send a certain list of contacts
     *
     * @param contacts A list with contacts
     */
    public void sendAllContacts(final Collection<Contact> contacts) throws IOException {
        LOGGER.info("Begin writing multiple contacts :" + contacts.toString());
        final byte[] bytes;
        final DataWrapper dWrapper = new DataWrapper(new ArrayList<>(contacts));
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(bos)) {
            oos.writeObject(dWrapper);
            oos.flush();
            bytes = bos.toByteArray();
        }
        send(bytes);
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
        final DataWrapper dWrapper = new DataWrapper(contact);
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(bos)) {
            oos.writeObject(dWrapper);
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
     * The main method that deals with the result of the scocket stream
     * and dispatches the data approprietly in order to parse it.
     *
     * @param bytes Data represented as bytes (usually received from the socket)
     */
    @SuppressWarnings({"checkstyle:cyclomaticcomplexity", "PMD.StdCyclomaticComplexity", "PMD.CyclomaticComplexity"})
    void parseInput(final byte[] bytes) {
        LOGGER.info("Reading these bytes: %s", bytes);
        final DataWrapper dataWrapper;
        try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
             ObjectInputStream ois = new ObjectInputStream(bis)) {
            dataWrapper = (DataWrapper) ois.readObject();
        } catch (final ClassNotFoundException | IOException e) {
            LOGGER.error(" Couldn't start deserialization!", e);
            throw (DeserializationException) new DeserializationException("The contact could not be deserialized").initCause(e);
        }
        switch (dataWrapper.getClazz().getSimpleName()) {
            case CONTACT_CLASS:
                LOGGER.info("Content was a contact");
                saveContact((Contact) dataWrapper.getData());
                break;
            case "ArrayList":
                LOGGER.info("Content was a List");
                final ArrayList<Object> list = (ArrayList<Object>) dataWrapper.getData();
                if (!list.isEmpty()) {
                    for (final Object o : list) {
                        if (o.getClass().getSimpleName().equals(CONTACT_CLASS)) {
                            LOGGER.info("Call to save this contact: " + o);
                            saveContact((Contact) o);
                        } else if (o.getClass().getSimpleName().equals("SingleTap")) {
                            LOGGER.info("Call to analyze this tap :" + o);
                            //analyzeTaps(o); or something like that
                        }
                    }
                }
                break;
            case "String" :
                final String verification = (String) dataWrapper.getData();
                LOGGER.info("Content was a String" + verification);
                if (verification.contains("Rhythm")) {
                    LOGGER.info("");
                    //go to rhythm activity
                } else if (verification.contains("Visual")) {
                    LOGGER.info("");
                    //go to visual activity
                }
                break;
            default:
                LOGGER.info("Content was unknown for the parser :" + dataWrapper.getClazz().getSimpleName());
                break;
        }
    }

    /**
     * Deserializes a contact through a byte array and sends it to the database.
     *
     * @param contact the {@link Contact} to save
     * @return Whether or not the process finished successfully
     */
    Contact saveContact(final Contact contact) {
        LOGGER.info("Saving this contact: %s", contact);
        try {
            database.addContact(contact);
        } catch (IOException e) {
            LOGGER.warn("Contact already existed", e);
            throw (DatabaseException) new DatabaseException("Contact already existed in the database").initCause(e);
        }
        return contact;
    }

    /*
    void analayzeTaps(final MultiTap multiTap) {

    }
    */

    /**
     * * A method to send a contact file
     *
     * @return content of the file to be sent in byte
     * @throws IOException Can be because of serialization or if there is a read error
     */
    byte[] sendContactFile() throws IOException {
        //to ensure that the file is not modified during read
        final RandomAccessFile f = new RandomAccessFile(constants.getDatabaseContactsPath(), "rw");
        final byte[] fileData = new byte[(int) f.length()];
        final byte[] bytes;
        f.readFully(fileData);
        final FileWrapper fWrapper = new FileWrapper(fileData);
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(bos)) {
            oos.writeObject(fWrapper);
            oos.flush();
            bytes = bos.toByteArray();
        }
        send(bytes);
        return bytes;
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
