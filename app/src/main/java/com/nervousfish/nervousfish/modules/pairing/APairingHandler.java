package com.nervousfish.nervousfish.modules.pairing;

import com.nervousfish.nervousfish.data_objects.Contact;
import com.nervousfish.nervousfish.modules.database.IDatabase;
import com.nervousfish.nervousfish.service_locator.IServiceLocatorCreator;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

/**
 * Contains common methods shared by all pairing modules.
 */
@SuppressWarnings("PMD.AbstractClassWithoutAbstractMethod")
abstract class APairingHandler {

    private final IServiceLocatorCreator serviceLocatorCreator;
    private final IDatabase database;

    /**
     * Prevent instatiation from non-subclasses outside the package
     */
    APairingHandler(IServiceLocatorCreator serviceLocatorCreator) {
        // Prevent instatiation from non-subclasses outside the package
        this.serviceLocatorCreator = serviceLocatorCreator;
        this.serviceLocatorCreator.registerToEventBus(this);
        database = this.serviceLocatorCreator.getServiceLocator().getDatabase();
    }

    /**
     *
     * @throws IOException When deserialization doesn't go well.
     */
    public void writeAllContacts() throws IOException {
        List<Contact> list = database.getAllContacts();
        for (Contact e: list) {
            byte[] bytes = null;
            ByteArrayOutputStream bos = null;
            ObjectOutputStream oos = null;
            try {
                bos = new ByteArrayOutputStream();
                oos = new ObjectOutputStream(bos);
                oos.writeObject(e);
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
    }

    public boolean saveContact(final byte[] bytes){
        Contact contact = null;
        ByteArrayInputStream bis = null;
        ObjectInputStream ois = null;
        try {
            bis = new ByteArrayInputStream(bytes);
            ois = new ObjectInputStream(bis);
            contact = (Contact) ois.readObject();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (ois != null) {
                try {
                    ois.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        try {
            database.addContact(contact);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * Write to the connected Device via outputstream.
     *
     * @param buffer The bytes to write
     */
    public abstract void write(final byte[] buffer);
}
