package com.nervousfish.nervousfish.service_locator;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.nervousfish.nervousfish.data_objects.Ed25519KeyPair;
import com.nervousfish.nervousfish.data_objects.Ed25519PrivateKeyWrapper;
import com.nervousfish.nervousfish.data_objects.Profile;

import net.i2p.crypto.eddsa.EdDSAPrivateKey;
import net.i2p.crypto.eddsa.spec.EdDSAPrivateKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;

import nl.tudelft.ewi.ds.bankver.Blockchain;
import nl.tudelft.ewi.ds.bankver.IBAN;

/**
 * An implementation of the {@link nl.tudelft.ewi.ds.bankver.Blockchain} class.
 */

public class BlockchainWrapper implements Blockchain {

    private static final Logger LOGGER = LoggerFactory.getLogger("BlockchainWrapper");
    private final Profile profile;
    private final IServiceLocator serviceLocator;

    public BlockchainWrapper(final Profile profile) {
        this.profile = profile;
        this.serviceLocator = NervousFish.getServiceLocator();
    }

    /**
     * {@inheritDoc}
     */
    @NonNull
    @Override
    public PrivateKey getPrivateKey() {
        return this.profile.getEd25519KeyPairs().get(0).getPrivateKey().getKey();
    }

    /**
     * {@inheritDoc}
     */
    @NonNull
    @Override
    public PublicKey getPublicKey() {
        return this.profile.getEd25519KeyPairs().get(0).getPublicKey().getKey();
    }

    /**
     * {@inheritDoc}
     */
    @Nullable
    @Override
    public PublicKey getPublicKeyForIBAN(final IBAN iban) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setIbanVerified(final PublicKey publicKey, final IBAN iban, final String s) {
        try {
            this.serviceLocator.getDatabase().setIbanVerified(iban);
        } catch (IOException e) {
            LOGGER.error("Could not set iban verified", e);
        }
    }
}
