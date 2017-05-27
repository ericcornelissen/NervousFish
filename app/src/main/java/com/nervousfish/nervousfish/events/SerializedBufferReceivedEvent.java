package com.nervousfish.nervousfish.events;

/**
 * Contains the byte buffer containing some object
 */
public final class SerializedBufferReceivedEvent {
    private final byte[] buffer;

    /**
     * Cosntructs a new event that a buffer of serialized data is received
     * @param buffer The buffer with serialized data
     */
    public SerializedBufferReceivedEvent(final byte[] buffer) {
        this.buffer = buffer;
    }

    /**
     * @return The buffer with serialized data
     */
    public byte[] getBuffer() {
        return buffer;
    }
}
