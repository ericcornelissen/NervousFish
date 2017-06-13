package com.nervousfish.nervousfish.activities;


import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.support.v7.app.ActionBar;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.view.MenuItem;

import com.nervousfish.nervousfish.ConstantKeywords;
import com.nervousfish.nervousfish.R;
import com.nervousfish.nervousfish.data_objects.Contact;
import com.nervousfish.nervousfish.data_objects.Profile;
import com.nervousfish.nervousfish.service_locator.IServiceLocator;
import com.nervousfish.nervousfish.service_locator.NervousFish;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import edu.umd.cs.findbugs.annotations.SuppressWarnings;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. This
 * is the place where your profile is changed.
 */
@SuppressFBWarnings(value = "ST_WRITE_TO_STATIC_FROM_INSTANCE_METHOD")
//Because of the structure with the static methods, a static attribute has to be set.
@SuppressWarnings({"checkstyle:AnonInnerLength", "PMD.AvoidUsingVolatile"})
//1. In this class large anonymous classes are needed. It does not infer with readability.
//2. The volatile identifier is needed because this class uses static methods, which are essential.
public final class SettingsActivity extends AAppCompatPreferenceActivity {

    private static final Logger LOGGER = LoggerFactory.getLogger("SettingsActivity");
    private static boolean firstLoad = true;
    private static volatile IServiceLocator serviceLocator;

    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */
    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(final Preference preference, final Object value) {
            LOGGER.info("Preference changed");
            final String stringValue = value.toString();

            if (preference.getKey().equals(ConstantKeywords.DISPLAY_NAME)) {
                LOGGER.info("Preference changed at the display name");
                updateDisplayName(preference, stringValue);
                return true;
            } else if (preference instanceof ListPreference) {
                LOGGER.info("Preference changed for a ListPreference");
                updateListPreference(preference, stringValue);
                return true;
            } else {
                LOGGER.info("Preference changed which is not a ListPreference, and not the display name");
                // For all other preferences, set the summary to the value's
                // simple string representation.
                preference.setSummary(stringValue);
                return true;
            }
        }

        /**
         * If the key is display_name this method is called to update the summary
         * and the Profile in the database.
         *
         * @param preference The preference which is changed
         * @param stringValue The string value which is new
         */
        private void updateDisplayName(final Preference preference, final String stringValue) {
            if (firstLoad) {
                firstLoad = false;

                try {
                    preference.setSummary(serviceLocator.getDatabase().getProfile().getName());
                    return;
                } catch (IOException e) {
                    LOGGER.error("Couldn't get profiles from database while loading for the first time", e);
                }

                preference.setSummary(stringValue);
            } else {
                try {
                    LOGGER.info("Updating profile name");
                    final Profile profile = serviceLocator.getDatabase().getProfile();
                    final Contact contact = new Contact(stringValue, profile.getContact().getKeys());
                    serviceLocator.getDatabase().updateProfile(new Profile(contact, profile.getKeyPairs()));
                } catch (IOException e) {
                    LOGGER.error("Couldn't get profiles from database", e);
                }

                preference.setSummary(stringValue);
            }
        }

        /**
         * If the preference is a list preference this method is called to update the summary.
         *
         * @param preference The preference which is changed
         * @param stringValue The string value which is new
         */
        private void updateListPreference(final Preference preference, final String stringValue) {
            // For list preferences, look up the correct display value in
            // the preference's 'entries' list.
            final ListPreference listPreference = (ListPreference) preference;
            final int index = listPreference.findIndexOfValue(stringValue);

            // Set the summary to reflect the new value.
            preference.setSummary(
                    index >= 0
                            ? listPreference.getEntries()[index]
                            : "");
        }
    };

    /**
     * Binds a preference's summary to its value. More specifically, when the
     * preference's value is changed, its summary (line of text below the
     * preference title) is updated to reflect the value. The summary is also
     * immediately updated upon calling this method. The exact display format is
     * dependent on the type of preference.
     *
     * @see #sBindPreferenceSummaryToValueListener
     */
    private static void bindPreferenceSummaryToValue(final Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        // Trigger the listener immediately with the preference's
        // current value.
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActionBar();

        if (serviceLocator == null) {
            serviceLocator = NervousFish.getServiceLocator();
        }

        try {
            PreferenceManager
                    .getDefaultSharedPreferences(this)
                    .edit()
                    .putString(ConstantKeywords.DISPLAY_NAME, serviceLocator.getDatabase().getProfile().getName())
                    .apply();
        } catch (IOException e) {
            LOGGER.error("Couldn't get profiles from database at the onCreate", e);
        }
        LOGGER.info("SettingsActivity created");
    }

    @Override
    public void onHeaderClick(final Header header, final int position) {
        super.onHeaderClick(header, position);
        if (header.id == R.id.key_management_header) {
            final Intent intent = new Intent(this, KeyManagementActivity.class);
            this.startActivity(intent);
        }
    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    private void setupActionBar() {
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        final int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void onBuildHeaders(final List<Header> target) {
        loadHeadersFromResource(R.xml.pref_headers, target);
    }

    /**
     * This method stops fragment injection in malicious applications.
     * Make sure to deny any unknown fragments here.
     *
     * @param fragmentName The name of the fragment
     * @return A boolean which is true when the fragment is valid
     */
    protected boolean isValidFragment(final String fragmentName) {
        return PreferenceFragment.class.getName().equals(fragmentName)
                || GeneralPreferenceFragment.class.getName().equals(fragmentName)
                || ProfilePreferenceFragment.class.getName().equals(fragmentName);
    }

    /**
     * This fragment shows general preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static final class GeneralPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_general);
            setHasOptionsMenu(true);

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            bindPreferenceSummaryToValue(findPreference(ConstantKeywords.CHOOSE_VERIFICATION_PREFERENCE));
        }

        @Override
        public boolean onOptionsItemSelected(final MenuItem item) {
            final int id = item.getItemId();
            if (id == android.R.id.home) {
                final Activity activity = this.getActivity();
                final Intent intent = new Intent(activity, SettingsActivity.class); // Needed bacause we're in a Fragment
                startActivity(intent);
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }

    /**
     * This fragment shows profile settings only.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static final class ProfilePreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_profile);
            setHasOptionsMenu(true);

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            bindPreferenceSummaryToValue(findPreference(ConstantKeywords.DISPLAY_NAME));
        }

        @Override
        public boolean onOptionsItemSelected(final MenuItem item) {
            final int id = item.getItemId();
            if (id == android.R.id.home) {
                final Activity activity = this.getActivity();
                final Intent intent = new Intent(activity, SettingsActivity.class); // Needed bacause we're in a Fragment
                startActivity(intent);
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }
}
