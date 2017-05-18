package com.nervousfish.nervousfish.service_locator;

import com.nervousfish.nervousfish.services.IModule;

/**
 * Wraps a module and provides a package-private method to retrieve it so that only the service_locator classes can access the module.
 */
public final class ModuleWrapper<T extends IModule> {
    private final T module;

    public ModuleWrapper(final T module) {
        this.module = module;
    }

    /* package */ T get() {
        return this.module;
    }

}
