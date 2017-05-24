package com.nervousfish.nervousfish.test;

import android.os.Bundle;
import android.support.test.runner.MonitoringInstrumentation;

import cucumber.api.android.CucumberInstrumentationCore;

public class Instrumentation extends MonitoringInstrumentation {

    private final CucumberInstrumentationCore mInstrumentationCore = new CucumberInstrumentationCore(this);

    @Override
    public void onCreate(final Bundle bundle) {
        super.onCreate(bundle);

        mInstrumentationCore.create(bundle);
        start();
    }

    @Override
    public void onStart() {
        super.onStart();

        waitForIdleSync();
        mInstrumentationCore.start();
    }

}
