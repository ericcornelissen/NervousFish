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
import java.io.Serializable;
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
        public void send(byte[] buffer) {
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
}