package com.nervousfish.nervousfish.service_locator;


/**
 * Defines the interface of a class that can create a new {@link IServiceLocator}.
 */
public interface IServiceLocatorCreator {
    IServiceLocator getServiceLocator();
}