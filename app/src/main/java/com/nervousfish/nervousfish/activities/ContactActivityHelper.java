package com.nervousfish.nervousfish.activities;

import android.app.Activity;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.nervousfish.nervousfish.R;
import com.nervousfish.nervousfish.data_objects.Contact;
import com.nervousfish.nervousfish.data_objects.IKey;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * The ContactActivityHelper has some static methods which are
 * used in the ContactActivity and the ChangeContactActivity.
 */

final class ContactActivityHelper {

    /**
     * Methods are static so this is not called.
     */
    private ContactActivityHelper() {
        // Prevent instantiation
    }

    /**
     * Set the name of the {@link Contact} to the {@link ContactActivity}.
     *
     * @param activity The where the name has to be set.
     * @param name The name.
     */
    static void setName(final Activity activity, final String name) {
        final TextView tv = (TextView) activity.findViewById(R.id.contact_name);
        tv.setText(name);
    }

    /**
     * Set the keys of the {@link Contact} to the {@link ContactActivity}.
     *
     * @param activity The where the name has to be set.
     * @param keys A {@link Collection} of {@link IKey}s.
     */
    static void setKeys(final Activity activity, final Collection<IKey> keys) {
        final List<String> keyNames = new ArrayList<>();
        for (final IKey key : keys) {
            keyNames.add(key.getName());
        }

        final ListView lv = (ListView) activity.findViewById(R.id.listView);
        lv.setAdapter(new ArrayAdapter<>(activity,
                android.R.layout.simple_list_item_1, keyNames));
    }
}
