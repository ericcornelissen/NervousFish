package com.nervousfish.nervousfish.modules.pairing;

/**
 * Defines a thread that's used by an {@link IBluetoothHandlerService}
 */
public interface IBluetoothThread {

    /**
     * Starts the thread.
     */
    void start();

    /**
     * Cancels the connect thread and optionally closes the socket
     */
    void cancel();
}
