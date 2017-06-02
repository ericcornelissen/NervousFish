package com.nervousfish.nervousfish.activities;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.ViewFlipper;

import com.nervousfish.nervousfish.R;
import com.nervousfish.nervousfish.data_objects.Contact;
import com.nervousfish.nervousfish.data_objects.IKey;
import com.nervousfish.nervousfish.list_adapters.ContactsByKeyTypeListAdapter;
import com.nervousfish.nervousfish.list_adapters.ContactsByNameListAdapter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Is used to sort the list in the MainActivity
 */

class MainActivitySorter {
    private static final int SORT_BY_NAME = 0;
    private static final int SORT_BY_KEY_TYPE = 1;
    private static final int NUMBER_OF_SORTING_MODES = 2;
    private static final Comparator<Contact> NAME_SORTER = new Comparator<Contact>() {
        @Override
        public int compare(final Contact o1, final Contact o2) {
            return o1.getName().compareTo(o2.getName());
        }
    };

    private final MainActivity mainActivity;
    private int currentSorting;

    /**
     * Create and initialize the class.
     *
     * @param mainActivity The MainActivity where sorting is needed
     */
    MainActivitySorter(final MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    /**
     * Switches the sorting mode.
     *
     * @param view The sort floating action button that was clicked
     */
    void onSortButtonClicked(final View view) {
        currentSorting++;
        if (currentSorting >= NUMBER_OF_SORTING_MODES) {
            currentSorting = 0;
        }
        final ViewFlipper flipper = (ViewFlipper) mainActivity.findViewById(R.id.view_flipper_sorter_main);
        flipper.showNext();
        switch (currentSorting) {
            case SORT_BY_NAME:
                sortOnName();
                break;
            case SORT_BY_KEY_TYPE:
                sortOnKeyType();
                break;
            default:
                break;
        }
    }

    /**
     * Gets all Types of keys in the database
     *
     * @return a List with the Types of keys.
     */
    private List<String> getKeyTypes() {
        final Set<String> typeSet = new HashSet<>();
        for (final Contact c : mainActivity.getContacts()) {
            for (final IKey k : c.getKeys()) {
                typeSet.add(k.getType());
            }
        }
        return new ArrayList<>(typeSet);
    }

    /**
     * Sorts contacts by name
     */
    void sortOnName() {
        final ListView lv = (ListView) mainActivity.findViewById(R.id.list_view_main);
        final ContactsByNameListAdapter contactsByNameListAdapter =
                new ContactsByNameListAdapter(mainActivity, mainActivity.getContacts());
        contactsByNameListAdapter.sort(NAME_SORTER);
        lv.setAdapter(contactsByNameListAdapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            /**
             * {@inheritDoc}
             */
            @Override
            public void onItemClick(final AdapterView<?> parent, final View view, final int index, final long id) {
                mainActivity.openContact(index);
            }

        });

    }

    /**
     * Sorts contacts by key type
     */
    private void sortOnKeyType() {
        final ExpandableListView ev = (ExpandableListView) mainActivity.findViewById(R.id.expandable_contact_list_by_key_type);
        final ContactsByKeyTypeListAdapter contactsByKeyTypeListAdapter =
                new ContactsByKeyTypeListAdapter(mainActivity, getKeyTypes(), mainActivity.getContacts());
        ev.setAdapter(contactsByKeyTypeListAdapter);
        ev.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            /**
             * {@inheritDoc}
             */
            @Override
            public void onItemClick(final AdapterView<?> parent, final View view, final int index, final long id) {
                mainActivity.openContact(index);
            }

        });
    }

}