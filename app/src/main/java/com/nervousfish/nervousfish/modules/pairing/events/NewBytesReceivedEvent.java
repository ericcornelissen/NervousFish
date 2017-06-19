package com.nervousfish.nervousfish.modules.pairing.events;

/**
 * Greenrobot's EventBus message event
 * <p>
 * Sent when new bytes are received
 */
public final class NewBytesReceivedEvent {
    private final byte[] bytes;

    /**
     * Constructs a new {@link NewBytesReceivedEvent}
     *
     * @param bytes The byte array received
     */
    public NewBytesReceivedEvent(final byte[] bytes) {
        this.bytes = bytes;
    }

    /**
     * @return The bytes received
     */
    public byte[] getData() {
        return this.data;
    }
}
