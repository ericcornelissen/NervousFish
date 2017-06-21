package com.nervousfish.nervousfish.modules.pairing;

import com.nervousfish.nervousfish.modules.IModule;

import java.io.IOException;
import java.io.Serializable;

/**
 * Defines the interface implemented by all pairing handlers
 */
interface IPairingHandler extends IModule {

    /**
     * Serializes an object and returns it's bytes by using the subclass specific method
     *
     * @param object The object to serialize
     * @return the byte representation of the {@code object}
     * @throws IOException When deserialization goes wrong
     */
    byte[] objectToBytes(Serializable object) throws IOException;

    /**
     * Serializes an object, encrypts it with a certain key and sends it by using the subclass specific method
     *
     * @param object The object to serialize
     * @param key    The key to encrypt the message with. Must be positive
     * @throws IOException When deserialization goes wrong
     */
    void send(Serializable object, long key) throws IOException;

    /**
     * Write the buffer to the world
     *
     * @param buffer The bytes to send. Must be at least a single byte
     */
    void send(byte[] buffer);

    /**
     * Serializes an object and write's it to an outputstream according to the send with bytes method
     *
     * @param object The object to serialize
     * @throws IOException When deserialization goes wrong
     */
    void send(Serializable object) throws IOException;

    /**
     * @return The object responsible for newly received data
     */
    PairingWrapper getDataReceiver();
}
