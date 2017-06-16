package com.nervousfish.nervousfish.activities;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.ViewFlipper;

import com.nervousfish.nervousfish.R;
import com.nervousfish.nervousfish.data_objects.Contact;
import com.nervousfish.nervousfish.list_adapters.ContactsByKeyTypeListAdapter;
import com.nervousfish.nervousfish.list_adapters.ContactsByNameListAdapter;

import java.util.Comparator;

/**
 * Is used to sort the list in the MainActivity
 */

final class MainActivitySorter {
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
        this.currentSorting++;
        if (this.currentSorting >= NUMBER_OF_SORTING_MODES) {
            this.currentSorting = 0;
        }
        final ViewFlipper flipper = (ViewFlipper) this.mainActivity.findViewById(R.id.view_flipper_sorter_main);
        flipper.showNext();
        switch (this.currentSorting) {
            case SORT_BY_NAME:
                this.sortOnName();
                break;
            case SORT_BY_KEY_TYPE:
                this.sortOnKeyType();
                break;
            default:
                break;
        }
    }


    /**
     * Sorts contacts by name
     */
    void sortOnName() {
        final ListView lv = (ListView) this.mainActivity.findViewById(R.id.list_view_main);
        final ContactsByNameListAdapter contactsByNameListAdapter =
                new ContactsByNameListAdapter(this.mainActivity, this.mainActivity.getContacts());
        contactsByNameListAdapter.sort(NAME_SORTER);
        lv.setAdapter(contactsByNameListAdapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            /**
             * {@inheritDoc}
             */
            @Override
            public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
                MainActivitySorter.this.mainActivity.openContact(position);
            }

        });

    }

    /**
     * Sorts contacts by key type
     */
    private void sortOnKeyType() {
        final ExpandableListView ev = (ExpandableListView) this.mainActivity.findViewById(R.id.expandable_contact_list_by_key_type);
        final ContactsByKeyTypeListAdapter contactsByKeyTypeListAdapter =
                new ContactsByKeyTypeListAdapter(this.mainActivity, this.mainActivity.getContacts());
        ev.setAdapter(contactsByKeyTypeListAdapter);
        ev.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            /**
             * {@inheritDoc}
             */
            @Override
            public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
                MainActivitySorter.this.mainActivity.openContact(position);
            }

        });
    }

}
