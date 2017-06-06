package com.nervousfish.nervousfish.data_objects;

import com.nervousfish.nervousfish.modules.cryptography.EncryptedSaver;

import java.util.List;

/**
 * An Profile POJO to store a name, public key and private key.
 */
public class Profile {

    private final Contact contact;
    private final List<KeyPair> keyPairs;

    /**
     * The constructor for the {@link Profile} class.
     *
     * @param contact The contact belonging to the user.
     * @param keyPairs the public/private key-pairs of the user.
     */
    public Profile(final Contact contact, final List<KeyPair> keyPairs) {
        this.contact = contact;
        this.keyPairs = keyPairs;
    }

    /**
     * Returns the contact Object of the user
     * @return - The Contact POJO.
     */
    public Contact getContact() {
        return contact;
    }

    /**
     * Returns the Keypairs of the users.
     * @return
     */
    public List<KeyPair> getKeyPairs() {
        return keyPairs;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object o) {
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }

        final Profile that = (Profile) o;
        if (this.keyPairs != that.keyPairs) {
            return false;
        }
        for (KeyPair kp : this.keyPairs) {
            for (KeyPair kp2 : that.keyPairs) {
                if(!kp.equals(kp2)) {
                    return false;
                }
            }
        }
        return this.contact.equals(that.contact);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return this.contact.hashCode() + this.keyPairs.hashCode();
    }

}
