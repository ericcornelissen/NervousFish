package com.nervousfish.nervousfish.modules.pairing.events;

/**
 * Greenrobot's EventBus message event
 * <p>
 * Sent when new bytes are received, decrypted by some key
 */
public final class NewDecryptedBytesReceivedEvent {
    private final byte[] bytes;

    /**
     * Constructs a new {@link NewDecryptedBytesReceivedEvent}
     *
     * @param bytes The decrypted byte array received
     */
    public NewDecryptedBytesReceivedEvent(final byte[] bytes) {
        this.bytes = bytes.clone();
    }

    /**
     * @return The decrypted bytes received
     */
    public byte[] getBytes() {
        return this.bytes.clone();
    }
}
