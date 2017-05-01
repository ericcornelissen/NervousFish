package com.nervousfish.nervousfish.service_locator;

import com.nervousfish.nervousfish.modules.IModule;
import com.nervousfish.nervousfish.modules.constants.IConstants;

/**
 * Created by jverb on 5/1/2017.
 */

public final class ModuleWrapper<T extends IModule> {
    private final T module;

    public ModuleWrapper(final T module) {
        this.module = module;
    }

    T get() {
        return this.module;
    }

}
