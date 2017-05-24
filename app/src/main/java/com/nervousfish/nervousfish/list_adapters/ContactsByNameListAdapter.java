package com.nervousfish.nervousfish.list_adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.nervousfish.nervousfish.R;
import com.nervousfish.nervousfish.data_objects.Contact;

import java.util.List;

/**
 * An Adapter which converts a list with contacts into List entries.
 * {@link ContactsByNameListAdapter}
 */
public final class ContactsByNameListAdapter extends ArrayAdapter<Contact> {

    /**
     * Create and initialize a ContactsByNameListAdapter.
     *
     * @param context  the Context where the ListView is created
     * @param contacts the list with contacts
     */
    public ContactsByNameListAdapter(final Context context, final List<Contact> contacts) {
        super(context, 0, contacts);
    }

    /**
     * {@inheritDoc}
     */
    @NonNull
    @Override
    public View getView(final int position, final View convertView, @NonNull final ViewGroup parent) {
        final View v;

        if (convertView == null) {
            final LayoutInflater vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.contact_list_entry, null);
        } else {
            v = convertView;
        }

        final Contact contact = getItem(position);

        if (contact != null) {
            final TextView name = (TextView) v.findViewById(R.id.name);

            if (name != null) {
                name.setText(contact.getName());
            }
        }

        return v;
    }

}
