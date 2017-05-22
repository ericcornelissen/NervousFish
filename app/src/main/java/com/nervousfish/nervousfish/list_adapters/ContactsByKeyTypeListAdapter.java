package com.nervousfish.nervousfish.list_adapters;

import android.app.Activity;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.nervousfish.nervousfish.R;
import com.nervousfish.nervousfish.data_objects.Contact;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Kilian on 22/05/2017.
 */

@SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
public final class ContactsByKeyTypeListAdapter extends BaseExpandableListAdapter {

    private final Map<String, List<Contact>> groupedContacts;
    private final List<String> types;
    private final Activity context;

    /**
     * Constructor for this listadapter
     * @param context The Activity in which the adapter is needed
     * @param types The list of keytypes
     * @param contacts  The list of contacts to be sorted
     */
    public ContactsByKeyTypeListAdapter(final Activity context, final List<String> types, final List<Contact> contacts) {
        super();
        this.context = context;
        this.types = types;
        groupedContacts = new HashMap<>();
        for (final String type : types) {
            groupedContacts.put(type, new ArrayList<Contact>());
        }
        for (final Contact contact : contacts) {
            for (final String type : types) {
                if (!groupedContacts.get(type).contains(contact)) {
                    groupedContacts.get(type).add(contact);
                }
            }
        }


    }

    @Override
    public Object getChild(final int groupPosition, final int childPosition) {

        return groupedContacts.get(types.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getChildId(final int groupPosition, final int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition,
                             final boolean isLastChild, final View convertView, final ViewGroup parent) {
        final Contact contact = (Contact) getChild(groupPosition, childPosition);
        View v = convertView;

        if (v == null) {
            final LayoutInflater vi = context.getLayoutInflater();
            v = vi.inflate(R.layout.contact_list_entry, null);
        }


        if (contact != null) {
            final TextView name = (TextView) v.findViewById(R.id.name);

            if (name != null) {
                name.setText(contact.getName());
            }
        }

        return v;
    }

    @Override
    public int getChildrenCount(final int groupPosition) {
        return groupedContacts.get(types.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(final int groupPosition) {
        return types.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return types.size();
    }

    @Override
    public long getGroupId(final int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(final int groupPosition, final boolean isExpanded,
                             final View convertView, final ViewGroup parent) {
        final String type = (String) getGroup(groupPosition);
        View view = convertView;
        if (convertView == null) {
            final LayoutInflater vi = context.getLayoutInflater();
            view = vi.inflate(R.layout.key_type, null);
        }

        final TextView item = (TextView) view.findViewById(R.id.type);
        item.setTypeface(null, Typeface.BOLD);
        item.setText("Keytype: " + type);
        return view;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public boolean isChildSelectable(final int groupPosition, final int childPosition) {
        return true;
    }
}