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
import com.nervousfish.nervousfish.exceptions.EncryptionException;
import com.nervousfish.nervousfish.modules.cryptography.IEncryptor;
import com.nervousfish.nervousfish.modules.database.IDatabase;
import com.nervousfish.nervousfish.modules.pairing.events.NewDecryptedBytesReceivedEvent;
import com.nervousfish.nervousfish.modules.pairing.events.NewEncryptedBytesReceivedEvent;
import com.nervousfish.nervousfish.modules.pairing.events.NewDataReceivedEvent;
import com.nervousfish.nervousfish.service_locator.IServiceLocator;
import com.nervousfish.nervousfish.service_locator.NervousFish;

import org.apache.commons.lang3.Validate;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.SecretKey;

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
    private IDatabase database;
    private byte[] dataReceived;
    private int key;

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_wait);
        this.serviceLocator = NervousFish.getServiceLocator();
        this.encryptor = this.serviceLocator.getEncryptor();
        this.database = this.serviceLocator.getDatabase();

        final Intent intent = this.getIntent();

        this.dataReceived = (byte[]) intent.getSerializableExtra(ConstantKeywords.DATA_RECEIVED);
        this.key = (int) intent.getSerializableExtra(ConstantKeywords.KEY);

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

        if (this.dataReceived != null) {
            this.onNewEncryptedBytesReceivedEvent(new NewEncryptedBytesReceivedEvent(this.dataReceived));
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
        } else if (event.getClazz().equals(Contact.class)) {
            final Contact contact = (Contact) event.getData();
            ContactReceivedHelper.newContactReceived(this.database, this, contact);
            this.goToMainActivity();
        }
    }

    /**
     * Called when a new byte array is received.
     *
     * @param event Contains the byte array
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNewEncryptedBytesReceivedEvent(final NewEncryptedBytesReceivedEvent event) {
        Validate.notNull(event);
        LOGGER.info("onNewEncryptedBytesReceivedEvent called");
        final SecretKey password = this.encryptor.makeKeyFromPassword(Integer.toString(this.key));
        final byte[] bytes;
        try {
            bytes = this.encryptor.decryptWithPassword(event.getBytes().toString(), password).getBytes();
        } catch (final IllegalBlockSizeException | BadPaddingException e) {
            throw new EncryptionException(e);
        }
        this.serviceLocator.postOnEventBus(new NewDecryptedBytesReceivedEvent(bytes));
    }

    /**
     * Can be called by a button to cancel the pairing
     *
     * @param view The view that called this method
     */
    public void cancelWaiting(final View view) {
        this.setResult(ConstantKeywords.CANCEL_PAIRING_RESULT_CODE);
        this.finish();
    }

    /**
     * Launch the mainActivity at the top.
     */
    private void goToMainActivity() {
        LOGGER.info("Going to the main activity");
        final Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(ConstantKeywords.SUCCESSFUL_EXCHANGE, true);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        this.startActivity(intent);
    }
}
