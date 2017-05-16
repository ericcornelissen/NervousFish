package com.nervousfish.nervousfish.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.nervousfish.nervousfish.ConstantKeywords;
import com.nervousfish.nervousfish.R;
import com.nervousfish.nervousfish.service_locator.IServiceLocator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Example activity to verify identity in Bluetooth connections.
 */
public class VisualVerificationActivity extends Activity {

    private static final Logger LOGGER = LoggerFactory.getLogger("VisualVerificationActivity");
    private static final int SECURITY_CODE_LENGTH = 5;

    private IServiceLocator serviceLocator;
    private String securityCode = "";

    /**
     * Stuff that needs to be done when the new activity being created.
     *
     * @param savedInstanceState The saved state
     */
    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.grid);

        final Intent intent = this.getIntent();
        this.serviceLocator = (IServiceLocator) intent.getSerializableExtra(ConstantKeywords.SERVICE_LOCATOR);

        this.initButtons();

        LOGGER.info("VisualVerificationActivity created");
    }

    private void nextActivity() {
        // TODO: Progress to the correct activity
        final Intent intent = new Intent(this, LoginActivity.class);
        intent.putExtra(ConstantKeywords.SECURITY_CODE, this.securityCode);
        intent.putExtra(ConstantKeywords.SERVICE_LOCATOR, this.serviceLocator);
        this.startActivity(intent);
    }

    /**
     * Initialize all the buttons.
     */
    private void initButtons() {
        final View.OnClickListener onClickListener = new View.OnClickListener() {

            /**
             * {@inheritDoc}
             */
            @Override
            public void onClick(final View v) {
                final String button = v.getContentDescription().toString();
                LOGGER.info("button '" + button + "' clicked");
                Log.d("tst", "button " + button + " clicked");

                if (securityCode.length() > VisualVerificationActivity.SECURITY_CODE_LENGTH) {
                    LOGGER.info("Security code already long enough");
                } else if (securityCode.length() + 1 == VisualVerificationActivity.SECURITY_CODE_LENGTH) {
                    securityCode += button;
                    LOGGER.info("final code is: " + securityCode);
                    nextActivity();
                } else {
                    securityCode += button;
                    LOGGER.info("code so far: " + securityCode);
                }
            }

        };

        this.findViewById(R.id.button00).setOnClickListener(onClickListener);
        this.findViewById(R.id.button01).setOnClickListener(onClickListener);
        this.findViewById(R.id.button02).setOnClickListener(onClickListener);
        this.findViewById(R.id.button03).setOnClickListener(onClickListener);
        this.findViewById(R.id.button10).setOnClickListener(onClickListener);
        this.findViewById(R.id.button11).setOnClickListener(onClickListener);
        this.findViewById(R.id.button12).setOnClickListener(onClickListener);
        this.findViewById(R.id.button13).setOnClickListener(onClickListener);
        this.findViewById(R.id.button20).setOnClickListener(onClickListener);
        this.findViewById(R.id.button21).setOnClickListener(onClickListener);
        this.findViewById(R.id.button22).setOnClickListener(onClickListener);
        this.findViewById(R.id.button23).setOnClickListener(onClickListener);
        this.findViewById(R.id.button30).setOnClickListener(onClickListener);
        this.findViewById(R.id.button31).setOnClickListener(onClickListener);
        this.findViewById(R.id.button32).setOnClickListener(onClickListener);
        this.findViewById(R.id.button33).setOnClickListener(onClickListener);
    }

}
