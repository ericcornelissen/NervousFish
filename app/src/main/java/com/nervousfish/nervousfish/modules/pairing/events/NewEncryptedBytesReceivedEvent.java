package com.nervousfish.nervousfish.modules.pairing.events;

/**
 * Greenrobot's EventBus message event
 * <p>
 * Sent when new bytes are received, encrypted by some key
 */
public final class NewEncryptedBytesReceivedEvent {
    private final byte[] bytes;

    /**
     * Constructs a new {@link NewEncryptedBytesReceivedEvent}
     *
     * @param bytes The encrypted byte array received
     */
    public NewEncryptedBytesReceivedEvent(final byte[] bytes) {
        this.bytes = bytes.clone();
    }

    /**
     * @return The encrypted bytes received
     */
    public byte[] getBytes() {
        return this.bytes.clone();
    }
}
