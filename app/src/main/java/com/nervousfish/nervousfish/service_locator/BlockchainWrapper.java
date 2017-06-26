package com.nervousfish.nervousfish.service_locator;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.nervousfish.nervousfish.data_objects.Contact;
import com.nervousfish.nervousfish.data_objects.Ed25519Key;
import com.nervousfish.nervousfish.data_objects.KeyPair;
import com.nervousfish.nervousfish.data_objects.Profile;
import com.nervousfish.nervousfish.modules.database.IDatabase;

import java.security.PrivateKey;
import java.security.PublicKey;

import nl.tudelft.ewi.ds.bankver.Blockchain;
import nl.tudelft.ewi.ds.bankver.IBAN;

/**
 * Implements the Blockchain class.
 */

public class BlockchainWrapper implements Blockchain {

    final private Profile profile;
    final private IDatabase database;

    public BlockchainWrapper(final Profile profile) {
        this.profile = profile;
        this.database = NervousFish.getServiceLocator().getDatabase();
    }

    /**
     * Gets the Ed25519 private key of the profile.
     *
     * @return The Ed25519 private key of the profile.
     */
    @NonNull
    @Override
    public PrivateKey getPrivateKey() {
        final KeyPair keyPair = this.profile.getFirstEd25519KeyPair();
        return Ed25519Key.getPrivateKeyOf((Ed25519Key) keyPair.getPrivateKey());
    }

    /**
     * Gets the Ed25519 public key of the profile.
     *
     * @return The Ed25519 public key of the profile.
     */
    @NonNull
    @Override
    public PublicKey getPublicKey() {
        final KeyPair keyPair = this.profile.getFirstEd25519KeyPair();
        return Ed25519Key.getPublicKeyOf((Ed25519Key) keyPair.getPublicKey());
    }

    /**
     * Gets the Public key of the person corresponding to the given IBAN.
     *
     * @param iban The iban of the contact.
     * @return The Ed25519 public key of the contact.
     */
    @Nullable
    @Override
    public PublicKey getPublicKeyForIBAN(final IBAN iban) {
        final Contact contact = this.database.getContactWithIBAN(iban);
        return Ed25519Key.getPublicKeyOf((Ed25519Key) contact.getFirstEd25519Key());
    }

    @Override
    public void setIbanVerified(final PublicKey publicKey, final IBAN iban, final String s) {

    }
}
