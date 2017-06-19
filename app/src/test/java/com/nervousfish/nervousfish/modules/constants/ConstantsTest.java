package com.nervousfish.nervousfish.modules.constants;

import com.nervousfish.nervousfish.BaseTest;
import com.nervousfish.nervousfish.service_locator.IServiceLocator;

import org.junit.BeforeClass;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

// We cannot test much more than just that the results are not null.
// If we use serialization and change the names of the variables, then the tests don't compile anymore otherwise.
public class ConstantsTest {
    static IServiceLocator serviceLocator = mock(IServiceLocator.class);
    static Constants constants;

    @BeforeClass
    public static void init() {
        when(serviceLocator.getAndroidFilesDir()).thenReturn("foo");
        constants = (Constants) BaseTest.accessConstructor(Constants.class, serviceLocator);
    }

    @Test
    public void getDatabaseContactsPath() throws Exception {
        assertNotNull(constants.getDatabaseContactsPath());
    }

    @Test
    public void getDatabaseUserdataPath() throws Exception {
        assertNotNull(constants.getDatabaseUserdataPath());
    }

    @Test
    public void getUuid() throws Exception {
        assertNotNull(constants.getUuid());
    }

    @Test
    public void getSDPRecord() throws Exception {
        assertNotNull(constants.getSDPRecord());
    }

    @Test
    public void testSerialization() throws IOException, ClassNotFoundException {
        try (
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(bos)
        ) {
            oos.writeObject(constants);
            byte[] bytes = bos.toByteArray();
            try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
                 ObjectInputStream ois = new ObjectInputStream(bis)) {
                Object constants1 = ois.readObject();
                assertTrue(constants1.getClass().equals(Constants.class));
                Constants constants2 = (Constants) constants1;
                assertEquals(constants.getDatabaseContactsPath(), constants2.getDatabaseContactsPath());
                assertEquals(constants.getDatabasePath(), constants2.getDatabasePath());
                assertEquals(constants.getDatabaseUserdataPath(), constants2.getDatabaseUserdataPath());
            }
        }
    }
}
