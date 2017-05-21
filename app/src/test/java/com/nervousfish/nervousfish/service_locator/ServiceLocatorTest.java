package com.nervousfish.nervousfish.service_locator;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

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
}
