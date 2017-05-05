package com.nervousfish.nervousfish.test;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;

import com.nervousfish.nervousfish.BluetoothConnectionService;
import com.nervousfish.nervousfish.events.SLReadyEvent;
import com.nervousfish.nervousfish.modules.constants.IConstants;
import com.nervousfish.nervousfish.modules.pairing.IBluetoothHandler;
import com.nervousfish.nervousfish.service_locator.IServiceLocator;
import com.nervousfish.nervousfish.service_locator.IServiceLocatorCreator;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.internal.invocation.MockitoMethod;
import org.mockito.internal.matchers.Any;
import org.mockito.internal.matchers.NotNull;


import java.io.IOException;
import java.util.UUID;

import static com.nervousfish.nervousfish.test.BaseTest.accessConstructor;
import static com.nervousfish.nervousfish.test.BaseTest.accessMethod;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.notNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by smironov on 17/05/05.
 */


public class BluetoothConnectionServiceTest {

    private IServiceLocatorCreator serviceLocatorCreator = mock(IServiceLocatorCreator.class);
    private IServiceLocator serviceLocator = mock(IServiceLocator.class);
    private Handler handler = mock(Handler.class);
    private IConstants constants = mock(IConstants.class);
    private BluetoothDevice device = mock(BluetoothDevice.class);
    private BluetoothSocket socket = mock(BluetoothSocket.class);

    private IBluetoothHandler bConService;
    private IBluetoothHandler bConService2;
    private IBluetoothHandler bConService3;

    @Before
    public void setup() {
        when(serviceLocatorCreator.getServiceLocator()).thenReturn(serviceLocator);
        when(serviceLocator.getConstants()).thenReturn(constants);
        try {
            when(device.createRfcommSocketToServiceRecord((UUID)notNull())).thenReturn(socket);
        } catch (IOException e) {
            e.printStackTrace();
        }
        when(accessMethod(bConService, "connectionFailed", new Object[0])).thenThrow(IOException.class); //this probably won't work

        this.bConService = (BluetoothConnectionService) accessConstructor(BluetoothConnectionService.class, new Object[]{handler ,serviceLocatorCreator});
        this.bConService2 = (BluetoothConnectionService) accessConstructor(BluetoothConnectionService.class, new Object[]{handler ,serviceLocatorCreator});
        this.bConService3 = (BluetoothConnectionService) accessConstructor(BluetoothConnectionService.class, new Object[]{handler ,serviceLocatorCreator});
        this.bConService.onSLReadyEvent(mock(SLReadyEvent.class));
        this.bConService2.onSLReadyEvent(mock(SLReadyEvent.class));
        this.bConService3.onSLReadyEvent(mock(SLReadyEvent.class));
    }

    @After
    public void tearDown() {
        accessMethod(bConService, "stop", new Object[0]);
        accessMethod(bConService2, "stop", new Object[0]);
        accessMethod(bConService3, "stop", new Object[0]);
    }

    @Test
    public void testConstructorState() {
        assertEquals(0, accessMethod(bConService, "getState", new Object[0]));
    }

    @Test
    public void testCreateConnection() {
        accessMethod(bConService, "start", new Object[0]);
        accessMethod(bConService2, "connect", device);
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertEquals(3, accessMethod(bConService, "getState", new Object[0]));
        assertEquals(3, accessMethod(bConService2, "getState", new Object[0]));
    }

    @Test(expected = IOException.class)
    public void testFailCreateConnection() {
        accessMethod(bConService, "connect", new Object[0]);
        assertEquals(2, accessMethod(bConService, "getState", new Object[0]));
    }

    @Test
    public void testCreateConnectionWhenConnected() {
        accessMethod(bConService, "start", new Object[0]);
        accessMethod(bConService2, "connect", device);
        accessMethod(bConService2, "start", new Object[0]);
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertEquals(3, accessMethod(bConService, "getState", new Object[0]));
        assertEquals(3, accessMethod(bConService2, "getState", new Object[0]));
        assertEquals(0, accessMethod(bConService3, "getState", new Object[0]));
        accessMethod(bConService2, "stop", new Object[0]);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertEquals(0, accessMethod(bConService, "getState", new Object[0]));
        accessMethod(bConService, "connect", device);
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertEquals(3, accessMethod(bConService, "getState", new Object[0]));
        assertEquals(3, accessMethod(bConService3, "getState", new Object[0]));
    }
}
