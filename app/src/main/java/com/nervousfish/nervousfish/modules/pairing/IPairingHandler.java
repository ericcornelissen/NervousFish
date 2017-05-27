package com.nervousfish.nervousfish.modules.pairing;

import com.nervousfish.nervousfish.modules.IModule;

import java.io.Serializable;

/**
 * Defines the interface implemented by all pairing handlers
 */
interface IPairingHandler extends IModule, Serializable {

    /**
     * Write the buffer to the world
     *
     * @param buffer The bytes to send
     */
    void send(final byte[] buffer);

    ReceiverWrapper getDataReceiver();
}
