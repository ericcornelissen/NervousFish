package com.nervousfish.nervousfish.modules.constants;

import com.nervousfish.nervousfish.modules.IModule;

import java.util.UUID;

/**
 * Defines a class containing global constants for the application. These constants are definied in this file, because
 * they're used by multiple files and don't logically belong to one of them.
 */
public interface IConstants extends IModule {

    /**
     * Get the path to the database file for the contacts.
     *
     * @return A {@link String} of the path.
     */
    String getDatabaseContactsPath();

    /**
     * Get the path to the database file for the userdata.
     *
     * @return A {@link String} of the path.
     */
    String getDatabaseUserdataPath();

    /**
     * @return The unique UUID of this application
     */
    UUID getUuid();

    /**
     * @return The name of the SDP record used for the Bluetooth communication
     */
    String getSDPRecord();
}
