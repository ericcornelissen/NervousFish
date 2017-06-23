package com.nervousfish.nervousfish.service_locator;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.nervousfish.nervousfish.data_objects.Ed25519KeyPair;
import com.nervousfish.nervousfish.data_objects.Ed25519PrivateKeyWrapper;
import com.nervousfish.nervousfish.data_objects.Profile;

import net.i2p.crypto.eddsa.EdDSAPrivateKey;
import net.i2p.crypto.eddsa.spec.EdDSAPrivateKeySpec;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;

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
        return this.profile.getEd25519KeyPairs().get(0).getPrivateKey().;
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
}
