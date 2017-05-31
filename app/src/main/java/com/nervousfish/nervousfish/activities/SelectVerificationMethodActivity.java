package com.nervousfish.nervousfish.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.nervousfish.nervousfish.R;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Activity used to select a verification method.
 */
public class SelectVerificationMethodActivity extends AppCompatActivity {

    private static final Logger LOGGER = LoggerFactory.getLogger("SelectVerificationMethodActivity");

    private final List<String> methods = new ArrayList<>();

    /**
     * Creates a new {@link SelectVerificationMethodActivity} activity.
     *
     * @param savedInstanceState state previous instance of this activity
     */
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.select_verification_method);

        // Add possible verification methods
        this.methods.add("Visual pattern");
        this.methods.add("Tab a rhythm");

        // Set verificatio methods in the ListView
        final ListView listView = (ListView) this.findViewById(R.id.verification_method_list);
        listView.setAdapter(new ArrayAdapter<>(this, R.layout.verification_list_entry, this.methods));

        LOGGER.info("Activity created");
    }

}
