package com.nervousfish.nervousfish.modules;

import com.nervousfish.nervousfish.events.SLReadyEvent;

import java.io.Serializable;

/**
 * Defines a module that can be used by an {@link com.nervousfish.nervousfish.service_locator.IServiceLocator}
 */

public interface IModule extends Serializable {
    /**
     * Called when the service locator is ready to be used
     * @param event Indicates that the {@link SLReadyEvent} happened
     */
    void onSLReadyEvent(final SLReadyEvent event);
}
