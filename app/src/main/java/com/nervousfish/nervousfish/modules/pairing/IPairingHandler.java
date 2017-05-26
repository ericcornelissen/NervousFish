package com.nervousfish.nervousfish.modules.pairing;

import com.nervousfish.nervousfish.modules.IModule;

import java.io.IOException;

/**
 * Defines the interface implemented by all pairing handlers
 */
public interface IPairingHandler extends IModule {

    /**
     * Luxury method that calls sendContact() for each contact of the database.
     *
     * @throws IOException When deserialization doesn't go well.
     */
    void sendAllContacts() throws IOException;
}
