package com.nervousfish.nervousfish.modules.util;

import com.nervousfish.nervousfish.service_locator.IServiceLocator;
import com.nervousfish.nervousfish.service_locator.ModuleWrapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InvalidClassException;

/**
 * Contains both the Android-specific util function as inherited cross-platform utils functions.
 */
public final class AndroidUtils extends AUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger("EncryptorAdapter");

    /**
     * Prevents construction from outside the class.
     *
     * @param serviceLocator Can be used to get access to other modules
     */
    @SuppressWarnings("PMD.UnusedFormalParameter")
    // This servicelocator will be used later on probably
    private AndroidUtils(final IServiceLocator serviceLocator) {
        super(new ZXingQRUtils(serviceLocator));
        LOGGER.info("Initialized");
    }

    /**
     * Creates a new instance of itself and wraps it in a {@link ModuleWrapper} so that only an
     * {@link IServiceLocator}
     *
     * @param serviceLocator The new service locator
     * @return A wrapper around a newly created instance of this class
     */
    public static ModuleWrapper<AndroidUtils> newInstance(final IServiceLocator serviceLocator) {
        return new ModuleWrapper<>(new AndroidUtils(serviceLocator));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object encodeQR(final String publicKey) {
        return super.getQrUtils().encodeQR(publicKey);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String decodeQR(final Object qrCode) throws InvalidClassException {
        return super.getQrUtils().decodeQR(qrCode);
    }
}
