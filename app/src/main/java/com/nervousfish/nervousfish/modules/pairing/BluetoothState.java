package com.nervousfish.nervousfish.modules.pairing;

/**
 * A type safe enumeration to denote the current state of the bluetooth connection
 */
enum BluetoothState {
    STATE_NONE, STATE_LISTEN, STATE_CONNECTING, STATE_CONNECTED
}