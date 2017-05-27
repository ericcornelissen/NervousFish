package com.nervousfish.nervousfish.modules.pairing;

/**
 * Defines a class that can handle received data from other devices
 */
interface IDataReceiver {
    void dataReceived(final byte[] bytes);
}
