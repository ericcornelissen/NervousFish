package com.nervousfish.nervousfish;

/**
 * Contains the keywords used in the application. These should only be used for constants for which the
 * value is irrelevant, for example for passing information between activities.
 */
public final class ConstantKeywords {

    public static final String SERVICE_LOCATOR = "Service Locator";
    public static final String SECURITY_CODE = "Security Code";
    public static final String SIMPLE_KEY = "simple";
    public static final String RSA_KEY = "RSA";
    public static final String CONTACT = "contact";
    public static final String PROXY_REQUIRED = "Proxy required.";
    public static final String WAIT_MESSAGE = "Wait message";
    public static final String DATA_RECEIVED = "Data received";
    public static final String TAP_DATA = "Tap data";

    public static final String SUCCESSFUL_BLUETOOTH = "Successful Bluetooth pairing";

    public static final int CANCEL_PAIRING_RESULT_CODE = 44;
    public static final int DONE_PAIRING_RESULT_CODE = 55;
    public static final int START_RHYTHM_REQUEST_CODE = 66;

    /**
     * Prevents instantiation from outside the class
     */
    private ConstantKeywords() {
        // Unused, just prevents instantiation from outside the class
    }

}
