package com.nervousfish.nervousfish.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.nervousfish.nervousfish.ConstantKeywords;
import com.nervousfish.nervousfish.R;
import com.nervousfish.nervousfish.service_locator.IServiceLocator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An {@link Activity} that is used to let the user verify his identity by tapping on certain places in an image.
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
        this.setContentView(R.layout.visual_verification);

        final Intent intent = this.getIntent();
        this.serviceLocator = (IServiceLocator) intent.getSerializableExtra(ConstantKeywords.SERVICE_LOCATOR);

        LOGGER.info("VisualVerificationActivity created");
    }

    /**
     * Go to the next activity and provide it with the generated pattern.
     */
    private void nextActivity() {
        final Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(ConstantKeywords.SECURITY_CODE, this.securityCode);
        intent.putExtra(ConstantKeywords.SERVICE_LOCATOR, this.serviceLocator);
        this.startActivity(intent);
    }

    /**
     * Action to perform when clicking on a button in the activity.
     *
     * @param v The view of the button being clicked.
     */
    public void buttonAction(final View v) {
        final String button = v.getContentDescription().toString();
        LOGGER.info("button '%s' clicked", button);

        if (this.securityCode.length() > VisualVerificationActivity.SECURITY_CODE_LENGTH) {
            LOGGER.warn("Security code already long enough");
        } else if (this.securityCode.length() + 1 == VisualVerificationActivity.SECURITY_CODE_LENGTH) {
            this.securityCode += button;
            LOGGER.info("final code is: %s", this.securityCode);
            this.nextActivity();
        } else {
            this.securityCode += button;
            LOGGER.info("code so far: %s", this.securityCode);
        }
    }

}
