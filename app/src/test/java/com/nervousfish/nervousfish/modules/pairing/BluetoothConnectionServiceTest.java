package com.nervousfish.nervousfish.modules.pairing;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;

import com.nervousfish.nervousfish.modules.constants.IConstants;
import com.nervousfish.nervousfish.service_locator.IServiceLocator;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.UUID;

import static com.nervousfish.nervousfish.BaseTest.accessConstructor;
import static org.mockito.ArgumentMatchers.notNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by stasp on 11-5-2017.
 */
public class BluetoothConnectionServiceTest {

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
        when(serviceLocator.getConstants()).thenReturn(constants);
        try {
            when(device.createRfcommSocketToServiceRecord((UUID)notNull())).thenReturn(socket);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //when(accessMethod(bConService, "connectionFailed", new Object[0])).thenThrow(IOException.class); //this probably won't work

        this.bConService = (BluetoothConnectionService) accessConstructor(BluetoothConnectionService.class, new Handler(), mock(IServiceLocator.class), mock(BluetoothAdapter.class));
        //this.bConService2 = (BluetoothConnectionService) accessConstructor(BluetoothConnectionService.class, new Object[]{handler ,serviceLocator});
        //this.bConService3 = (BluetoothConnectionService) accessConstructor(BluetoothConnectionService.class, new Object[]{handler ,serviceLocator});

    }

    @After
    public void tearDown() {
        //accessMethod(bConService, "stop", new Object[0]);
        //accessMethod(bConService2, "stop", new Object[0]);
        //accessMethod(bConService3, "stop", new Object[0]);
    }


    @Test
    public void newInstance() throws Exception {
        accessConstructor(BluetoothConnectionService.class, new Handler(), mock(IServiceLocator.class), mock(BluetoothAdapter.class));
    }

    @Test
    public void start() throws Exception {
        bConService.start();
    }

    @Test
    public void connect() throws Exception {
        bConService.connect(mock(BluetoothDevice.class));
    }

    @Test
    public void connected() throws Exception {
        bConService.connect(mock(BluetoothDevice.class));
    }

    @Test
    public void stop() throws Exception {
        bConService.stop();
    }

    @Test
    public void write() throws Exception {

    }

    @Test
    public void showWarning() throws Exception {

    }

    @Test
    public void getState() throws Exception {

    }

}