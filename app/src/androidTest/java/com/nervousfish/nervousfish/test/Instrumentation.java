package com.nervousfish.nervousfish.test;

import android.os.Bundle;
import android.support.test.runner.AndroidJUnitRunner;

import cucumber.api.android.CucumberInstrumentationCore;

public class Instrumentation extends AndroidJUnitRunner {

    private final CucumberInstrumentationCore mInstrumentationCore = new CucumberInstrumentationCore(this);

    @Override
    public void onCreate(final Bundle bundle) {
        //MultiDex.install(getTargetContext());
        mInstrumentationCore.create(bundle);
        super.onCreate(bundle);
    }

    @Override
    public void onStart() {
        waitForIdleSync();
        mInstrumentationCore.start();

//        super.onStart();
//        allFinished();
    }

//    private int mResultCode;
//    private Bundle mResults = new Bundle();
//
//    /**
//     * Catch calls to finish() and aggregate results into a bundle
//     */
//    @Override
//    public void finish(int resultCode, Bundle results) {
//        mResultCode = resultCode;
//        mResults.putAll(results);
//    }
//
//    private void allFinished() {
//        super.finish(mResultCode, mResults);
//    }
}
