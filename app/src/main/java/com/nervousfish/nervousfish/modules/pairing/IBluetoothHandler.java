package com.nervousfish.nervousfish.modules.pairing;

import android.bluetooth.BluetoothDevice;

import com.nervousfish.nervousfish.modules.IModule;

import java.io.IOException;

/**
 * Defines a module used for pairing over Bluetooth that can be used by a service locator.
 */

public interface IBluetoothHandler extends IBluetoothCommunicator, IPairingHandler {
    void writeAllContacts() throws IOException;

}
