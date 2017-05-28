package com.nervousfish.nervousfish.modules.pairing;

/**
 * Wraps a module and provides a package-private method to retrieve it so that only the service_locator classes can access the module.
 * @param <T> The type of the Module that the ModuleWrapper is wrapping
 */
public final class PairingWrapper<T> {
    private final T module;

    /**
     * Creates a class that wraps a module so that only the classes in the same package as this class
     * i.e. the service locators can access the newly created module.
     * This is done to make sure that classes cannot obtain direct references to modules.
     *
     * @param module The module the wrapper should wrap
     */
    public PairingWrapper(final T module) {
        this.module = module;
    }

    T get() {
        return this.module;
    }
}

