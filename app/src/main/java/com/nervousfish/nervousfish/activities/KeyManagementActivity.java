package com.nervousfish.nervousfish.activities;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
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

        lv.setOnItemClickListener(new KeyManagementActivity.KeyListClickListener(builder, this, myProfile));

        final ImageButton backButton = (ImageButton) this.findViewById(R.id.back_button_key_management);
        backButton.setOnClickListener(v -> KeyManagementActivity.this.finish());

        LOGGER.info("Activity created");
    }

    /**
     * An {@link AdapterView.OnItemClickListener}
     * which listents to the clicks on keys in  the list view of {@link KeyManagementActivity}
     */
    private static class KeyListClickListener implements AdapterView.OnItemClickListener {
        private final AlertDialog.Builder builder;
        private final Activity activity;
        private final Profile profile;

        /**
         * A Constructor for {@link AdapterView.OnItemClickListener} where we pass an
         * Alertdialog builder
         *
         * @param builder An {@link AlertDialog.Builder}
         */
        KeyListClickListener(final AlertDialog.Builder builder, final Activity activity, final Profile profile) {
            this.builder = builder;
            this.activity = activity;
            this.profile = profile;
        }

        @Override
        public final void onItemClick(final AdapterView<?> parent, final View view, final int position,
                                      final long id) {
            this.builder.setPositiveButton("Copy", (dialog, which) -> {
                final Activity activity = this.activity;
                final Profile profile = this.profile;
                final ClipboardManager clipboard = (ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);
                final ClipData clip = ClipData.newPlainText(MIMETYPE_TEXT_PLAIN, profile.getKeyPairs().get(0).getPublicKey().getKey());
                clipboard.setPrimaryClip(clip);
            });
            this.builder.show();
        }
    }
}