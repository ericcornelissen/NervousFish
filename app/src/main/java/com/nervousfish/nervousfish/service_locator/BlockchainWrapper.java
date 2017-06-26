package com.nervousfish.nervousfish.service_locator;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.nervousfish.nervousfish.data_objects.Ed25519Key;
import com.nervousfish.nervousfish.data_objects.KeyPair;
import com.nervousfish.nervousfish.data_objects.Profile;

import java.security.PrivateKey;
import java.security.PublicKey;

import nl.tudelft.ewi.ds.bankver.Blockchain;
import nl.tudelft.ewi.ds.bankver.IBAN;

/**
 * Implements the Blockchain class.
 */

public class BlockchainWrapper implements Blockchain {

    final private Profile profile;

    public BlockchainWrapper(final Profile profile) {
        this.profile = profile;
    }

    @NonNull
    @Override
    public PrivateKey getPrivateKey() {
        final KeyPair keyPair = this.profile.getFirstEd25519KeyPair();
        return Ed25519Key.getPrivateKeyOf((Ed25519Key) keyPair.getPrivateKey());
    }

    @NonNull
    @Override
    public PublicKey getPublicKey() {
        final KeyPair keyPair = this.profile.getFirstEd25519KeyPair();
        return Ed25519Key.getPublicKeyOf((Ed25519Key) keyPair.getPublicKey());
    }

    @Nullable
    @Override
    public PublicKey getPublicKeyForIBAN(IBAN iban) {
        return null;
    }

    @Override
    public void setIbanVerified(PublicKey publicKey, IBAN iban, String s) {

    }
}
