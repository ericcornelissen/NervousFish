package com.nervousfish.nervousfish.modules.pairing;

/**
 * Wraps an object that can process the data received from other devices so that only classes within
 * the pairing package can "receive" data
 */
final class ReceiverWrapper {
    private final IDataReceiver dataReceiver;

    /**
     * Constructs a new object that wraps an {@link IDataReceiver}
     * @param dataReceiver A class that can process data received by other devices
     */
    ReceiverWrapper(final IDataReceiver dataReceiver) {
        this.dataReceiver = dataReceiver;
    }

    /**
     * @return The object responsible for processing the data received from other devices
     */
    IDataReceiver get() {
        return this.dataReceiver;
    }
}
