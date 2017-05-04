package com.nervousfish.nervousfish.modules.filesystem;

import com.nervousfish.nervousfish.events.SLReadyEvent;
import com.nervousfish.nervousfish.service_locator.IServiceLocator;
import com.nervousfish.nervousfish.service_locator.IServiceLocatorCreator;
import com.nervousfish.nervousfish.service_locator.ModuleWrapper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * An adapter to the default Android file system
 */
public final class AndroidFileSystemAdapter implements IFileSystem {
    final IServiceLocatorCreator serviceLocatorCreator;

    /**
     * Prevents construction from outside the class.
     * @param serviceLocatorCreator The object responsible for creating the service locator
     */
    @SuppressWarnings("PMD.UnusedFormalParameter")
    private AndroidFileSystemAdapter(final IServiceLocatorCreator serviceLocatorCreator) {
        this.serviceLocatorCreator = serviceLocatorCreator;
        EventBus.getDefault().register(this);
    }

    /**
     * Creates a new instance of itself and wraps it in a {@link ModuleWrapper} so that only an {@link IServiceLocatorCreator}
     * can access the new module to create the new {@link IServiceLocator}.
     *
     * @param serviceLocatorCreator The service locator bridge that creates the new service locator
     * @return A wrapper around a newly created instance of this class
     */
    public static ModuleWrapper<AndroidFileSystemAdapter> newInstance(final IServiceLocatorCreator serviceLocatorCreator) {
        return new ModuleWrapper<>(new AndroidFileSystemAdapter(serviceLocatorCreator));
    }

    /**
     * {@inheritDoc}
     * @param event Indicates that the {@link SLReadyEvent} happened
     */
    @Subscribe
    @Override
    public void onSLReadyEvent(final SLReadyEvent event) {

    }
}
