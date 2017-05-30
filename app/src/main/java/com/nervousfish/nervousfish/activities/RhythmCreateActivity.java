package com.nervousfish.nervousfish.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import com.nervousfish.nervousfish.ConstantKeywords;
import com.nervousfish.nervousfish.R;
import com.nervousfish.nervousfish.data_objects.tap.SingleTap;
import com.nervousfish.nervousfish.service_locator.IServiceLocator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class RhythmCreateActivity extends AppCompatActivity {
    private static final Logger LOGGER = LoggerFactory.getLogger("RhythmCreateActivity");
    private List<SingleTap> tapCombination;
    private IServiceLocator serviceLocator;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rhythm_create);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_create_rhythm);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        final Intent intent = this.getIntent();
        this.serviceLocator = (IServiceLocator) intent.getSerializableExtra(ConstantKeywords.SERVICE_LOCATOR);
    }

    /**
     * Gets triggered when the tap screen is clicked.
     *
     * @param v - the {@link View} clicked
     */
    public void onTapClick(final View v) {
        LOGGER.info("Tapped");
        if (tapCombination != null) {
            tapCombination.add(new SingleTap(new Timestamp(System.currentTimeMillis())));
        }
    }

    /**
     * Gets triggered when the done button is clicked.
     *
     * @param v - the {@link View} clicked
     */
    public void onDoneClick(final View v) {
        LOGGER.info("Done tapping button clicked");
        final Intent intent = new Intent(this, WaitActivity.class);
        intent.putExtra(ConstantKeywords.SERVICE_LOCATOR, serviceLocator);
        intent.putExtra(ConstantKeywords.WAIT_MESSAGE, this.getString(R.string.wait_message_partner_rhythm_tapping));
        this.startActivity(intent);
    }

    /**
     * Gets triggered when the start recording button is clicked.
     *
     * @param v - the {@link View} clicked
     */
    public void onStartRecordingClick(final View v) {
        LOGGER.info("Start Recording clicked");
        tapCombination = new ArrayList<>();

        final Button startButton = (Button) this.findViewById(R.id.start_recording_button);
        startButton.setVisibility(View.GONE);

        final Button stopButton = (Button) this.findViewById(R.id.stop_recording_button);
        stopButton.setVisibility(View.VISIBLE);

        final Button doneButton = (Button) this.findViewById(R.id.done_tapping_button);
        doneButton.setVisibility(View.GONE);
    }

    /**
     * Gets triggered when the stop recording button is clicked.
     *
     * @param v - the {@link View} clicked
     */
    public void onStopRecordingClick(final View v) {
        LOGGER.info("Stop Recording clicked");
        final Button startButton = (Button) this.findViewById(R.id.start_recording_button);
        startButton.setVisibility(View.VISIBLE);

        final Button stopButton = (Button) this.findViewById(R.id.stop_recording_button);
        stopButton.setVisibility(View.GONE);

        final Button doneButton = (Button) this.findViewById(R.id.done_tapping_button);
        doneButton.setVisibility(View.VISIBLE);
    }
}
