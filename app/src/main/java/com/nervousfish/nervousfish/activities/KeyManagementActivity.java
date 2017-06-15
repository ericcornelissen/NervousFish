package com.nervousfish.nervousfish.activities;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;

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
import java.util.List;

import static android.content.ClipDescription.MIMETYPE_TEXT_PLAIN;

/**
 * An {@link Activity} that used for adding, changing and deleting key pairs.
 */
public final class KeyManagementActivity extends Activity {

    private static final Logger LOGGER = LoggerFactory.getLogger("KeyManagementActivity");

    private Profile myProfile;

    /**
     * Creates the new activity, should only be called by Android
     *
     * @param savedInstanceState The saved state of the instance.
     */
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_key_management);
        final IServiceLocator serviceLocator = NervousFish.getServiceLocator();
        final IDatabase database = serviceLocator.getDatabase();
        try {
            this.myProfile = database.getProfiles().get(0);
        } catch (IOException e) {
            LOGGER.error("Could not get my public key from the database ", e);
        }
        final String title = this.myProfile.getKeyPairs().get(0).getName();
        final String[] key = this.myProfile.getKeyPairs().get(0).getPublicKey().getKey().split(" ");

        final List<IKey> list = new ArrayList<>();
        for (final KeyPair kp : this.myProfile.getKeyPairs()) {
            list.add(kp.getPublicKey());
        }

        ListviewActivityHelper.setKeys(this, list, R.id.list_view_edit_keys);

        final ListView lv = (ListView) this.findViewById(R.id.list_view_edit_keys);

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(String.format("Exponent: %s %nModulus: %s ", key[0], key[1]));

        lv.setOnItemClickListener(new KeyListClickListener(builder));

        final ImageButton backButton = (ImageButton) this.findViewById(R.id.back_button_key_management);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                KeyManagementActivity.this.finish();
            }
        });

        LOGGER.info("Activity created");
    }

    /**
     * An {@link android.widget.AdapterView.OnItemClickListener}
     * which listents to the clicks on keys in  the list view of {@link KeyManagementActivity}
     */
    private class KeyListClickListener implements AdapterView.OnItemClickListener {
        private final AlertDialog.Builder builder;

        /**
         * A Constructor for {@link android.widget.AdapterView.OnItemClickListener} where we pass an
         * Alertdialog builder
         *
         * @param builder An {@link android.support.v7.app.AlertDialog.Builder}
         */
        KeyListClickListener(final AlertDialog.Builder builder) {
            this.builder = builder;
        }

        @Override
        public final void onItemClick(final AdapterView<?> parent, final View view, final int position,
                                      final long id) {
            builder.setPositiveButton("Copy", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(final DialogInterface dialog, final int which) {
                    final ClipboardManager clipboard = (ClipboardManager) KeyManagementActivity.this.getSystemService(Context.CLIPBOARD_SERVICE);
                    final ClipData clip = ClipData.newPlainText(MIMETYPE_TEXT_PLAIN,
                            KeyManagementActivity.this.myProfile.getKeyPairs().get(0).getPublicKey().getKey());
                    clipboard.setPrimaryClip(clip);
                }
            });
            this.builder.show();
        }
    }
}