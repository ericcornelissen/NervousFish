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
import com.nervousfish.nervousfish.data_objects.AKeyPair;
import com.nervousfish.nervousfish.data_objects.IKey;
import com.nervousfish.nervousfish.data_objects.Profile;
import com.nervousfish.nervousfish.exceptions.DatabaseException;
import com.nervousfish.nervousfish.modules.database.IDatabase;
import com.nervousfish.nervousfish.service_locator.IServiceLocator;
import com.nervousfish.nervousfish.service_locator.NervousFish;

import org.apache.commons.lang3.Validate;
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
        final Profile myProfile;
        try {
            myProfile = database.getProfile();
        } catch (final IOException e) {
            LOGGER.error("Could not get my public key from the database ", e);
            throw new DatabaseException(e);
        }
        final String title = myProfile.getKeyPairs().get(0).getName();
        final String key = ((IKey) myProfile.getKeyPairs().get(0).getPublicKey()).getFormattedKey();

        final List<IKey> list = new ArrayList<>();
        //noinspection Convert2streamapi Because if we want to refactor this to a stream, we have to use at least API 24
        for (final AKeyPair kp : myProfile.getKeyPairs()) {
            list.add((IKey) kp.getPublicKey());
        }

        ListviewActivityHelper.setKeys(this, list, R.id.list_view_edit_keys);

        final ListView lv = (ListView) this.findViewById(R.id.list_view_edit_keys);

        lv.setOnItemClickListener(new KeyManagementActivity.KeyListClickListener(this, title, key, myProfile));

        final ImageButton backButton = (ImageButton) this.findViewById(R.id.back_button_key_management);
        backButton.setOnClickListener(v -> this.finish());

        LOGGER.info("Activity created");
    }

    /**
     * An {@link AdapterView.OnItemClickListener}
     * which listens to the clicks on keys in  the list view of {@link KeyManagementActivity}
     */
    private static final class KeyListClickListener implements AdapterView.OnItemClickListener {
        private final Activity activity;
        private final Profile profile;
        private final String title;
        private final String key;

        /**
         * * A Constructor for {@link AdapterView.OnItemClickListener} where we pass an
         * Alertdialog builder
         *
         * @param activity {@link Activity} where this listener is located
         * @param title    Title of the dialog
         * @param key      The string which shoud be presented in the body of the dialog
         * @param profile  Your {@link Profile}
         */
        KeyListClickListener(final Activity activity, final String title, final String key, final Profile profile) {
            Validate.notNull(activity);
            Validate.notBlank(title);
            Validate.notNull(key);
            Validate.notNull(profile);
            this.activity = activity;
            this.profile = profile;
            this.title = title;
            this.key = key;
        }

        @Override
        public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this.activity);
            builder.setTitle(this.title);
            builder.setMessage(key);
            builder.setPositiveButton("Copy", (dialog, which) -> {
                final Activity activity = this.activity;
                final Profile profile = this.profile;
                final ClipboardManager clipboard = (ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);
                final ClipData clip = ClipData.newPlainText(MIMETYPE_TEXT_PLAIN,
                        ((IKey) profile.getKeyPairs().get(0).getPublicKey()).getPlainKey());
                clipboard.setPrimaryClip(clip);
            });
            builder.show();
        }
    }
}