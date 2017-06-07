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
     * Serializes an object, encrypts it with a certain key and sends it by using the subclass specific method
     *
     * @param object The object to serialize
     * @param key    The key to encrypt the message with
     * @throws IOException When deserialization goes wrong
     */
    void send(Serializable object, int key) throws IOException;

    /**
     * Write the buffer to the world
     *
     * @param buffer The bytes to send
     */
    void send(byte[] buffer);

    /**
     * @return The object responsible for newly received data
     */
    PairingWrapper getDataReceiver();
}
