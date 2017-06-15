package com.nervousfish.nervousfish.modules.pairing.events;

import org.apache.commons.lang3.Validate;

/**
 * Greenrobot's EventBus message event
 * <p>
 * Sent when a new data object is received
 */
public final class NewDataReceivedEvent {
    private final Object data;
    private final Class<?> clazz;

    /**
     * Constructs a new {@link NewDataReceivedEvent}
     *
     * @param data  The data received
     * @param clazz The class of the data received
     */
    public NewDataReceivedEvent(final Object data, final Class<?> clazz) {
        Validate.notNull(data);
        Validate.notNull(clazz);
        this.data = data;
        this.clazz = clazz;
    }

    /**
     * @return The data received
     */
    public Object getData() {
        return this.data;
    }

    /**
     * @return The class of the data received
     */
    public Class<?> getClazz() {
        return this.clazz;
    }
}
