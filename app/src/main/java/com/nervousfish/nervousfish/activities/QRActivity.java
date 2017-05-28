package com.nervousfish.nervousfish.activities;

import android.app.Activity;
import android.os.Bundle;

import com.nervousfish.nervousfish.R;

/**
 * Used for pairing using QR codes
 */
public class QRActivity extends Activity {
    /**
     * Creates the new activity, should only be called by Android
     *
     * @param savedInstanceState The saved state of the instance.
     */
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr);
    }
}