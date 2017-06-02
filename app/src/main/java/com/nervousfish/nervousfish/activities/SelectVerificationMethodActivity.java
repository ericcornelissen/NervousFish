package com.nervousfish.nervousfish.activities;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.nervousfish.nervousfish.ConstantKeywords;
import com.nervousfish.nervousfish.R;
import com.nervousfish.nervousfish.service_locator.IServiceLocator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Activity used to select a verification method.
 */
public class SelectVerificationMethodActivity extends AppCompatActivity {

    private static final Logger LOGGER = LoggerFactory.getLogger("SelectVerificationMethodActivity");

    private IServiceLocator serviceLocator;

    /**
     * Creates a new {@link SelectVerificationMethodActivity} activity.
     *
     * @param savedInstanceState state previous instance of this activity
     */
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_select_verification_method);

        final Intent intent = this.getIntent();
        this.serviceLocator = (IServiceLocator) intent.getSerializableExtra(ConstantKeywords.SERVICE_LOCATOR);

        LOGGER.info("Activity created");
    }

    /**
     * Open the correct activity given a verification method.
     *
     * @param view The view on which the click was performed
     */
    public void openVerificationMethod(final View view) {
        final Intent intent = new Intent();
        intent.putExtra(ConstantKeywords.SERVICE_LOCATOR, this.serviceLocator);

        switch (view.getId()) {
            case R.id.select_visual_verification:
                LOGGER.info("Selected visual verification method, opening activity");
                intent.setComponent(new ComponentName(this, VisualVerificationActivity.class));
                break;
            case R.id.select_rhythm_verification:
                // TODO: open correct (Rhythm) activity (also update test!)
                LOGGER.info("Selected rhythm verification method, opening activity");
                intent.setComponent(new ComponentName(this, RhythmCreateActivity.class));
                break;
            default:
                LOGGER.warn("unknown verification method selected, view: " + view);
                this.showUnknownVerificationMethodPopup();
                return;
        }

        this.startActivity(intent);
    }

    /**
     * Show a popup when (somehow) an unknown verification method was selected.
     */
    private void showUnknownVerificationMethodPopup() {
        new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText(this.getString(R.string.unknown_verification_method_title))
                .setContentText(this.getString(R.string.unknown_verification_method_description))
                .setConfirmText(this.getString(R.string.dialog_ok))
                .show();
    }

}
