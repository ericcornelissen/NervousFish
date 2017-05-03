package com.nervousfish.nervousfish.test;

import android.test.ActivityInstrumentationTestCase2;

import com.nervousfish.nervousfish.BluetoothConnectionActivity;

import cucumber.api.CucumberOptions;

/**
 * Created by Kilian on 3/05/2017.
 */

@SuppressWarnings("PMD")
@CucumberOptions(features = "features")
public class BluetoothActivitySteps extends ActivityInstrumentationTestCase2<BluetoothConnectionActivity> {

    public BluetoothActivitySteps(Class<BluetoothConnectionActivity> activityClass) {
        super(BluetoothConnectionActivity.class);
    }

}
