package com.nervousfish.nervousfish;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static com.nervousfish.nervousfish.BaseTest.accessConstructor;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by smironov on 17/05/05.
 */


public class BluetoothConnectionServiceTest {

    private IServiceLocatorCreator serviceLocatorCreator = mock(IServiceLocatorCreator.class);
    private IServiceLocator serviceLocator = mock(IServiceLocator.class);
    private IConstants constants = mock(IConstants.class);

    private DummyBluetoothHandler bConService;

    @Before
    public void setup() {
        when(serviceLocatorCreator.getServiceLocator()).thenReturn(serviceLocator);
        when(serviceLocator.getConstants()).thenReturn(constants);
        when(constants.getDatabaseContactsPath()).thenReturn(CONTACTS_PATH);
        when(constants.getDatabaseUserdataPath()).thenReturn(USERDATA_PATH);

        this.bConService = (BluetoothConnectionService) accessConstructor(BluetoothConnectionService.class, serviceLocatorCreator);
        this.bConService.onSLReadyEvent(mock(SLReadyEvent.class));
    }

    @After
    public void tearDown() {

    }

    @Test
    public void testCreateConnection() {

    }
}
