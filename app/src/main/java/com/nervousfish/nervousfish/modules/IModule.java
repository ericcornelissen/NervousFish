package com.nervousfish.nervousfish.modules;

import java.io.Serializable;

/**
 * Defines a module that can be used by an {@link com.nervousfish.nervousfish.service_locator.IServiceLocator}
 * A module provides a single service to the application and should be registered to the ServiceLocator
 * before use.
 */

public interface IModule extends Serializable {
}
