package com.nervousfish.nervousfish.service_locator;


import com.nervousfish.nervousfish.modules.IModule;

import java.io.Serializable;

/**
 * Defines the interface of a class that can create a new {@link IServiceLocator}.
 */
public interface IServiceLocatorCreator extends Serializable {
    IServiceLocator getServiceLocator();
    void registerToEventBus(final Object object);
}