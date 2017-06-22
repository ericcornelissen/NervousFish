package com.nervousfish.nervousfish.modules.pairing;

import com.nervousfish.nervousfish.modules.IModule;

/**
 * Defines a module used for pairing using NFC that can be used by a service locator.
 */
public interface INfcHandler extends IPairingHandler, IModule {

    /**
     * Makes sure that the data received is handled properly
     *
     * @param bytes the bytes received
     */
    void dataReceived(byte[] bytes);

}
