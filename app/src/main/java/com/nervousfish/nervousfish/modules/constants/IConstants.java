package com.nervousfish.nervousfish.modules.constants;

import com.nervousfish.nervousfish.modules.IModule;

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

    /**
     * Get the path to the file directory of the database.
     *
     * @return A {@link String} of the path.
     */
    String getFileDir();

    /**
     * Get the path to the file directory.
     *
     * @param FileDir the path to the file directory
     */
    void setFileDir(String FileDir);

}
