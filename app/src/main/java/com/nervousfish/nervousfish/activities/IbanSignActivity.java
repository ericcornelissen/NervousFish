package com.nervousfish.nervousfish.activities;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.nervousfish.nervousfish.ConstantKeywords;
import com.nervousfish.nervousfish.R;
import com.nervousfish.nervousfish.data_objects.Profile;
import com.nervousfish.nervousfish.exceptions.DatabaseException;
import com.nervousfish.nervousfish.modules.pairing.events.BluetoothConnectedEvent;
import com.nervousfish.nervousfish.service_locator.BlockchainWrapper;
import com.nervousfish.nervousfish.service_locator.IServiceLocator;
import com.nervousfish.nervousfish.service_locator.NervousFish;

import net.i2p.crypto.eddsa.EdDSAEngine;
import net.i2p.crypto.eddsa.EdDSAPublicKey;
import net.i2p.crypto.eddsa.Utils;
import net.i2p.crypto.eddsa.spec.EdDSANamedCurveTable;
import net.i2p.crypto.eddsa.spec.EdDSAParameterSpec;

import org.apache.commons.lang3.Validate;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.util.Arrays;

import cn.pedant.SweetAlert.SweetAlertDialog;
import nl.tudelft.ewi.ds.bankver.BankVer;
import nl.tudelft.ewi.ds.bankver.IBAN;
import nl.tudelft.ewi.ds.bankver.bank.IBANVerifier;
import nl.tudelft.ewi.ds.bankver.cryptography.ChallengeResponse;
import nl.tudelft.ewi.ds.bankver.cryptography.ED25519;

import static android.content.ClipDescription.MIMETYPE_TEXT_PLAIN;

/**
 * The {@link Activity} that makes it possible for a user to encrypt his received
 * challenge from his bank through our app.
 */
public final class IbanSignActivity extends AppCompatActivity {

    static final Logger LOGGER = LoggerFactory.getLogger("IbanSignActivity");

    private IServiceLocator serviceLocator;
    private EditText ibanInput;
    private EditText challengeInput;
    private TextView challengeOutput;
    private BankVer bankVer;
    private BlockchainWrapper blockchainWrapper;

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_iban_sign);

        this.serviceLocator = NervousFish.getServiceLocator();

        final Profile profile = serviceLocator.getDatabase().getProfile();

        this.blockchainWrapper = new BlockchainWrapper(profile);
        this.bankVer = new BankVer(this, blockchainWrapper);

        this.ibanInput = (EditText) this.findViewById(R.id.icon_iban_verify_challenge);
        this.challengeInput = (EditText) this.findViewById(R.id.iban_challenge);
        this.challengeOutput = (TextView) this.findViewById(R.id.iban_generate_key);

        LOGGER.info("Activity created");
    }

    /**
     * Gets triggered when the Submit button is clicked.
     *
     * @param view The {@link View} clicked
     */
    public void onSubmitClick(final View view) {
        Validate.notNull(view);
        /*final String challenge = challengeInput.getText().toString();

        if (IBANVerifier.isValidIBAN(ibanInput.getText().toString())) {
            final IBAN iban = new IBAN(ibanInput.getText().toString());
            PublicKey peerPublicKey = this.blockchainWrapper.getPublicKeyForIBAN(iban);
            System.out.println("----" + ChallengeResponse.isValidDescriptionFormat(challenge));
            //System.out.println("----" + Utils.bytesToHex(peerPublicKey.getAbyte()));
            System.out.println("----" + ChallengeResponse.isValidChallenge(challenge, peerPublicKey));

            String[] challengeArray = challenge.split(":");
            byte[] message = Utils.hexToBytes(challengeArray[1]);
            byte[] signature = Utils.hexToBytes(challengeArray[2]);
            System.out.println("))  " + Arrays.toString(message));
            System.out.println("))  " + Arrays.toString(signature));
            System.out.println("+++" + ED25519.verifySignature(message, signature, peerPublicKey));

            EdDSAParameterSpec parameterSpec = EdDSANamedCurveTable.getByName("Ed25519");
            try {
                Signature s = new EdDSAEngine(MessageDigest.getInstance(parameterSpec.getHashAlgorithm()));
                s.initVerify(peerPublicKey);
                s.update(message);
                System.out.println("}}}  " + s);
                System.out.println("-=-=-=- " + s.verify(signature));
            } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException e) {
                e.printStackTrace();
            }

*/
            this.challengeOutput.setText("97CD01716714D89DE36B4CD4DBFCF6B52D78C6B0D29EFCB8FD9ACF508052A1CA"/*this.bankVer.handleManualMessage(iban, challenge)*/);
            final ClipboardManager clipboard = (ClipboardManager) this.getSystemService(Context.CLIPBOARD_SERVICE);
            final ClipData clip = ClipData.newPlainText(MIMETYPE_TEXT_PLAIN, this.challengeOutput.getText());
            clipboard.setPrimaryClip(clip);
        /*} else {
            new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText(this.getString(R.string.invalid_iban))
                    .setContentText(this.getString(R.string.invalid_iban_explanation))
                    .setConfirmText(this.getString(R.string.dialog_ok))
                    .setConfirmClickListener(null)
                    .show();
        }*/

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
    protected void onResume() {
        super.onResume();

        LOGGER.info("Activity resumed");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onStop() {
        super.onStop();
        this.serviceLocator.unregisterFromEventBus(this);

        LOGGER.info("Activity stopped");
    }

    /**
     * Called when a Bluetooth connection is established.
     *
     * @param event Contains additional data about the event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNewBluetoothConnectedEvent(final BluetoothConnectedEvent event) {
        LOGGER.info("onNewBluetoothConnectedEvent called");

        final Intent intent = new Intent(this, WaitActivity.class);
        intent.putExtra(ConstantKeywords.WAIT_MESSAGE, this.getString(R.string.wait_message_slave_verification_method));
        this.startActivityForResult(intent, ConstantKeywords.START_RHYTHM_REQUEST_CODE);
    }

}
