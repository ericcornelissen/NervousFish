package com.nervousfish.nervousfish.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import com.nervousfish.nervousfish.ConstantKeywords;
import com.nervousfish.nervousfish.R;
import com.nervousfish.nervousfish.data_objects.Contact;
import com.nervousfish.nervousfish.data_objects.Profile;
import com.nervousfish.nervousfish.data_objects.SimpleKey;
import com.nervousfish.nervousfish.data_objects.tap.SingleTap;
import com.nervousfish.nervousfish.modules.database.IDatabase;
import com.nervousfish.nervousfish.modules.pairing.IBluetoothHandler;
import com.nervousfish.nervousfish.modules.pairing.events.NewDataReceivedEvent;
import com.nervousfish.nervousfish.service_locator.IServiceLocator;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * The RhythmCreateActivity is an Activity where you can tap a sequence.
 */
@SuppressFBWarnings(value = "BC_BAD_CAST_TO_CONCRETE_COLLECTION")
//List is cast to an ArrayList, but that is needed to put in an intent.
@SuppressWarnings({"PMD.LooseCoupling", "InstanceVariableMayNotBeInitialized"})
//We don't want to use 'List' but the implementation 'ArrayList' to prevent errors.
public final class RhythmCreateActivity extends AppCompatActivity {
    private static final Logger LOGGER = LoggerFactory.getLogger("RhythmCreateActivity");
    private Button startButton;
    private Button stopButton;
    private Button doneButton;
    private ArrayList<SingleTap> taps;
    private IServiceLocator serviceLocator;
    private IDatabase database;
    private IBluetoothHandler bluetoothHandler;
    private Contact dataReceived;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_rhythm_create);
        final Toolbar toolbar = (Toolbar) this.findViewById(R.id.toolbar_create_rhythm);
        this.setSupportActionBar(toolbar);

        final Intent intent = this.getIntent();
        this.serviceLocator = (IServiceLocator) intent.getSerializableExtra(ConstantKeywords.SERVICE_LOCATOR);
        this.database = this.serviceLocator.getDatabase();
        this.bluetoothHandler = this.serviceLocator.getBluetoothHandler();

        this.startButton = (Button) this.findViewById(R.id.start_recording_button);
        this.stopButton = (Button) this.findViewById(R.id.stop_recording_button);
        this.doneButton = (Button) this.findViewById(R.id.done_tapping_button);

        LOGGER.info("RhythmActivity started");
    }

    /**
     * Gets triggered when the tap screen is clicked.
     *
     * @param v - the {@link View} clicked
     */
    public void onTapClick(final View v) {
        LOGGER.info("Tapped");
        if (this.taps != null && this.startButton.getVisibility() == View.GONE) {
            this.taps.add(new SingleTap(new Timestamp(System.currentTimeMillis())));
        }
    }

    /**
     * Gets triggered when the done button is clicked.
     *
     * @param v - the {@link View} clicked
     */
    public void onDoneCreatingRhythmClick(final View v) {
        LOGGER.info("Done tapping button clicked");
        try {
            final Profile myProfile = this.database.getProfiles().get(0);

            LOGGER.info("Sending my profile with name: {}, public key: {}", myProfile.getName(), myProfile.getPublicKey());
            final Contact myProfileAsContact = new Contact(myProfile.getName(), new SimpleKey("simplekey", "73890ien"));
            final int encryptionKey = new KMeansClusterHelper().getEncryptionKey(this.taps);
            this.bluetoothHandler.send(myProfileAsContact, encryptionKey);
        } catch (final IOException e) {
            LOGGER.error("Could not send my contact to other device ", e);
        }
        final Intent intent = new Intent(this, WaitActivity.class);
        intent.putExtra(ConstantKeywords.SERVICE_LOCATOR, this.serviceLocator);
        intent.putExtra(ConstantKeywords.WAIT_MESSAGE, this.getString(R.string.wait_message_partner_rhythm_tapping));
        intent.putExtra(ConstantKeywords.DATA_RECEIVED, this.dataReceived);
        intent.putExtra(ConstantKeywords.TAP_DATA, this.taps);
        this.startActivityForResult(intent, ConstantKeywords.START_RHYTHM_REQUEST_CODE);
    }

    /**
     * Gets triggered when the start recording button is clicked.
     *
     * @param v - the {@link View} clicked
     */

    public void onStartRecordingClick(final View v) {
        LOGGER.info("Start Recording clicked");
        this.taps = new ArrayList<>(10);
        this.startButton.setVisibility(View.GONE);
        this.stopButton.setVisibility(View.VISIBLE);
        this.doneButton.setVisibility(View.GONE);
    }

    /**
     * Gets triggered when the stop recording button is clicked.
     *
     * @param v - the {@link View} clicked
     */
    public void onStopRecordingClick(final View v) {
        LOGGER.info("Stop Recording clicked");
        this.startButton.setVisibility(View.VISIBLE);
        this.stopButton.setVisibility(View.GONE);
        this.doneButton.setVisibility(View.VISIBLE);
        if (this.taps.size() < 3) {
            new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText(this.getString(R.string.too_few_taps_title))
                    .setContentText(this.getString(R.string.too_few_taps_description))
                    .setConfirmText(this.getString(R.string.try_again))
                    .setConfirmClickListener(sweetAlertDialog -> {
                        this.taps.clear();
                        this.doneButton.setVisibility(View.GONE);
                    })
                    .show();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == ConstantKeywords.DONE_PAIRING_RESULT_CODE) {
            this.setResult(ConstantKeywords.DONE_PAIRING_RESULT_CODE);
            this.finish();
        } else if (resultCode == ConstantKeywords.CANCEL_PAIRING_RESULT_CODE) {
            this.setResult(ConstantKeywords.CANCEL_PAIRING_RESULT_CODE);
            this.finish();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onStart() {
        super.onStart();
        this.serviceLocator.registerToEventBus(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onStop() {
        this.serviceLocator.unregisterFromEventBus(this);
        super.onStop();
    }

    /**
     * Called when a new data is received.
     *
     * @param event Contains additional data about the event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNewDataReceivedEvent(final NewDataReceivedEvent event) {
        LOGGER.info("onNewDataReceivedEvent called");
        if (event.getClazz().equals(Contact.class)) {
            final Contact contact = (Contact) event.getData();
            try {
                LOGGER.info("Adding contact to database...");
                this.database.addContact(contact);
            } catch (IOException | IllegalArgumentException e) {
                LOGGER.error("Couldn't get contacts from database", e);
            }

            //This needs to be outside of the try catch block
            this.dataReceived = contact;
        }
    }

    private enum Cluster {
        SHORT,
        LONG
    }

    static final class KMeansClusterHelper {
        private List<Long> intervals;
        private List<Long> clusterCenter1;
        private List<Long> clusterCenter2;

        private KMeansClusterHelper() {
            // Prevent instantiation from outside the package
        }

        int getEncryptionKey(final List<SingleTap> taps) {
            this.clusterCenter1 = new ArrayList<>(taps.size());
            this.clusterCenter2 = new ArrayList<>(taps.size());
            this.intervals = getIntervals(taps);
            long clusterCenterMean1 = this.makeAndReturnFirstClusterMean();
            long clusterCenterMean2 = this.makeAndReturnSecondClusterMean();

            while (!this.intervals.isEmpty()) {
                this.addClosestTimestampToCluster(clusterCenterMean1, clusterCenterMean2);
                final ImmutablePair<Long, Long> newClusterCenters = this.recalculateClusterCenters();
                clusterCenterMean1 = newClusterCenters.getLeft();
                clusterCenterMean2 = newClusterCenters.getRight();
            }

            final long breakpoint = this.getBreakpoint();
            this.intervals = getIntervals(taps);
            return this.generateKey(breakpoint);
        }

        private static List<Long> getIntervals(final List<SingleTap> taps) {
            final List<Long> intervals = new ArrayList<>(taps.size() - 1);
            for (int i = 0; i < taps.size() - 1; i++) {
                intervals.add(taps.get(i + 1).getTimestamp().getTime() - taps.get(i).getTimestamp().getTime());
            }
            return intervals;
        }

        private long makeAndReturnFirstClusterMean() {
            this.clusterCenter1.add(this.intervals.get(0));
            this.intervals.remove(0);
            return this.clusterCenter1.get(0);
        }

        private long makeAndReturnSecondClusterMean() {
            this.clusterCenter2.add(this.intervals.get(this.intervals.size() - 1));
            this.intervals.remove(this.intervals.size() - 1);
            return this.clusterCenter2.get(0);
        }

        private void addClosestTimestampToCluster(final long centerMean1, final long centerMean2) {
            ImmutablePair<Cluster, Long> closestPoint = this.searchClosestPoint(centerMean1, centerMean2);
            if (closestPoint.getLeft() == Cluster.SHORT) {
                this.clusterCenter1.add(closestPoint.getRight());
            } else if (closestPoint.getLeft() == Cluster.LONG) {
                this.clusterCenter2.add(closestPoint.getRight());
            } else {
                LOGGER.error("A timestamp does neither belong to the short or long timestamp");
                throw new RuntimeException("This cannot happen");
            }
            this.intervals.remove(closestPoint.getRight());
        }

        private ImmutablePair<Cluster, Long> searchClosestPoint(final long centerMean1, final long centerMean2) {
            Long closestPoint = null;
            long distance = Long.MAX_VALUE;
            Cluster targetCluster = null;
            for (final Long interval : this.intervals) {
                final long dist1 = interval - centerMean1;
                if (dist1 < distance) {
                    closestPoint = interval;
                    distance = dist1;
                    targetCluster = Cluster.SHORT;
                }
                final long dist2 = centerMean2 - interval;
                if (dist2 < distance) {
                    closestPoint = interval;
                    distance = dist2;
                    targetCluster = Cluster.LONG;
                }
            }
            return new ImmutablePair<>(targetCluster, closestPoint);
        }

        private ImmutablePair<Long, Long> recalculateClusterCenters() {
            long clusterCenterMean1 = 0L;
            long clusterCenterMean2 = 0L;

            for (Long interval : this.clusterCenter1) {
                clusterCenterMean1 += interval;
            }
            for (Long interval : this.clusterCenter2) {
                clusterCenterMean2 += interval;
            }

            clusterCenterMean1 /= this.clusterCenter1.size();
            clusterCenterMean2 /= this.clusterCenter2.size();

            return new ImmutablePair<>(clusterCenterMean1, clusterCenterMean2);
        }

        private int generateKey(final long breakpoint) {
            int key = 0;
            int counter = 0;
            for (final long interval : this.intervals) {
                if (interval < breakpoint) {
                    counter++;
                } else {
                    key += Math.pow(2, counter);
                    counter++;
                }
            }
            return key;
        }

        private long getBreakpoint() {
            final long lastShortInterval = this.clusterCenter1.get(this.clusterCenter1.size() - 1);
            final long firstLongInterval = this.clusterCenter2.get(0);
            return (firstLongInterval - lastShortInterval) / 2 + lastShortInterval;
        }
    }
}
