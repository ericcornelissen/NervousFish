package com.nervousfish.nervousfish.modules.pairing;

/**
 * Defines the interface for the Bluetooth service.
 */
interface IBluetoothHandlerService extends IBluetoothCommunicator {
    /**
     * Write to the AndroidBluetoothConnectedThread in an unsynchronized manner
     *
     * @param output The bytes to write
     * @see AndroidBluetoothConnectedThread#write(byte[])
     */
    void write(byte[] output);
}
