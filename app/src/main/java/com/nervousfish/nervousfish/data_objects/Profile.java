package com.nervousfish.nervousfish.data_objects;

import com.nervousfish.nervousfish.modules.cryptography.EncryptedSaver;

/**
 * An Profile POJO to store a name, public key and private key.
 */
public class Profile {

    private final String name;
    private final String encryptedPassword;
    private final byte[] salt;
    private final KeyPair keyPair;

    /**
     * The constructor for the {@link Profile} class.
     *
     * @param name the name belonging to the Profile
     * @param keyPair the public/private key-pair
     */
    public Profile(final String name, final String password, final KeyPair keyPair) {
        this.name = name;
        this.salt = EncryptedSaver.generateSalt();
        this.encryptedPassword = EncryptedSaver.hashUsingSalt(salt, password);
        this.keyPair = keyPair;
    }

    /**
     * Get the name of the {@link Profile}.
     *
     * @return The name.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Get the {@link KeyPair} of the {@link Profile}.
     *
     * @return The {@link KeyPair}.
     */
    public KeyPair getKeyPair() { return this.keyPair; }

    /**
     * Get the public {@link IKey} of the {@link Profile}.
     *
     * @return The public {@link IKey}.
     */
    public IKey getPublicKey() {
        return this.keyPair.getPublicKey();
    }

    /**
     * Get the private {@link IKey} of the {@link Profile}.
     *
     * @return The private {@link IKey}.
     */
    public IKey getPrivateKey() {
        return this.keyPair.getPrivateKey();
    }

    /**
     * Get the password of the {@link Profile}
     * @return The encrypted password.
     */
    public String getEncryptedPassword() {
        return encryptedPassword;
    }

    /**
     * Get the salt bytestring of the {@link Profile}
     * @return The salt bytestring.
     */
    public byte[] getSalt() {
        return salt;
    }

    /**
     * Checks if a given string equals the password of the user.
     * @param password The string to check.
     * @return Whether or not the string is the user's password.
     */
    public boolean checkPassword(String password){
        String checkingPassword = EncryptedSaver.hashUsingSalt(salt, password);
        return checkingPassword.equals(encryptedPassword);
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
        return this.name.equals(that.name)
            && this.keyPair.equals(that.keyPair);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return this.name.hashCode() + this.keyPair.hashCode();
    }

}
