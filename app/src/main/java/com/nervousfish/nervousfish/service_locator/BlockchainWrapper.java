package com.nervousfish.nervousfish.service_locator;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.nervousfish.nervousfish.data_objects.Ed25519Key;
import com.nervousfish.nervousfish.data_objects.Profile;

import java.security.PrivateKey;
import java.security.PublicKey;

import nl.tudelft.ewi.ds.bankver.Blockchain;
import nl.tudelft.ewi.ds.bankver.IBAN;

/**
 * An implementation of the {@link nl.tudelft.ewi.ds.bankver.Blockchain} class.
 */

public class BlockchainWrapper implements Blockchain {

    final Profile profile;

    public BlockchainWrapper(final Profile profile) {
        this.profile = profile;
    }

    /**
     * {@inheritDoc}
     */
    @NonNull
    @Override
    public PrivateKey getPrivateKey() {
        return this.profile.getKeyPairs().;
    }

    /**
     * {@inheritDoc}
     */
    @NonNull
    @Override
    public PublicKey getPublicKey() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Nullable
    @Override
    public PublicKey getPublicKeyForIBAN(IBAN iban) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setIbanVerified(PublicKey publicKey, IBAN iban, String s) {

    }

    public Ed25519Key getFirstEd25519Key() {

    }
}
