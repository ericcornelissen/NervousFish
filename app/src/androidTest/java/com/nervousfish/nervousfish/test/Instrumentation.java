package com.nervousfish.nervousfish.test;

import android.os.Bundle;
import android.support.multidex.MultiDex;
import android.support.test.runner.AndroidJUnitRunner;
import android.support.test.runner.MonitoringInstrumentation;

import cucumber.api.android.CucumberInstrumentationCore;

@SuppressWarnings("PMD")
public class Instrumentation extends AndroidJUnitRunner {

    private final CucumberInstrumentationCore mInstrumentationCore = new CucumberInstrumentationCore(this);

    @Override
    public void onCreate(Bundle arguments) {
        MultiDex.install(getTargetContext());
        mInstrumentationCore.create(arguments);
        super.onCreate(arguments);
       // start();
    }

    @Override
    public void onStart() {
        mInstrumentationCore.start();
        waitForIdleSync();
        super.onStart();

        allFinished();
    }

    private int mResultCode;
    private Bundle mResults = new Bundle();

    /**
     * Catch calls to finish() and aggregate results into a bundle
     */
    @Override
    public void finish(int resultCode, Bundle results) {
        mResultCode = resultCode;
        mResults.putAll(results);
    }

    private void allFinished() {
        super.finish(mResultCode, mResults);
    }
}
