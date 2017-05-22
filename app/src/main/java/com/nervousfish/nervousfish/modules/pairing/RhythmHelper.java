package com.nervousfish.nervousfish.modules.pairing;

import com.nervousfish.nervousfish.data_objects.tap.ATapData;
import com.nervousfish.nervousfish.data_objects.tap.SingleTap;
import com.nervousfish.nervousfish.util.CircularList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Provides some helper function for verifying the identity of the user by using a rhythm
 */
public final class RhythmHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger("RhythmHelper");
    private final List<SingleTap> masterData;
    private final CircularList<SingleTap> slaveData;

    /**
     * Constructs a new RhythmHelper
     * @param masterData The rhythm tapped by the master
     */
    RhythmHelper(final List<SingleTap> masterData) {
        this.masterData = masterData;
        this.slaveData = new CircularList<>(this.masterData.size());
    }

    /**
     * Adds a single tap from the slave to the queue
     * @param data The single tap
     */
    public void addSlaveData(final SingleTap data) {
        this.slaveData.add(data);
    }

    /**
     * Adds an ordered ascending list by timestamp of taps from the slave to the queue
     * @param data The list of taps
     */
    public void setSlaveData(final List<SingleTap> data) {
        for (final SingleTap elem : data) {
            addSlaveData(elem);
        }
    }

    /**
     * Checks if the rhythm of the master matches the rhythm of the slave.
     *
     * @param power The extent to which the taps should be at exactly the same moments
     * @param threshold The maximum difference in millisecond in order to be a match
     * @return True if the slave rhythm matches the master rhythm
     */
    public boolean isMatch(final int power, final int threshold) {
        if (slaveData.size() < masterData.size()) {
            return false;
        }

        /* Denotes the accumulated difference between the times at which the slave and the
           master tapped on their screen */
        double dist = 0d;
        final long offset = slaveData.get(0).getTimestamp().getTime();
        for (int i = 0; i < masterData.size(); i++) {
            final ATapData masterSample = masterData.get(i);
            final ATapData slaveSample = slaveData.get(i);
            final long masterTimeMilliseconds = masterSample.getTimestamp().getTime();
            final long slaveTimeMilliseconds = slaveSample.getTimestamp().getTime() - offset;
            final long difference = Math.abs(slaveTimeMilliseconds - masterTimeMilliseconds);
            dist += Math.pow(difference, power);
        }
        dist /= masterData.size();
        LOGGER.info("distance = " + Double.toString(dist));
        return dist < threshold;
    }
}
