package com.nervousfish.nervousfish.util.commands;

import com.nervousfish.nervousfish.service_locator.IServiceLocator;

/**
 * Created by jverb on 4/30/2017.
 */

public interface IServiceLocatorCreatedCommand {
    void execute(IServiceLocator serviceLocator);
}
