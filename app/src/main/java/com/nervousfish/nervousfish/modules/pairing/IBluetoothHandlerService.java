package com.nervousfish.nervousfish.modules.pairing;

/**
 * Defines the interface for the Bluetooth service.
 */
interface IBluetoothHandlerService extends IBluetoothCommunicator {

    /**
     * Write to the AndroidBluetoothConnectedThread in an asynchronous manner
     *
     * @param output The bytes to write. Must be at least a single byte
     * @see AndroidBluetoothConnectedThread#write(byte[])
     */
    void write(byte[] output);

}
