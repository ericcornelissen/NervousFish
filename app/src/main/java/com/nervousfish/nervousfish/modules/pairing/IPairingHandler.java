package com.nervousfish.nervousfish.modules.pairing;

import com.nervousfish.nervousfish.modules.IModule;

import java.io.IOException;
import java.io.Serializable;

/**
 * Defines the interface implemented by all pairing handlers
 */
interface IPairingHandler extends IModule, Serializable {

    /**
     * Serializes an object and sends it by using the subclass specific method
     *
     * @param object The object to serialize
     * @throws IOException When deserialization goes wrong
     */
    void send(Serializable object) throws IOException;

    /**
     * Write the buffer to the world
     *
     * @param buffer The bytes to send
     */
    void send(byte[] buffer);

    /**
     * @return The object responsible for newly received data
     */
    ReceiverWrapper getDataReceiver();
}
