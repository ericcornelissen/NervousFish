package com.nervousfish.nervousfish.modules.util;

/**
 * This class should be extended by all platform-specific {@link IUtils} implementations and contains
 * all cross-platform functionality
 */
@SuppressWarnings("PMD.AbstractClassWithoutAnyMethod")
abstract class AUtils implements IUtils {
    private static final long serialVersionUID = -2583470662725272558L;
    private final IQRUtils qrUtils;

    /**
     * Constructs the new abstract class for utilization functions
     * @param qrUtils The module used for QR related stuff
     */
    AUtils(final IQRUtils qrUtils) {
        this.qrUtils = qrUtils;
    }

    IQRUtils getQrUtils() {
        return qrUtils;
    }
}
