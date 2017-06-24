package com.nervousfish.nervousfish.modules.cryptography;

import com.nervousfish.nervousfish.data_objects.AKeyPair;
import com.nervousfish.nervousfish.data_objects.Ed25519KeyPair;
import com.nervousfish.nervousfish.data_objects.RSAKeyPair;
import com.nervousfish.nervousfish.modules.IModule;

/**
 * Defines a module for generating secure keys that can be used by a service locator.
 */

public interface IKeyGenerator extends IModule {

    /**
     * Generates a random {@link AKeyPair} with the RSA algorithm.
     *
     * @param name The name of the newly generated key
     * @return a randomly generated AKeyPair
     */
    RSAKeyPair generateRSAKeyPair(String name) throws KeyGenerationException;

    /**
     * Generates a random {@link AKeyPair} with the Ed25519 algorithm. For more
     * details about Ed25519: https://ed25519.cr.yp.to/
     *
     * @param name The name of the newly generated key
     * @return a randomly generated AKeyPair
     */
    Ed25519KeyPair generateEd25519KeyPair(String name) throws KeyGenerationException;

}
