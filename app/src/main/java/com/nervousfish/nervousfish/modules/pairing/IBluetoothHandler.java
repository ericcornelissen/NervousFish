package com.nervousfish.nervousfish.modules.pairing;

import java.io.IOException;

/**
 * Defines a module used for pairing over Bluetooth that can be used by a service locator.
 */

public interface IBluetoothHandler extends IBluetoothCommunicator, IPairingHandler {

    /**
     * Send all contacts over Bluetooth to the paired device
     * @throws IOException Thrown when something went wrong while sending the data
     */
    void sendAllContacts() throws IOException;

}
