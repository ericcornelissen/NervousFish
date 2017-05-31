package com.nervousfish.nervousfish.activities;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.nervousfish.nervousfish.ConstantKeywords;
import com.nervousfish.nervousfish.R;
import com.nervousfish.nervousfish.service_locator.IServiceLocator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Activity used to select a verification method.
 */
public class SelectVerificationMethodActivity extends AppCompatActivity {

    private static final Logger LOGGER = LoggerFactory.getLogger("SelectVerificationMethodActivity");

    private final List<Pair<String, String>> methods = new ArrayList<>();

    private IServiceLocator serviceLocator;

    /**
     * Creates a new {@link SelectVerificationMethodActivity} activity.
     *
     * @param savedInstanceState state previous instance of this activity
     */
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_select_verification_method);

        final Intent intent = this.getIntent();
        this.serviceLocator = (IServiceLocator) intent.getSerializableExtra(ConstantKeywords.SERVICE_LOCATOR);

        // Add possible verification methods
        final Pair<String, String> visual = new Pair<>(
                this.getString(R.string.select_visual_verification), ConstantKeywords.VISUAL_VERIFICATION_METHOD);
        final Pair<String, String> rhythm = new Pair<>(
                this.getString(R.string.select_rhythm_verification), ConstantKeywords.RHYTHM_VERIFICATION_METHOD);
        this.methods.add(visual);
        this.methods.add(rhythm);

        // Set verification methods in the ListView
        final ListView listView = (ListView) this.findViewById(R.id.verification_method_list);
        listView.setAdapter(new PairAdapter(R.layout.verification_list_entry, this.methods));

        LOGGER.info("Activity created");
    }

    /**
     * Open the correct activity given a verification method.
     *
     * @param view The view on which the click was performed
     */
    public void openVerificationMethod(final View view) {
        final Intent intent = new Intent();
        intent.putExtra(ConstantKeywords.SERVICE_LOCATOR, this.serviceLocator);

        final String method = view.getContentDescription().toString();
        switch (method) {
            case ConstantKeywords.VISUAL_VERIFICATION_METHOD:
                LOGGER.error("Selected visual verification method, opening activity");
                intent.setComponent(new ComponentName(this, VisualVerificationActivity.class));
                this.startActivity(intent);
                break;
            case ConstantKeywords.RHYTHM_VERIFICATION_METHOD:
                // TODO: open correct (Rhythm) activity
                LOGGER.error("Selected rhythm verification method, opening activity");
                intent.setComponent(new ComponentName(this, WaitForSlaveActivity.class));
                this.startActivity(intent);
                break;
            default:
                LOGGER.error("unknown verification method selected: " + method);
                intent.setComponent(new ComponentName(this, MainActivity.class));
                this.startActivity(intent);
                break;
        }
    }

    private static class PairAdapter extends BaseAdapter {

        private final int layout;
        private final List<Pair<String, String>> values;

        /**
         * Create a new adapter for a pair of values. The first value is the textual value of a
         * {@link TextView} and the second value is the description of that {@link TextView}.
         *
         * @param layout The layout to use for the adapter.
         * @param values The {@link List} of values.
         */
        PairAdapter(final int layout, final List<Pair<String, String>> values) {
            super();

            this.layout = layout;
            this.values = values;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int getCount() {
            return this.values.size();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Object getItem(final int position) {
            return this.values.get(position);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public long getItemId(final int position) {
            return 0;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public View getView(final int position, final View view, final ViewGroup parent) {
            final LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            final TextView textView = (TextView) layoutInflater.inflate(this.layout, parent, false);

            final Pair<String, String> value = this.values.get(position);
            textView.setText(value.first);
            textView.setContentDescription(value.second);

            return textView;
        }

    }

}
