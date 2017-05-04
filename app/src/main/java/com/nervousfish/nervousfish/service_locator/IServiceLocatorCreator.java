package com.nervousfish.nervousfish.service_locator;


import java.io.Serializable;

/**
 * Defines the interface of a class that can create a new {@link IServiceLocator}.
 */
public interface IServiceLocatorCreator extends Serializable {
    IServiceLocator getServiceLocator();
}