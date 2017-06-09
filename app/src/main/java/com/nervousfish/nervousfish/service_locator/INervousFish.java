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

}
