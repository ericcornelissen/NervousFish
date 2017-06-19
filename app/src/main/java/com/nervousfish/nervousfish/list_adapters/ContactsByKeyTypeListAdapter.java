package com.nervousfish.nervousfish.list_adapters;

import android.app.Activity;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.nervousfish.nervousfish.R;
import com.nervousfish.nervousfish.data_objects.Contact;
import com.nervousfish.nervousfish.data_objects.IKey;
import com.nervousfish.nervousfish.service_locator.NervousFish;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Class that is a expandable list adapter to sort contacts by key TYPES in a expandable view
 */

@SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
//  1)  At line 42 in ContactsByKeyTypeListAdapter  a new arraylist should be instantiated at every
//      type in the map
public final class ContactsByKeyTypeListAdapter extends BaseExpandableListAdapter {

    private final Map<String, List<Contact>> groupedContacts;
    private final List<String> types;
    private final Activity context;

    /**
     * Constructor for this {@code ContactsByKeyTypeListAdapter } listadapter
     *
     * @param context  The Activity in which the adapter is needed
     * @param contacts The list of contacts to be sorted
     */
    public ContactsByKeyTypeListAdapter(final Activity context, final List<Contact> contacts) {
        super();
        this.context = context;
        this.types = new ArrayList<>();
        final Set<String> typeSet = new HashSet<>();
        for (final Contact contact : contacts) {
            for (final IKey key : contact.getKeys()) {
                typeSet.add(key.getType());
            }
        }
        this.types.addAll(typeSet);
        this.groupedContacts = new HashMap<>();
        for (final String type : this.types) {
            this.groupedContacts.put(type, new ArrayList<Contact>());
        }
        for (final Contact contact : contacts) {
            for (final IKey key : contact.getKeys()) {
                if (!this.groupedContacts.get(key.getType()).contains(contact)) {
                    this.groupedContacts.get(key.getType()).add(contact);
                }
            }
        }


    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object getChild(final int groupPosition, final int childPosition) {

        return this.groupedContacts.get(this.types.get(groupPosition)).get(childPosition);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getChildId(final int groupPosition, final int childPosition) {
        return childPosition;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public View getChildView(final int groupPosition, final int childPosition,
                             final boolean isLastChild, final View convertView, final ViewGroup parent) {
        final Contact contact = (Contact) this.getChild(groupPosition, childPosition);
        final View v;

        if (convertView == null) {
            v = View.inflate(this.context, R.layout.contact_list_entry, null);
        } else {
            v = convertView;
        }


        if (contact != null) {
            final TextView name = (TextView) v.findViewById(R.id.contact_name_list_entry);

            if (name != null) {
                name.setText(contact.getName());
            }
        }

        return v;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getChildrenCount(final int groupPosition) {
        return this.groupedContacts.get(this.types.get(groupPosition)).size();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object getGroup(final int groupPosition) {
        return this.types.get(groupPosition);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getGroupCount() {
        return this.types.size();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getGroupId(final int groupPosition) {
        return groupPosition;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public View getGroupView(final int groupPosition, final boolean isExpanded,
                             final View convertView, final ViewGroup parent) {
        final String type = (String) this.getGroup(groupPosition);
        final View view;
        if (convertView == null) {
            view = View.inflate(this.context, R.layout.key_type, null);
        } else {
            view = convertView;
        }

        final TextView item = (TextView) view.findViewById(R.id.key_type);
        item.setTypeface(null, Typeface.BOLD);
        item.setText(String.format(NervousFish.getInstance().getResources().getString(R.string.key_type), type));
        return view;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasStableIds() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isChildSelectable(final int groupPosition, final int childPosition) {
        return true;
    }
}