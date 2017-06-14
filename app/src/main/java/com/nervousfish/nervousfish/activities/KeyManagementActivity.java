package com.nervousfish.nervousfish.activities;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;

import com.nervousfish.nervousfish.R;
import com.nervousfish.nervousfish.data_objects.IKey;
import com.nervousfish.nervousfish.data_objects.KeyPair;
import com.nervousfish.nervousfish.data_objects.Profile;
import com.nervousfish.nervousfish.modules.database.IDatabase;
import com.nervousfish.nervousfish.service_locator.IServiceLocator;
import com.nervousfish.nervousfish.service_locator.NervousFish;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import static android.content.ClipDescription.MIMETYPE_TEXT_PLAIN;

/**
 * An {@link Activity} that used for adding, changing and deleting key pairs.
 */
public class KeyManagementActivity extends Activity {

    private static final Logger LOGGER = LoggerFactory.getLogger("KeyManagementActivity");

    private String[] key = {};

    /**
     * Creates the new activity, should only be called by Android
     *
     * @param savedInstanceState The saved state of the instance.
     */
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_key_management);
        final IServiceLocator serviceLocator = NervousFish.getServiceLocator();
        final IDatabase database = serviceLocator.getDatabase();
        String title = "";
        Profile myProfile = null;
        try {
            myProfile = database.getProfiles().get(0);
            title = myProfile.getKeyPairs().get(0).getName();
            key = myProfile.getKeyPairs().get(0).getPublicKey().getKey().split(" ");

        } catch (IOException e) {
            LOGGER.error("Could not get my public key from the database ", e);
        }

        final ArrayList<IKey> list = new ArrayList<>();
        for (KeyPair kp : myProfile.getKeyPairs()) {
            list.add(kp.getPublicKey());
        }

        ListviewActivityHelper.setKeys(this, list, R.id.list_view_contact);

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(String.format("This is the exponent: %s \n and this the modulus: %s ", key[0], key[1]));

        builder.setPositiveButton("Copy", new DialogInterface.OnClickListener() {
            public void onClick(final DialogInterface dialog, final int id) {
                final ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                final ClipData clip = ClipData.newPlainText(MIMETYPE_TEXT_PLAIN, Arrays.toString(key));
                clipboard.setPrimaryClip(clip);
            }
        });
        builder.show();

        LOGGER.info("Activity created");
    }
}