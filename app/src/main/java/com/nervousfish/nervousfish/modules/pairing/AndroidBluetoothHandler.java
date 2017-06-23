package com.nervousfish.nervousfish.modules.pairing;

import android.bluetooth.BluetoothDevice;

import com.nervousfish.nervousfish.modules.pairing.events.NewDecryptedBytesReceivedEvent;
import com.nervousfish.nervousfish.service_locator.IServiceLocator;
import com.nervousfish.nervousfish.service_locator.ModuleWrapper;
import com.nervousfish.nervousfish.service_locator.NervousFish;

import org.apache.commons.lang3.Validate;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;

/**
 * Helper class for {@link AndroidBluetoothService} and acts as the bridge between the client and
 * the service. It simplifies the whole Bluetooth functionality to the methods start, connect, stop and send
 */
// A logical consequence of using an EventBus. No problem, because it are just (empty) POJO's.
@SuppressWarnings("checkstyle:classdataabstractioncoupling")
public final class AndroidBluetoothHandler extends APairingHandler implements IBluetoothHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger("AndroidBluetoothHandler");

    /**
     * Prevents construction from outside the class.
     *
     * @param serviceLocator Can be used to get access to other modules
     */
    private AndroidBluetoothHandler(final IServiceLocator serviceLocator) {
        super(serviceLocator);
    }

    /**
     * Creates a new instance of itself and wraps it in a {@link ModuleWrapper} so that only an
     * {@link IServiceLocator}
     *
     * @param serviceLocator The new service locator
     * @return A wrapper around a newly created instance of this class
     */
    public static ModuleWrapper<AndroidBluetoothHandler> newInstance(final IServiceLocator serviceLocator) {
        Validate.notNull(serviceLocator);
        return new ModuleWrapper<>(new AndroidBluetoothHandler(serviceLocator));
    }

    private static IBluetoothHandlerService getService() {
        return ((NervousFish) NervousFish.getInstance()).getBluetoothService().get();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void start() throws IOException {
        getService().start();
        if (!this.getServiceLocator().isRegisteredToEventBus(this)) {
            this.getServiceLocator().registerToEventBus(this);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void connect(final BluetoothDevice device) {
        getService().connect(device);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void restart() throws IOException {
        this.stop();
        this.start();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void stop() {
        getService().stop();
        this.getServiceLocator().unregisterFromEventBus(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void send(final byte[] buffer) {
        Validate.isTrue(buffer.length > 0);
        getService().write(buffer);
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Bytes written: {}", Arrays.toString(buffer));
        }
    }

    /**
     * Called by Greenrobot's Eventbus whenever a received byte array is decrypted
     *
     * @param event Contains the decrypted bytes
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNewDecryptedBytesReceivedEvent(final NewDecryptedBytesReceivedEvent event) {
        LOGGER.info("onNewDecryptedBytesReceivedEvent received");
        this.getDataReceiver().get().dataReceived(event.getBytes());
    }

}