package com.nervousfish.nervousfish;

import com.nervousfish.nervousfish.modules.constants.IConstants;
import com.nervousfish.nervousfish.modules.pairing.DummyBluetoothHandler;
import com.nervousfish.nervousfish.modules.pairing.IBluetoothHandler;
import com.nervousfish.nervousfish.service_locator.IServiceLocator;
import com.nervousfish.nervousfish.service_locator.IServiceLocatorCreator;

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

    private IBluetoothHandler bConService;

    @Before
    public void setup() {
        when(serviceLocatorCreator.getServiceLocator()).thenReturn(serviceLocator);
        when(serviceLocator.getConstants()).thenReturn(constants);

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
