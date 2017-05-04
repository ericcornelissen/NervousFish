package com.nervousfish.nervousfish.modules.database;

import com.nervousfish.nervousfish.modules.IModule;

/**
 * Defines a module defining the actions that can be executed on the database that can be used by a service locator.
 */

public interface IDatabase extends IModule {
    void initializeDatabaseFiles();
}
