package com.nervousfish.nervousfish;

/**
 * Contains the keywords used in the application. These should only be used for constants for which the
 * value is irrelevant, for example for passing information between activities.
 */
public final class ConstantKeywords {

    public static final String ED25519_KEY = "ed25519";
    public static final String RSA_KEY = "RSA";
    public static final String CONTACT = "contact";
    public static final String PROXY_REQUIRED = "Proxy required.";
    public static final String WAIT_MESSAGE = "Wait message";
    public static final String DATA_RECEIVED = "Data received";
    public static final String KEY = "Key";
    public static final String CHOOSE_VERIFICATION_PREFERENCE = "choose_verification_method_every_time";
    public static final String DISPLAY_NAME = "display_name";
    public static final String RHYTHM_FAILURE = "Rhythm failure";

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
