package com.nervousfish.nervousfish.exceptions;

/**
 * Simple {@link RuntimeException} that is used to notify usage of Bluetooth is not avaiable.
 */
public class NoBluetoothException extends RuntimeException {

    private static final long serialVersionUID = -7030163248191198652L;

    /**
     * Constructor for a new NoBluetoothException with a message.
     *
     * @param msg A string describing the exception
     */
    public NoBluetoothException(final String msg) {
        super(msg);
    }

}
