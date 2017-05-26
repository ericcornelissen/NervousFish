package com.nervousfish.nervousfish.modules.pairing;

import com.nervousfish.nervousfish.data_objects.Contact;
import com.nervousfish.nervousfish.data_objects.RSAKey;
import com.nervousfish.nervousfish.data_objects.SimpleKey;
import com.nervousfish.nervousfish.data_objects.communication.FileWrapper;
import com.nervousfish.nervousfish.data_objects.tap.DataWrapper;
import com.nervousfish.nervousfish.modules.constants.IConstants;
import com.nervousfish.nervousfish.modules.database.IDatabase;
import com.nervousfish.nervousfish.service_locator.IServiceLocator;

import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class APairingHandlerTest implements Serializable {
    private static final long serialVersionUID = -674831556056079552L;

    private static class PairingHandler extends APairingHandler {
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
        void send(byte[] buffer) {
            myBuffer = buffer;
        }

    }

    private byte[] serialize(Serializable contact) throws IOException {
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

    private byte[] serializeFile() throws IOException {
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
        return bytes;
    }

    private PairingHandler tmp, phspy;
    private IDatabase database;
    private IConstants constants;

    @Before
    public void setup() throws Exception {
        IServiceLocator serviceLocator = mock(IServiceLocator.class);
        database = mock(IDatabase.class);
        when(serviceLocator.getDatabase()).thenReturn(database);
        constants = mock(IConstants.class);
        when(serviceLocator.getConstants()).thenReturn(constants);
        when(constants.getDatabaseContactsPath()).thenReturn(new File("E:\\docs\\NervousFish\\app\\src\\main\\AndroidManifest.xml").getAbsolutePath());
        tmp = new PairingHandler(serviceLocator);
        phspy = spy(tmp);
    }

    @Test
    public void writeCheckContactSimpleKeyTest() throws IOException {
        Contact contact = new Contact("Test", new SimpleKey("Test", ""));
        phspy.sendContact(contact);
        verify(phspy).sendContact(contact);
        verify(phspy).send(phspy.myBuffer);
    }

    @Test
    public void writeCheckContactRSAKeyTest() throws IOException {
        Contact contact = new Contact("Test", new RSAKey("Test","1234", "0"));
        phspy.sendContact(contact);
        verify(phspy).sendContact(contact);
        verify(phspy).send(phspy.myBuffer);
    }

    @Test
    public void writeAllContactsTest() throws IOException {
        List<Contact> list = new LinkedList<>();
        Contact c1 = new Contact("Test", new SimpleKey("Test", ""));
        Contact c2 = new Contact("Test2", new RSAKey("Test", "1234", "0"));
        list.add(c1);
        list.add(c2);
        phspy.sendAllContacts(list);
        verify(phspy, times(1)).send(any(byte[].class));
        verify(phspy, times(1)).send(any(byte[].class));
    }

    @Test
    public void writeAllContactsEmptyTest() throws IOException {
        phspy.sendAllContacts(new LinkedList<Contact>());
        verify(phspy, never()).sendContact((Contact) any());
    }

    @Test
    public void saveContactSimpleKeyTest() throws Exception {
        Contact contact = new Contact("Test", new SimpleKey("Test", ""));
        assertEquals(tmp.saveContact(contact), contact);
        verify(database).addContact(contact);
    }

    @Test
    public void saveContactRSAKeyTest() throws Exception {
        Contact contact = new Contact("Test", new RSAKey("Test", "1234", "0"));
        assertEquals(tmp.saveContact(contact), contact);
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
    @Test
    public void sendContactFileTest() throws IOException {
        assertTrue(Arrays.equals(tmp.sendContactFile(), serializeFile()));
    }

    @Test
    public void parseInputSingleContactTest() throws IOException {
        Contact contact = new Contact("Test", new SimpleKey("Test", ""));
        DataWrapper dataWrapper = new DataWrapper(contact);
        phspy.parseInput(serialize(dataWrapper));
        verify(phspy).saveContact(any(Contact.class));
    }

    @Test
    public void parseInputMultipleContactTest() throws IOException {
        List<Contact> list = new ArrayList<>();
        Contact c1 = new Contact("Test", new SimpleKey("Test", ""));
        Contact c2 = new Contact("Test2", new RSAKey("Test", "1234", "0"));
        list.add(c1);
        list.add(c2);
        DataWrapper dataWrapper = new DataWrapper((ArrayList)list);
        phspy.parseInput(serialize(dataWrapper));
        verify(phspy, times(2)).saveContact(any(Contact.class));
    }

}