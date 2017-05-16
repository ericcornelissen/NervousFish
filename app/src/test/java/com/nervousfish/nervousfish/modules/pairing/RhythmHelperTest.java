package com.nervousfish.nervousfish.modules.pairing;

import com.nervousfish.nervousfish.data_objects.tap.ATapData;
import com.nervousfish.nervousfish.data_objects.tap.SingleTap;

import org.junit.Before;
import org.junit.Test;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class RhythmHelperTest {
    @Test
    public void testSingleMatchByAdd() throws Exception {
        List<ATapData> masterData = new ArrayList<>();
        masterData.add(new SingleTap(new Timestamp(0L)));
        final RhythmHelper helper = new RhythmHelper(masterData);

        helper.addSlaveData(new SingleTap(new Timestamp(1000000L)));
        assertTrue(helper.isMatch(2, 10));
    }

    @Test
    public void testSingleMatchBySet() throws Exception {
        List<ATapData> masterData = new ArrayList<>();
        masterData.add(new SingleTap(new Timestamp(0L)));
        final RhythmHelper helper = new RhythmHelper(masterData);

        List<ATapData> slaveData = new ArrayList<>();
        slaveData.add(new SingleTap(new Timestamp(1000000L)));
        helper.setSlaveData(slaveData);
        assertTrue(helper.isMatch(2, 10));
    }

    @Test
    public void testFiveMatchByAdd() throws Exception {
        List<ATapData> masterData = new ArrayList<>();
        masterData.add(new SingleTap(new Timestamp(0L)));
        masterData.add(new SingleTap(new Timestamp(20L)));
        masterData.add(new SingleTap(new Timestamp(40L)));
        masterData.add(new SingleTap(new Timestamp(60L)));
        masterData.add(new SingleTap(new Timestamp(80L)));
        final RhythmHelper helper = new RhythmHelper(masterData);

        helper.addSlaveData(new SingleTap(new Timestamp(1000000L)));
        helper.addSlaveData(new SingleTap(new Timestamp(1000018L)));
        helper.addSlaveData(new SingleTap(new Timestamp(1000036L)));
        helper.addSlaveData(new SingleTap(new Timestamp(1000065L)));
        helper.addSlaveData(new SingleTap(new Timestamp(1000081L)));
        assertTrue(helper.isMatch(2, 20));
    }

    @Test
    public void testFiveNoMatchByAdd() throws Exception {
        List<ATapData> masterData = new ArrayList<>();
        masterData.add(new SingleTap(new Timestamp(0L)));
        masterData.add(new SingleTap(new Timestamp(20L)));
        masterData.add(new SingleTap(new Timestamp(40L)));
        masterData.add(new SingleTap(new Timestamp(60L)));
        masterData.add(new SingleTap(new Timestamp(80L)));
        final RhythmHelper helper = new RhythmHelper(masterData);

        helper.addSlaveData(new SingleTap(new Timestamp(1000000L)));
        helper.addSlaveData(new SingleTap(new Timestamp(1000018L)));
        helper.addSlaveData(new SingleTap(new Timestamp(1000036L)));
        helper.addSlaveData(new SingleTap(new Timestamp(1000065L)));
        helper.addSlaveData(new SingleTap(new Timestamp(1000081L)));
        assertFalse(helper.isMatch(2, 2));
    }

    @Test
    public void testFiveMatchBySet() throws Exception {
        List<ATapData> masterData = new ArrayList<>();
        masterData.add(new SingleTap(new Timestamp(0L)));
        masterData.add(new SingleTap(new Timestamp(20L)));
        masterData.add(new SingleTap(new Timestamp(40L)));
        masterData.add(new SingleTap(new Timestamp(60L)));
        masterData.add(new SingleTap(new Timestamp(80L)));
        final RhythmHelper helper = new RhythmHelper(masterData);

        List<ATapData> slaveData = new ArrayList<>();
        slaveData.add(new SingleTap(new Timestamp(1000000L)));
        slaveData.add(new SingleTap(new Timestamp(1000018L)));
        slaveData.add(new SingleTap(new Timestamp(1000036L)));
        slaveData.add(new SingleTap(new Timestamp(1000065L)));
        slaveData.add(new SingleTap(new Timestamp(1000081L)));
        helper.setSlaveData(slaveData);
        assertTrue(helper.isMatch(2, 20));
    }

    @Test
    public void testFiveNoMatchBySet() throws Exception {
        List<ATapData> masterData = new ArrayList<>();
        masterData.add(new SingleTap(new Timestamp(0L)));
        masterData.add(new SingleTap(new Timestamp(20L)));
        masterData.add(new SingleTap(new Timestamp(40L)));
        masterData.add(new SingleTap(new Timestamp(60L)));
        masterData.add(new SingleTap(new Timestamp(80L)));
        final RhythmHelper helper = new RhythmHelper(masterData);

        helper.addSlaveData(new SingleTap(new Timestamp(1000000L)));
        helper.addSlaveData(new SingleTap(new Timestamp(1000018L)));
        helper.addSlaveData(new SingleTap(new Timestamp(1000036L)));
        helper.addSlaveData(new SingleTap(new Timestamp(1000065L)));
        helper.addSlaveData(new SingleTap(new Timestamp(1000081L)));
        List<ATapData> slaveData = new ArrayList<>();
        slaveData.add(new SingleTap(new Timestamp(1000000L)));
        slaveData.add(new SingleTap(new Timestamp(1000018L)));
        slaveData.add(new SingleTap(new Timestamp(1000036L)));
        slaveData.add(new SingleTap(new Timestamp(1000065L)));
        slaveData.add(new SingleTap(new Timestamp(1000081L)));
        helper.setSlaveData(slaveData);
        assertFalse(helper.isMatch(2, 2));
    }

    @Test
    public void testTooFewSlaveData() throws Exception {
        List<ATapData> masterData = new ArrayList<>();
        masterData.add(new SingleTap(new Timestamp(0L)));
        masterData.add(new SingleTap(new Timestamp(20L)));
        masterData.add(new SingleTap(new Timestamp(40L)));
        masterData.add(new SingleTap(new Timestamp(60L)));
        masterData.add(new SingleTap(new Timestamp(80L)));
        final RhythmHelper helper = new RhythmHelper(masterData);

        helper.addSlaveData(new SingleTap(new Timestamp(1000000L)));
        helper.addSlaveData(new SingleTap(new Timestamp(1000018L)));
        helper.addSlaveData(new SingleTap(new Timestamp(1000036L)));
        helper.addSlaveData(new SingleTap(new Timestamp(1000065L)));
        assertFalse(helper.isMatch(2, 20));
    }

    @Test
    public void testTooMuchSlaveData() throws Exception {
        List<ATapData> masterData = new ArrayList<>();
        masterData.add(new SingleTap(new Timestamp(0L)));
        masterData.add(new SingleTap(new Timestamp(20L)));
        masterData.add(new SingleTap(new Timestamp(40L)));
        masterData.add(new SingleTap(new Timestamp(60L)));
        masterData.add(new SingleTap(new Timestamp(80L)));
        final RhythmHelper helper = new RhythmHelper(masterData);

        helper.addSlaveData(new SingleTap(new Timestamp(1000000L)));
        helper.addSlaveData(new SingleTap(new Timestamp(1000020L)));
        helper.addSlaveData(new SingleTap(new Timestamp(1000040L)));
        helper.addSlaveData(new SingleTap(new Timestamp(1000060L)));
        helper.addSlaveData(new SingleTap(new Timestamp(1000080L)));
        helper.addSlaveData(new SingleTap(new Timestamp(10000100L)));
        helper.addSlaveData(new SingleTap(new Timestamp(10000120L)));
        helper.addSlaveData(new SingleTap(new Timestamp(10000140L)));
        assertFalse(helper.isMatch(2, 20));
    }

    @Test
    public void testSlaveDataNotInOrder() throws Exception {
        List<ATapData> masterData = new ArrayList<>();
        masterData.add(new SingleTap(new Timestamp(0L)));
        masterData.add(new SingleTap(new Timestamp(20L)));
        masterData.add(new SingleTap(new Timestamp(40L)));
        masterData.add(new SingleTap(new Timestamp(60L)));
        masterData.add(new SingleTap(new Timestamp(80L)));
        final RhythmHelper helper = new RhythmHelper(masterData);

        helper.addSlaveData(new SingleTap(new Timestamp(1000000L)));
        helper.addSlaveData(new SingleTap(new Timestamp(1000030L)));
        helper.addSlaveData(new SingleTap(new Timestamp(1000060L)));
        helper.addSlaveData(new SingleTap(new Timestamp(1000090L)));
        helper.addSlaveData(new SingleTap(new Timestamp(1100000L)));
        helper.addSlaveData(new SingleTap(new Timestamp(1100020L)));
        helper.addSlaveData(new SingleTap(new Timestamp(1100040L)));
        helper.addSlaveData(new SingleTap(new Timestamp(1100060L)));
        helper.addSlaveData(new SingleTap(new Timestamp(1100080L)));
        assertTrue(helper.isMatch(2, 20));
    }
}