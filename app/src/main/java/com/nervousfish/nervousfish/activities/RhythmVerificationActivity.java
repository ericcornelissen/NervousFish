package com.nervousfish.nervousfish.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.nervousfish.nervousfish.ConstantKeywords;
import com.nervousfish.nervousfish.R;
import com.nervousfish.nervousfish.data_objects.KeyPair;
import com.nervousfish.nervousfish.data_objects.Profile;
import com.nervousfish.nervousfish.exceptions.EncryptionException;
import com.nervousfish.nervousfish.exceptions.UnknownIntervalException;
import com.nervousfish.nervousfish.modules.pairing.ByteWrapper;
import com.nervousfish.nervousfish.modules.pairing.IBluetoothHandler;
import com.nervousfish.nervousfish.modules.pairing.events.NewDataReceivedEvent;
import com.nervousfish.nervousfish.service_locator.IServiceLocator;
import com.nervousfish.nervousfish.service_locator.NervousFish;

import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * The RhythmVerificationActivity is an Activity where you can tap a sequence.
 */
@SuppressWarnings({"PMD.LooseCoupling", "InstanceVariableMayNotBeInitialized", "PMD.ExcessiveImports"})
// 1) List is cast to an ArrayList, but that is needed to put in an intent.
// 2) We cannot pre-initialize for example the buttons because activities don't have a constructor
// 3) We cannot easily reduce the number of imports because most of them are plain data objects to events
public final class RhythmVerificationActivity extends AppCompatActivity {

    private static final Logger LOGGER = LoggerFactory.getLogger("RhythmVerificationActivity");
    private static final int MINIMUM_TAPS = 3;
    private static final int DECIMAL_SIZE = 10;
    private static final int MAX_RHYTHM_ENCODING = 10;

    private Button startButton;
    private Button stopButton;
    private Button doneButton;
    private List<Timestamp> taps;
    private IServiceLocator serviceLocator;
    private IBluetoothHandler bluetoothHandler;
    private byte[] dataReceived;

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_rhythm_create);

        this.serviceLocator = NervousFish.getServiceLocator();
        this.bluetoothHandler = this.serviceLocator.getBluetoothHandler();

        this.startButton = (Button) this.findViewById(R.id.start_recording_button);
        this.stopButton = (Button) this.findViewById(R.id.stop_recording_button);
        this.doneButton = (Button) this.findViewById(R.id.done_tapping_button);

        final Intent intent = this.getIntent();
        final Boolean rhythmFailure = (Boolean) intent.getSerializableExtra(ConstantKeywords.TAPPING_FAILURE);
        if (rhythmFailure != null && rhythmFailure) {
            new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText(this.getString(R.string.not_the_same_rhythm_title))
                    .setContentText(this.getString(R.string.not_the_same_rhythm_explanation))
                    .setConfirmText(this.getString(R.string.dialog_ok))
                    .show();
        }

        LOGGER.info("Activity created");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onStart() {
        super.onStart();
        this.serviceLocator.registerToEventBus(this);

        LOGGER.info("Activity started");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onStop() {
        super.onStop();
        this.serviceLocator.unregisterFromEventBus(this);

        LOGGER.info("Activity stopped");
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
     * Gets triggered when the tap screen is clicked.
     *
     * @param v - the {@link View} clicked
     */
    public void onTapClick(final View v) {
        LOGGER.info("Tapped");
        Validate.notNull(v);
        if (this.taps != null && this.startButton.getVisibility() == View.GONE) {
            this.taps.add(new Timestamp(System.currentTimeMillis()));
        }
    }

    /**
     * Gets triggered when the done button is clicked.
     *
     * @param view - the {@link View} clicked
     */
    public void onDoneCreatingRhythmClick(final View view) {
        LOGGER.info("Done tapping button clicked");
        Validate.notNull(view);

        final Profile profile = this.serviceLocator.getDatabase().getProfile();
        final KeyPair keyPair = profile.getKeyPairs().get(0);

        LOGGER.info("Sending my profile with name: {}, public key: {}", profile.getName(), keyPair.getPublicKey());
        final long key = new RhythmVerificationActivity.KMeansClusterHelper().getEncryptionKey(this.taps);
        try {
            this.bluetoothHandler.send(profile.getContact(), key);
        } catch (final BadPaddingException | IllegalBlockSizeException e) {
            LOGGER.error("Could not encrypt the contact", e);
            throw new EncryptionException(e);
        }

        final Intent intent = new Intent(this, WaitActivity.class);
        intent.putExtra(ConstantKeywords.WAIT_MESSAGE, this.getString(R.string.wait_message_partner_rhythm_tapping));
        intent.putExtra(ConstantKeywords.DATA_RECEIVED, this.dataReceived);
        intent.putExtra(ConstantKeywords.KEY, key);
        intent.putExtra(ConstantKeywords.CLASS_STARTED_FROM, this.getClass());
        this.startActivityForResult(intent, ConstantKeywords.START_RHYTHM_REQUEST_CODE);
    }

    /**
     * Gets triggered when the start recording button is clicked.
     *
     * @param v - the {@link View} clicked
     */
    public void onStartRecordingClick(final View v) {
        LOGGER.info("Start Recording clicked");
        Validate.notNull(v);
        this.taps = new ArrayList<>();
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
        Validate.notNull(v);
        this.startButton.setVisibility(View.VISIBLE);
        this.stopButton.setVisibility(View.GONE);
        if (this.taps.size() < MINIMUM_TAPS) {
            this.taps.clear();
            this.doneButton.setVisibility(View.GONE);
            new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText(this.getString(R.string.too_few_taps_title))
                    .setContentText(this.getString(R.string.too_few_taps_description))
                    .setConfirmText(this.getString(R.string.dialog_ok))
                    .show();
        } else {
            this.doneButton.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Called when new bytes are received.
     *
     * @param event Contains the bytes that are received
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNewDataReceivedEvent(final NewDataReceivedEvent event) {
        LOGGER.info("onNewEncryptedBytesReceivedEvent called");
        Validate.notNull(event);

        try {
            this.dataReceived = ((ByteWrapper) event.getData()).getBytes();
        } catch (final ClassCastException e) {
            LOGGER.error("Something else than a ByteWrapper is received: ", e);
        }
    }

    /**
     * Denotes the cluster to which a certain tap interval belongs
     */
    private enum Cluster {
        SHORT,
        LONG
    }

    /**
     * Executes K-means++ clustering on the intervals to divide them into a short and long cluster
     */
    private static final class KMeansClusterHelper {

        private List<Long> intervals;
        private List<Long> clusterCenter1;
        private List<Long> clusterCenter2;

        /**
         * Get a list of the time between the taps (= intervals)
         *
         * @param taps The taps that have obviously intervals in between
         * @return A list containing the time between the taps
         */
        private static List<Long> getIntervals(final List<Timestamp> taps) {
            assert taps != null;
            assert taps.size() >= 2;
            final List<Long> intervals = new ArrayList<>(taps.size() - 1);
            for (int i = 0; i < taps.size() - 1; i++) {
                intervals.add(taps.get(i + 1).getTime() - taps.get(i).getTime());
            }
            return intervals;
        }

        /**
         * Returns the unique key that corresponds to the taps specified.
         * The key is a binary number.
         * Short interval = 0, long interval = 1
         * If the user taps a short - long - short - long interval, then the returned key is
         * 1010 (because the numbers are always written from right to left
         *
         * @param taps The taps that should be encoded to a key
         * @return The unique key that corresponds to the taps
         */
        long getEncryptionKey(final List<Timestamp> taps) {
            Validate.noNullElements(taps);
            Validate.isTrue(taps.size() >= MINIMUM_TAPS);
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

        /**
         * @return The mean length of the intervals in the "Short" cluster
         */
        private long makeAndReturnFirstClusterMean() {
            this.clusterCenter1.add(this.intervals.get(0));
            this.intervals.remove(0);
            return this.clusterCenter1.get(0);
        }

        /**
         * @return The mean length of the intervals in the "Long" cluster
         */
        private long makeAndReturnSecondClusterMean() {
            this.clusterCenter2.add(this.intervals.get(this.intervals.size() - 1));
            this.intervals.remove(this.intervals.size() - 1);
            return this.clusterCenter2.get(0);
        }

        /**
         * Adds the intervals whose length is closest to the "Short" or "Long" cluster to the "Short" or "Long" cluster
         *
         * @param centerMean1 The mean of the length of the intervals in the "Short" cluster
         * @param centerMean2 The mean of the length of the intervals in the "Long" cluster
         */
        private void addClosestTimestampToCluster(final long centerMean1, final long centerMean2) {
            assert centerMean1 >= 0;
            assert centerMean2 >= 0;

            final ImmutablePair<RhythmVerificationActivity.Cluster, Long> closestPoint = this.searchClosestPoint(centerMean1, centerMean2);
            if (closestPoint.getLeft() == RhythmVerificationActivity.Cluster.SHORT) {
                this.clusterCenter1.add(closestPoint.getRight());
            } else if (closestPoint.getLeft() == RhythmVerificationActivity.Cluster.LONG) {
                this.clusterCenter2.add(closestPoint.getRight());
            } else {
                LOGGER.error("A timestamp does neither belong to the short or long interval");
                throw new UnknownIntervalException("The timestamp does neither long to the short or long interval");
            }
            this.intervals.remove(closestPoint.getRight());
        }

        /**
         * Returns the interval whose length is closest to the "Short" or "Long" cluster to the "Short" or "Long" cluster
         *
         * @param centerMean1 The mean of the length of the intervals in the "Short" cluster
         * @param centerMean2 The mean of the length of the intervals in the "Long" cluster
         * @return A pair of which the left value denotes the cluster the interval belongs to and the right value denotes the length of the cluster
         */
        private ImmutablePair<RhythmVerificationActivity.Cluster, Long> searchClosestPoint(final long centerMean1, final long centerMean2) {
            assert centerMean1 >= 0;
            assert centerMean2 >= 0;

            Long closestPoint = null;
            long distance = Long.MAX_VALUE;
            RhythmVerificationActivity.Cluster targetCluster = null;
            for (final Long interval : this.intervals) {
                final long dist1 = interval - centerMean1;
                if (dist1 < distance) {
                    closestPoint = interval;
                    distance = dist1;
                    targetCluster = RhythmVerificationActivity.Cluster.SHORT;
                }
                final long dist2 = centerMean2 - interval;
                if (dist2 < distance) {
                    closestPoint = interval;
                    distance = dist2;
                    targetCluster = RhythmVerificationActivity.Cluster.LONG;
                }
            }
            return new ImmutablePair<>(targetCluster, closestPoint);
        }

        /**
         * @return A tuple of which the left value is the mean length of the intervals of the "Short" cluster
         * and the right value is the mean length of the intervals of the "Long" cluster
         */
        private ImmutablePair<Long, Long> recalculateClusterCenters() {
            long clusterCenterMean1 = 0L;
            for (final Long interval : this.clusterCenter1) {
                clusterCenterMean1 += interval;
            }

            long clusterCenterMean2 = 0L;
            for (final Long interval : this.clusterCenter2) {
                clusterCenterMean2 += interval;
            }

            clusterCenterMean1 /= this.clusterCenter1.size();
            clusterCenterMean2 /= this.clusterCenter2.size();

            return new ImmutablePair<>(clusterCenterMean1, clusterCenterMean2);
        }

        /**
         * Generates a key based on the rhythm that was tapped by using powers of 2.
         * Short = 0
         * Short, Long = 2
         * Short, Long, Long = 6
         * Long, Long, Long = 7
         *
         * @param breakpoint The boundary between a short and long interval
         * @return The key as a Long
         */
        private long generateKey(final long breakpoint) {
            assert breakpoint >= 0;

            long key = 0;
            int counter = 0;
            for (final long interval : this.intervals) {
                if (interval <= breakpoint) {
                    counter++;
                } else {
                    key += StrictMath.pow(2, counter);
                    counter++;
                }
            }
            long tmpIntervalSize = this.intervals.size();
            int startValue = 1;
            while (tmpIntervalSize >= startValue * DECIMAL_SIZE) {
                startValue *= DECIMAL_SIZE;
            }
            for (int i = startValue; i >= 1; i /= DECIMAL_SIZE) {
                final long l = (long) Math.floor((double) tmpIntervalSize / i) * (long) StrictMath.pow(DECIMAL_SIZE, MAX_RHYTHM_ENCODING) * i;
                key += l;
                tmpIntervalSize -= i * Math.floor(tmpIntervalSize / i);
            }
            return key;
        }

        /**
         * Determines a breakpoint to determine which intervals are short and which are long
         *
         * @return The breakpoint
         */
        private long getBreakpoint() {
            final long lastShortInterval = this.clusterCenter1.get(this.clusterCenter1.size() - 1);
            final long firstLongInterval = this.clusterCenter2.get(0);
            return (firstLongInterval - lastShortInterval) / 2 + lastShortInterval;
        }

    }

}
