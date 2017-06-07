package com.nervousfish.nervousfish.activities;

import com.nervousfish.nervousfish.data_objects.tap.SingleTap;

import org.junit.Test;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class KMeansClusterHelperTest {
    @Test
    public void getEncryptionKey() throws Exception {
        RhythmCreateActivity activity = new RhythmCreateActivity();
        RhythmCreateActivity.KMeansClusterHelper helper = activity.new KMeansClusterHelper();

        final List<SingleTap> taps = new ArrayList<>();
        taps.add(new SingleTap(new Timestamp(0)));
        taps.add(new SingleTap(new Timestamp(5)));
        taps.add(new SingleTap(new Timestamp(20)));
        taps.add(new SingleTap(new Timestamp(24)));
        taps.add(new SingleTap(new Timestamp(40)));

        int key = helper.getEncryptionKey(taps);
        assertEquals(2 + 8, key);
    }
}