package com.nervousfish.nervousfish.activities;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.nervousfish.nervousfish.R;
import com.nervousfish.nervousfish.data_objects.VerificationMethod;
import com.nervousfish.nervousfish.data_objects.VerificationMethodEnum;
import com.nervousfish.nervousfish.modules.pairing.events.NewDataReceivedEvent;
import com.nervousfish.nervousfish.modules.pairing.IBluetoothHandler;
import com.nervousfish.nervousfish.service_locator.IServiceLocator;
import com.nervousfish.nervousfish.service_locator.NervousFish;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Activity used to select a verification method.
 */
public final class SelectVerificationMethodActivity extends AppCompatActivity {

    private static final Logger LOGGER = LoggerFactory.getLogger("SelectVerificationMethodActivity");

    private IServiceLocator serviceLocator;
    private IBluetoothHandler bluetoothHandler;

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_select_verification_method);

        this.serviceLocator = NervousFish.getServiceLocator();
        this.bluetoothHandler = serviceLocator.getBluetoothHandler();

        LOGGER.info("Activity created");
    }

    /**
     * Open the correct activity given a verification method.
     *
     * @param view The view on which the click was performed
     */
    public void openVerificationMethod(final View view) {
        final Intent intent = new Intent();
        switch (view.getId()) {
            case R.id.select_visual_verification:
                LOGGER.info("Selected visual verification method, opening activity");
                try {
                    this.bluetoothHandler.send(new VerificationMethod(VerificationMethodEnum.VISUAL));
                } catch (final IOException e) {
                    LOGGER.error("Sending the Verification VISUAL went wrong: ", e);
                }
                intent.setComponent(new ComponentName(this, VisualVerificationActivity.class));
                break;
            case R.id.select_rhythm_verification:
                LOGGER.info("Selected rhythm verification method, opening activity");
                try {
                    this.bluetoothHandler.send(new VerificationMethod(VerificationMethodEnum.RHYTHM));
                } catch (final IOException e) {
                    LOGGER.error("Sending the Verification RHYTHM went wrong: ", e);
                }
                intent.setComponent(new ComponentName(this, RhythmCreateActivity.class));
                break;
            default:
                LOGGER.warn("unknown verification method selected, view: {}", view);
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

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onStart() {
        super.onStart();
        this.serviceLocator.registerToEventBus(this);

        LOGGER.info("Activity started");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onStop() {
        this.serviceLocator.unregisterFromEventBus(this);

        LOGGER.info("Activity stopped");
        super.onStop();
    }

    /**
     * Called when a new data is received.
     *
     * @param event Contains additional data about the event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNewDataReceivedEvent(final NewDataReceivedEvent event) {
        LOGGER.info("onNewDataReceivedEvent called, type is " + event.getClazz());
        if (event.getClazz().equals(VerificationMethod.class)) {
            final VerificationMethodEnum verificationMethod = ((VerificationMethod) event.getData()).getVerificationMethod();

            final Intent intent = new Intent();
            switch (verificationMethod) {
                case RHYTHM:
                    //Go to RhythmActivity
                    intent.setComponent(new ComponentName(this, RhythmCreateActivity.class));
                    break;
                case VISUAL:
                    //Go to VisualVerificationActivity
                    intent.setComponent(new ComponentName(this, VisualVerificationActivity.class));
                    break;
                default:
                    LOGGER.error("Unknown verification method");
                    throw new IllegalArgumentException("Only existing verification methods can be used");
            }
            this.startActivityForResult(intent, 0);
        }
    }

}
