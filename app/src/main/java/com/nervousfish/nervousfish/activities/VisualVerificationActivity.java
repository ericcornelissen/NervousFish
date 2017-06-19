package com.nervousfish.nervousfish.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.nervousfish.nervousfish.ConstantKeywords;
import com.nervousfish.nervousfish.R;
import com.nervousfish.nervousfish.data_objects.Contact;
import com.nervousfish.nervousfish.data_objects.KeyPair;
import com.nervousfish.nervousfish.data_objects.Profile;
import com.nervousfish.nervousfish.data_objects.Ed25519Key;
import com.nervousfish.nervousfish.exceptions.EncryptionException;
import com.nervousfish.nervousfish.exceptions.NoBluetoothException;
import com.nervousfish.nervousfish.modules.pairing.events.NewEncryptedBytesReceivedEvent;
import com.nervousfish.nervousfish.service_locator.IServiceLocator;
import com.nervousfish.nervousfish.service_locator.NervousFish;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;

/**
 * An {@link Activity} that is used to let the user verify his identity by tapping on certain places in an image.
 */
public final class VisualVerificationActivity extends Activity {

    private static final Logger LOGGER = LoggerFactory.getLogger("VisualVerificationActivity");
    private static final int SECURITY_CODE_LENGTH = 6;
    private static final int NUM_BUTTONS = 12;

    private IServiceLocator serviceLocator;
    private int securityCode;
    private int numTaps;
    private byte[] dataReceived;

    /**
     * {@inheritDoc}
     */
    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_visual_verification);
        this.serviceLocator = NervousFish.getServiceLocator();

        LOGGER.info("Activity created");
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
     * Go to the next activity and provide it with the generated pattern.
     */
    private void nextActivity() {
        LOGGER.info("Done tapping the VisualVerification");
        try {
            final Profile profile = this.serviceLocator.getDatabase().getProfile();
            final KeyPair keyPair = profile.getKeyPairs().get(0);

            LOGGER.info("Sending my profile with name: {}, public key: {}", profile.getName(), keyPair.getPublicKey());

            final Contact myProfileAsContact = new Contact(profile.getName(), new Ed25519Key("Ed25519 key", "73890ien"));
            this.serviceLocator.getBluetoothHandler().send(myProfileAsContact, this.securityCode);
        } catch (final IOException e) {
            LOGGER.error("Could not send my contact to other device");
            throw new NoBluetoothException(e);
        } catch (final BadPaddingException | IllegalBlockSizeException e) {
            LOGGER.error("Could not encrypt the contact");
            throw new EncryptionException(e);
        }

        final Intent intent = new Intent(this, WaitActivity.class);
        intent.putExtra(ConstantKeywords.WAIT_MESSAGE, this.getString(R.string.wait_message_partner_rhythm_tapping));
        intent.putExtra(ConstantKeywords.DATA_RECEIVED, this.dataReceived);
        intent.putExtra(ConstantKeywords.KEY, this.securityCode);
        this.startActivityForResult(intent, ConstantKeywords.START_RHYTHM_REQUEST_CODE);
    }

    /**
     * Action to perform when clicking on a button in the activity.
     *
     * @param v The view of the button being clicked.
     */
    public void buttonAction(final View v) {
        final int button = Integer.parseInt(v.getContentDescription().toString());
        LOGGER.info("button {} clicked", button);

        if (this.numTaps > VisualVerificationActivity.SECURITY_CODE_LENGTH) {
            LOGGER.warn("Security code already long enough");
        } else if (this.numTaps + 1 == VisualVerificationActivity.SECURITY_CODE_LENGTH) {
            this.securityCode += button;
            LOGGER.info("final code is: {}", this.securityCode);
            this.nextActivity();
        } else {
            this.numTaps++;
            this.securityCode *= NUM_BUTTONS;
            this.securityCode += button;
            LOGGER.info("code so far: {}", this.securityCode);
        }
    }

    /**
     * Called when a new byte array is received.
     *
     * @param event Contains the byte array
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNewBytesReceivedEvent(final NewEncryptedBytesReceivedEvent event) {
        LOGGER.info("onNewEncryptedBytesReceivedEvent called");
        this.dataReceived = event.getBytes();
    }
}
