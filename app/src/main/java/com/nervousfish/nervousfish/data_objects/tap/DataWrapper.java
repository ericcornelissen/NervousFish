package com.nervousfish.nervousfish.data_objects.tap;

import java.io.Serializable;

/**
 * This class is used to let the other party know what kind of class it contains.
 */
public class DataWrapper implements Serializable {
    private final ATapData tapData;
    private final Class clazz;

    /**
     * Creates a new DataWrapper
     *
     * @param tapData The {@link ATapData} object the wrapper wraps
     */
    public DataWrapper(ATapData tapData) {
        this.tapData = tapData;
        this.clazz = tapData.getClass();
    }

    /**
     * @return The tapData object it wraps
     */
    public ATapData getTapData() {
        return tapData;
    }

    /**
     * @return The class of the thing the class wraps
     */
    public Class getClazz() {
        return clazz;
    }
}
