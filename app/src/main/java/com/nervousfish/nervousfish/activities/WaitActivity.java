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
import com.nervousfish.nervousfish.modules.constants.IConstants;
import com.nervousfish.nervousfish.modules.cryptography.IEncryptor;
import com.nervousfish.nervousfish.modules.database.IDatabase;
import com.nervousfish.nervousfish.modules.pairing.ByteWrapper;
import com.nervousfish.nervousfish.modules.pairing.events.NewDecryptedBytesReceivedEvent;
import com.nervousfish.nervousfish.modules.pairing.events.NewDataReceivedEvent;
import com.nervousfish.nervousfish.service_locator.IServiceLocator;
import com.nervousfish.nervousfish.service_locator.NervousFish;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import java.io.IOException;
import javax.crypto.spec.SecretKeySpec;

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
    private IConstants constants;
    private byte[] dataReceived;
    private Contact contactReceived;
    private Object tapCombination;
    private Long key;

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
        this.constants = this.serviceLocator.getConstants();

        final Intent intent = this.getIntent();

        this.dataReceived = (byte[]) intent.getSerializableExtra(ConstantKeywords.DATA_RECEIVED);
        final Serializable keyFromIntent = intent.getSerializableExtra(ConstantKeywords.KEY);
        if (keyFromIntent != null) {
            this.key = (long) intent.getSerializableExtra(ConstantKeywords.KEY);
        }

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

//        if (this.dataReceived != null) {
//            this.onNewEncryptedBytesReceivedEvent(new NewEncryptedBytesReceivedEvent(this.dataReceived));
//        }

        if (this.dataReceived != null && this.key != null) {
            validateEncryptedData();
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
        } else if (event.getClazz().equals(ByteWrapper.class)) {
            this.dataReceived = ((ByteWrapper) event.getData()).getBytes();
            this.validateEncryptedData();
        } else if (event.getClazz().equals(Contact.class)) {
            final Contact contact = (Contact) event.getData();
            ContactReceivedHelper.newContactReceived(this.database, this, contact);
            this.goToMainActivity(contact);
        }
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
     * Will validate and save the contact saved in the bytearray.
     */
    public void validateEncryptedData() {
        try {
            final ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
            buffer.putLong(key);

            final String k = "Bar12345Bar12345";
            final Key aesKey = new SecretKeySpec(k.getBytes(), "AES");
            final Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, aesKey);
            final byte[] decryptedData = cipher.doFinal(this.dataReceived);

            LOGGER.info("Decrypted data");
            this.serviceLocator.postOnEventBus(new NewDecryptedBytesReceivedEvent(decryptedData));
        } catch (Exception e) {
            LOGGER.error("An error occured when validating the encrypted data", e);
        }
    }

    /**
     * Launch the mainActivity at the top.
     *
     * @param contact The contact to give to the {@link MainActivity}.
     */
    private void goToMainActivity(final Contact contact) {
        LOGGER.info("Going to the main activity");
        try {
            this.serviceLocator.getBluetoothHandler().stop();
            this.serviceLocator.getBluetoothHandler().start();
        } catch (IOException e) {
            LOGGER.error("Restarting the threads went wrong", e);
        }
        final Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(ConstantKeywords.CONTACT, contact);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        this.startActivity(intent);
    }

}
