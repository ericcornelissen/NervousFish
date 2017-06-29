package com.nervousfish.nervousfish.modules.pairing;

/**
 * Defines a class that can handle received data from other devices
 */
public interface IDataReceiver {

    /**
     * Should be called when new data is received
     *
     * @param bytes The new data that is received
     */
    void dataReceived(byte[] bytes);

}
