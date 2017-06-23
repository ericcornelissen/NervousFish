package com.nervousfish.nervousfish.activities;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.nervousfish.nervousfish.ConstantKeywords;
import com.nervousfish.nervousfish.R;
import com.nervousfish.nervousfish.data_objects.Contact;
import com.nervousfish.nervousfish.data_objects.VerificationMethod;
import com.nervousfish.nervousfish.data_objects.VerificationMethodEnum;
import com.nervousfish.nervousfish.modules.cryptography.IEncryptor;
import com.nervousfish.nervousfish.modules.pairing.ByteWrapper;
import com.nervousfish.nervousfish.modules.pairing.events.NewDecryptedBytesReceivedEvent;
import com.nervousfish.nervousfish.modules.pairing.events.NewDataReceivedEvent;
import com.nervousfish.nervousfish.service_locator.IServiceLocator;
import com.nervousfish.nervousfish.service_locator.NervousFish;

import org.apache.commons.lang3.Validate;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.GeneralSecurityException;

import javax.crypto.BadPaddingException;

import java.io.IOException;

/**
 * Used to let the Bluetooth-initiating user know that he should wait for his partner
 * to complete the pairing session. Via this Activity the verification method
 * is started. Lastly, this activity evaluates the received data with the tap combination
 * created by our own device.
 */
public final class WaitActivity extends Activity {

    private static final Logger LOGGER = LoggerFactory.getLogger("WaitActivity");
    private IServiceLocator serviceLocator;
    private IEncryptor encryptor;
    private byte[] dataReceived;
    private Long key;
    private Class classStartedFrom;

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_wait);
        this.serviceLocator = NervousFish.getServiceLocator();
        this.encryptor = this.serviceLocator.getEncryptor();

        final Intent intent = this.getIntent();
        this.dataReceived = (byte[]) intent.getSerializableExtra(ConstantKeywords.DATA_RECEIVED);
        this.key = (Long) intent.getSerializableExtra(ConstantKeywords.KEY);
        this.classStartedFrom = (Class) intent.getSerializableExtra(ConstantKeywords.CLASS_STARTED_FROM);

        LOGGER.info("dataReceived is not null: {}, key is: {}", this.dataReceived != null, this.key);

        final String message = (String) intent.getSerializableExtra(ConstantKeywords.WAIT_MESSAGE);
        final TextView waitingMessage = (TextView) this.findViewById(R.id.waiting_message);
        waitingMessage.setText(message);

        LOGGER.info("WaitActivity created");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == ConstantKeywords.DONE_PAIRING_RESULT_CODE) {
            this.setResult(ConstantKeywords.DONE_PAIRING_RESULT_CODE);
            this.finish();
        } else if (resultCode == ConstantKeywords.CANCEL_PAIRING_RESULT_CODE) {
            this.setResult(ConstantKeywords.CANCEL_PAIRING_RESULT_CODE);
            this.finish();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onStart() {
        super.onStart();
        this.serviceLocator.registerToEventBus(this);

        if (this.dataReceived != null && this.key != null) {
            this.validateEncryptedData();
        }

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
        LOGGER.info("onNewDataReceivedEvent called, type is {}", event.getClazz());
        if (event.getClazz().equals(VerificationMethod.class)) {
            final VerificationMethodEnum verificationMethod = ((VerificationMethod) event.getData()).getVerificationMethod();

            final Intent intent = new Intent();
            switch (verificationMethod) {
                case RHYTHM:
                    //Go to RhythmActivity
                    intent.setComponent(new ComponentName(this, RhythmVerificationActivity.class));
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
        } else if (event.getClazz().equals(ByteWrapper.class)) {
            this.dataReceived = ((ByteWrapper) event.getData()).getBytes();
            this.validateEncryptedData();
        } else if (event.getClazz().equals(Contact.class)) {
            final Contact contact = (Contact) event.getData();
            this.goToMainActivity(contact);
        }
    }

    /**
     * Can be called by a button to cancel the pairing
     *
     * @param view The view that called this method
     */
    public void cancelWaiting(final View view) {
        Validate.notNull(view);
        this.setResult(ConstantKeywords.CANCEL_PAIRING_RESULT_CODE);
        this.finish();
    }

    /**
     * Will validate and save the contact saved in the bytearray.
     */
    public void validateEncryptedData() {
        try {
            final byte[] decryptedData = this.encryptor.decryptWithPassword(this.dataReceived, this.key);
            LOGGER.info("Decrypted data");
            this.serviceLocator.postOnEventBus(new NewDecryptedBytesReceivedEvent(decryptedData));
        } catch (final BadPaddingException e) {
            LOGGER.warn("Keys didn't match! Going back to the rhythm activity");
            final Intent intent = new Intent(this, this.classStartedFrom);
            intent.putExtra(ConstantKeywords.TAPPING_FAILURE, true);
            this.startActivity(intent);
        } catch (final GeneralSecurityException e) {
            LOGGER.error("An error occurred when validating the encrypted data", e);
        }
    }

    /**
     * Launch the mainActivity at the top.
     *
     * @param contact The contact to give to the {@link MainActivity}.
     */
    private void goToMainActivity(final Contact contact) {
        LOGGER.info("Going to the main activity");

        final Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(ConstantKeywords.CONTACT, contact);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        this.startActivity(intent);
    }

}
