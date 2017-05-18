package com.nervousfish.nervousfish.events;

/**
 * Greenrobot's EventBus message event
 *
 * Sent when the a new public key is received
 */
public class PublicKeyReceivedEvent {
    private final String key;

    /**
     * Constructs a new PublicKeyReceivedEvent
     * @param key The key of the new device
     */
    public PublicKeyReceivedEvent(final String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
