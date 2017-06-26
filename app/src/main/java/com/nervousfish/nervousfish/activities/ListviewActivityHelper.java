package com.nervousfish.nervousfish.activities;

import android.app.Activity;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.nervousfish.nervousfish.R;
import com.nervousfish.nervousfish.data_objects.Contact;
import com.nervousfish.nervousfish.data_objects.IKey;

import org.apache.commons.lang3.Validate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * The ListviewActivityHelper has some static methods which are
 * used in the ContactActivity and the ChangeContactActivity.
 */

final class ListviewActivityHelper {

    /**
     * Methods are static so this is not called.
     */
    private ListviewActivityHelper() {
        // Prevent instantiation
    }

    /**
     * Used to set text of a {@link TextView} in the {@link ContactActivity}.
     *
     * @param activity The where the text has to be set.
     * @param text The text.
     * @param id the {@code R.id} of the target {@link TextView}.
     */
    static void setText(final Activity activity, final String text, final int id) {
        final TextView tv = (TextView) activity.findViewById(id);
        tv.setText(text);
    }

    /**
     * Used to set verified icon of a {@link android.widget.ImageView} in the {@link ContactActivity}.
     *
     * @param activity The where the icon has to be set.
     * @param verified The boolean if the contact is verified.
     * @param id the {@code R.id} of the target {@link android.widget.ImageView}.
     */
    static void setVerified(final Activity activity, final boolean verified, final int id) {
        final ImageView tv = (ImageView) activity.findViewById(id);
        if (verified) {
            tv.setImageDrawable(activity.getResources().getDrawable(R.drawable.checked));
        } else {
            tv.setImageDrawable(activity.getResources().getDrawable(R.drawable.cancel));
        }
    }

    /**
     * Set the keys of the {@link Contact} to the {@link ContactActivity} or {@link KeyManagementActivity}.
     *
     * @param activity The where the name has to be set.
     * @param keys A {@link Collection} of {@link IKey}s.
     * @param id the {@code R.id} of the target {@link ListView}.
     */
    static void setKeys(final Activity activity, final Collection<IKey> keys, final int id) {
        Validate.notNull(activity);
        Validate.notNull(keys);
        Validate.isTrue(id > 0);
        final List<String> keyNames = new ArrayList<>();
        for (final IKey key : keys) {
            keyNames.add(key.getName());
        }

        final ListView lv = (ListView) activity.findViewById(id);
        lv.setAdapter(new ArrayAdapter<>(activity, android.R.layout.simple_list_item_1, keyNames));
    }

}
