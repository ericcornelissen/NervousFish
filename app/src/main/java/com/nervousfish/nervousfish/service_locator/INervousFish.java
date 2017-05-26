package com.nervousfish.nervousfish.service_locator;

import com.nervousfish.nervousfish.modules.pairing.AndroidBluetoothService;

/**
 * Defines the Nervous Fish {@link android.app.Application}
 */
public interface INervousFish {
    AndroidBluetoothService getBluetoothService();
    void setOnServiceBound(final Runnable runnable);
}
