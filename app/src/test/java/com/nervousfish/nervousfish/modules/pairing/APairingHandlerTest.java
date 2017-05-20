package com.nervousfish.nervousfish.modules.pairing;

import com.nervousfish.nervousfish.data_objects.Contact;
import com.nervousfish.nervousfish.data_objects.RSAKey;
import com.nervousfish.nervousfish.data_objects.SimpleKey;
import com.nervousfish.nervousfish.modules.database.IDatabase;
import com.nervousfish.nervousfish.service_locator.IServiceLocator;

import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class APairingHandlerTest {
    private class PairingHandler extends APairingHandler {
        private static final long serialVersionUID = 1767816444273833493L;
        byte[] myBuffer;

        /**
         * Prevent instatiation from non-subclasses outside the package
         *
         * @param serviceLocator bump
         */
        PairingHandler(IServiceLocator serviceLocator) {
            super(serviceLocator);
        }

        @Override
        void write(byte[] buffer) {
            myBuffer = buffer;
        }

    }

    private byte[] serialize(Contact contact) throws IOException {
        ByteArrayOutputStream bos = null;
        ObjectOutputStream oos = null;
        try {
            bos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(bos);
            oos.writeObject(contact);
            oos.flush();
           return bos.toByteArray();
        } finally {
            if (oos != null) {
                oos.close();
            }
            if (bos != null) {
                bos.close();
            }
        }
    }

    private PairingHandler tmp, phspy;
    private IDatabase database;

    @Before
    public void setup() throws Exception {
        IServiceLocator serviceLocator = mock(IServiceLocator.class);
        database = mock(IDatabase.class);
        when(serviceLocator.getDatabase()).thenReturn(database);
        tmp = new PairingHandler(serviceLocator);
        phspy = spy(tmp);
    }

    @Test
    public void writeCheckContactSimpleKeyTest() throws IOException {
        Contact contact = new Contact("Test", new SimpleKey("Test", ""));
        phspy.writeContact(contact);
        verify(phspy).checkExists(contact);
        verify(phspy).writeContact(contact);
        verify(phspy).write(phspy.myBuffer);
        assertTrue(Arrays.equals(serialize(contact), phspy.myBuffer));
    }

    @Test
    public void writeCheckContactRSAKeyTest() throws IOException {
        Contact contact = new Contact("Test", new RSAKey("Test","1234", "0"));
        phspy.writeContact(contact);
        verify(phspy).checkExists(contact);
        verify(phspy).writeContact(contact);
        verify(phspy).write(phspy.myBuffer);
        assertTrue(Arrays.equals(serialize(contact), phspy.myBuffer));
    }

    @Test
    public void writeAllContactsTest() throws IOException {
        List<Contact> list = new LinkedList<>();
        Contact c1 = new Contact("Test", new SimpleKey("Test", ""));
        Contact c2 = new Contact("Test2", new RSAKey("Test", "1234", "0"));
        list.add(c1);
        list.add(c2);
        when(database.getAllContacts()).thenReturn(list);
        phspy.writeAllContacts();
        verify(phspy, times(1)).writeContact(c1);
        verify(phspy, times(1)).writeContact(c2);
    }

    @Test
    public void writeAllContactsEmptyTest() throws IOException {
        when(database.getAllContacts()).thenReturn(new LinkedList<Contact>());
        phspy.writeAllContacts();
        verify(phspy, never()).writeContact((Contact) any());
    }

    @Test
    public void saveContactSimpleKeyTest() throws Exception {
        Contact contact = new Contact("Test", new SimpleKey("Test", ""));
        tmp.saveContact(serialize(contact));
        verify(database).addContact(contact);
    }

    @Test
    public void saveContactRSAKeyTest() throws Exception {
        Contact contact = new Contact("Test", new RSAKey("Test", "1234", "0"));
        tmp.saveContact(serialize(contact));
        verify(database).addContact(contact);
    }

    @Test
    public void checkExistsEmptyTest() throws IOException {
        when(database.getAllContacts()).thenReturn(new LinkedList<Contact>());
        Contact contact = new Contact("Test", new SimpleKey("Test", ""));
        assertFalse(phspy.checkExists(contact));
    }

    @Test
    public void checkExistsDuplicateTest() throws IOException {
        List<Contact> list = new LinkedList<>();
        Contact contact = new Contact("Test", new SimpleKey("Test", ""));
        list.add(contact);
        when(database.getAllContacts()).thenReturn(list);
        assertTrue(tmp.checkExists(contact));
    }

}