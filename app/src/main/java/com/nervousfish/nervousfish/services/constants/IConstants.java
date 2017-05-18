package com.nervousfish.nervousfish.services.constants;

import com.nervousfish.nervousfish.services.IModule;

/**
 * Interface for a class containing all main constants of the application.
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

}
