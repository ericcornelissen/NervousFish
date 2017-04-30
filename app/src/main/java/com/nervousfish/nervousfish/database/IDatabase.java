package com.nervousfish.nervousfish.database;

import java.security.KeyPair;

public interface IDatabase {

    boolean hasKeyPair();
    KeyPair readKeyPair();
}
