package com.nervousfish.nervousfish.service_locator;

import com.nervousfish.nervousfish.modules.constants.IConstants;
import com.nervousfish.nervousfish.modules.cryptography.IEncryptor;
import com.nervousfish.nervousfish.modules.cryptography.IKeyGenerator;
import com.nervousfish.nervousfish.modules.database.IDatabase;
import com.nervousfish.nervousfish.modules.filesystem.IFileSystem;
import com.nervousfish.nervousfish.modules.pairing.IBluetoothHandler;
import com.nervousfish.nervousfish.modules.pairing.INfcHandler;
import com.nervousfish.nervousfish.modules.pairing.IQRHandler;

import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

public final class ServiceLocatorTest {
    private IServiceLocator serviceLocator;

    @Before
    public void setup() {
        serviceLocator = new ServiceLocator("foo");
    }
    
    @Test
    public void testGetAndroidFilesDir() {
        assertTrue("foo".equals(serviceLocator.getAndroidFilesDir()));
    }
    
    @Test
    public void testHiddenConstructor() {
        final String androidFilesDir = "foo";
        final IDatabase database = mock(IDatabase.class);
        final IKeyGenerator keyGenerator = mock(IKeyGenerator.class);
        final IEncryptor encryptor = mock(IEncryptor.class);
        final IFileSystem fileSystem = mock(IFileSystem.class);
        final IConstants constants = mock(IConstants.class);
        final IBluetoothHandler bluetoothHandler = mock(IBluetoothHandler.class);
        final INfcHandler nfcHandler = mock(INfcHandler.class);
        final IQRHandler qrHandler = mock(IQRHandler.class);
        IServiceLocator serviceLocator = new ServiceLocator(
                androidFilesDir
                , database
                , keyGenerator
                , encryptor
                , fileSystem
                , constants
                , bluetoothHandler
                , nfcHandler
                , qrHandler
        );
        assertEquals(androidFilesDir, serviceLocator.getAndroidFilesDir());
        assertEquals(database, serviceLocator.getDatabase());
        assertEquals(keyGenerator, serviceLocator.getKeyGenerator());
        assertEquals(encryptor, serviceLocator.getEncryptor());
        assertEquals(fileSystem, serviceLocator.getFileSystem());
        assertEquals(constants, serviceLocator.getConstants());
        assertEquals(bluetoothHandler, serviceLocator.getBluetoothHandler());
        assertEquals(nfcHandler, serviceLocator.getNFCHandler());
        assertEquals(qrHandler, serviceLocator.getQRHandler());
    }

    @Test
    public void testGetDatabase() {
        assertNotNull(serviceLocator.getDatabase());
    }

    @Test
    public void testGetKeyGenerator() {
        assertNotNull(serviceLocator.getKeyGenerator());
    }

    @Test
    public void testGetEncryptor() {
        assertNotNull(serviceLocator.getEncryptor());
    }

    @Test
    public void testGetFileSystem() {
        assertNotNull(serviceLocator.getFileSystem());
    }

    @Test
    public void testGetConstants() {
        assertNotNull(serviceLocator.getConstants());
    }

    @Test
    public void testGetBluetoothHandler() {
        assertNotNull(serviceLocator.getBluetoothHandler());
    }

    @Test
    public void testGetNfcHandler() {
        assertNotNull(serviceLocator.getNFCHandler());
    }

    @Test
    public void testGetQRHandler() {
        assertNotNull(serviceLocator.getQRHandler());
    }

    @Test(expected = ServiceLocator.ModuleNotFoundException.class)
    public void testModuleNotFound() {
        new ServiceLocator(null);
    }

    @Test
    public void testSerialization() {
        byte[] buf;
        try (
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(baos)
                ) {
            oos.writeObject(serviceLocator);
            buf = baos.toByteArray();

            try (
                    ByteArrayInputStream baos2 = new ByteArrayInputStream(buf);
                    ObjectInputStream oos2 = new ObjectInputStream(baos2)
            ) {
                ServiceLocator object = (ServiceLocator) oos2.readObject();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testSerializationModuleNotFoundException() {
        byte[] buf;
        try (
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(baos)
        ) {
            oos.writeObject(new ServiceLocator.ModuleNotFoundException("foo"));
            buf = baos.toByteArray();

            try (
                    ByteArrayInputStream baos2 = new ByteArrayInputStream(buf);
                    ObjectInputStream oos2 = new ObjectInputStream(baos2)
            ) {
                ServiceLocator.ModuleNotFoundException object = (ServiceLocator.ModuleNotFoundException) oos2.readObject();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
