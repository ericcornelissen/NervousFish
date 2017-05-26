package com.nervousfish.nervousfish.events;

/**
 * Contains the byte buffer containing some object
 */
public class SerializedBufferReceivedEvent {
    final byte[] buffer;

    public SerializedBufferReceivedEvent(final byte[] buffer) {
        this.buffer = buffer;
    }

    public byte[] getBuffer() {
        return buffer;
    }
}
