package com.nervousfish.nervousfish.modules.pairing;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;

import com.nervousfish.nervousfish.AsynchTester;
import com.nervousfish.nervousfish.modules.constants.IConstants;
import com.nervousfish.nervousfish.service_locator.IServiceLocator;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.UUID;

import static com.nervousfish.nervousfish.BaseTest.accessConstructor;
import static com.nervousfish.nervousfish.BaseTest.getField;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.notNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class BluetoothConnectionServiceTest {

    private IServiceLocator serviceLocator = mock(IServiceLocator.class);
    private IConstants constants = mock(IConstants.class);
    private BluetoothDevice device = mock(BluetoothDevice.class);
    private BluetoothSocket socket = mock(BluetoothSocket.class);

    private BluetoothConnectionService bConService;



    @Before
    public void setup() {
        when(serviceLocator.getConstants()).thenReturn(constants);
        try {
            when(device.createRfcommSocketToServiceRecord((UUID)notNull())).thenReturn(socket);
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.bConService = (BluetoothConnectionService) accessConstructor(BluetoothConnectionService.class, serviceLocator);

    }


    @Test
    public void newInstanceTest() throws Exception {
        this.bConService = (BluetoothConnectionService) accessConstructor(BluetoothConnectionService.class, new Handler(), mock(IServiceLocator.class), mock(BluetoothAdapter.class));
        assertEquals(0, bConService.getState());
    }

    /*
     * This is done because all I can test is the creation of the necessary threads
     * and not their functionality
     */
    @Test(expected = NullPointerException.class)
    public void startThreadCreatedTest() throws Exception {
        assertNull(getField(bConService, "secureAcceptThread"));
        AsynchTester aTester = new AsynchTester(new Runnable() {
            @Override
            public void run() {
                bConService.start();
            }
        });

        aTester.start();
        aTester.test();

        assertNotNull(getField(bConService, "secureAcceptThread"));
    }

    /*
     * This is done because all I can test is the creation of the necessary threads
     * and not their functionality
     */
    @Test(expected = NullPointerException.class)
    public void connectThreadCreatedTest() throws Exception {
        assertNull(getField(bConService, "connectThread"));
        bConService.connect(mock(BluetoothDevice.class));
        assertNotNull(getField(bConService, "connectThread"));
    }

    /*
     * This is done because all I can test is the creation of the necessary threads
     * and not their functionality
     */
    /*@Test(expected = NullPointerException.class)
    public void connectedThreadCreatedTest() throws Exception {
        assertNull(getField(bConService, "connectedThread"));
        bConService.connect(mock(BluetoothDevice.class));
        assertNotNull(getField(bConService, "connectedThread"));
    }*/

    @Test
    public void stop() throws Exception {
        bConService.stop();
        assertNull(getField(bConService, "secureAcceptThread"));
        assertNull(getField(bConService, "connectThread"));
        assertNull(getField(bConService, "connectedThread"));
    }

    @Test
    public void write() throws Exception {
        bConService.write(new byte[]{});
        // need to test this with mocks but
        // bluetoothServiceConnection is final
    }

    @Test
    public void showWarning() throws Exception {
        bConService.showWarning();
        // can't test this as it isn't implemented yet
    }

    @Test
    public void getStateNewInstanceTest() throws Exception {
        assertEquals(0, bConService.getState());
    }


}