package com.nervousfish.nervousfish.service_locator;

import com.nervousfish.nervousfish.modules.pairing.AndroidBluetoothService;
import com.nervousfish.nervousfish.modules.pairing.PairingWrapper;

/**
 * Defines the Nervous Fish {@link android.app.Application}
 */
interface INervousFish {
    /**
     * @return The Bluetooth Service
     */
    PairingWrapper<AndroidBluetoothService> getBluetoothService();

    /**
     * Sets a runnable that's called when the Bluetooth Service is bound to the application
     * @param runnable The runnable that should run
     */
    void setOnBluetoothServiceBound(Runnable runnable);
}
