package com.nervousfish.nervousfish.test;

import android.os.Bundle;
import android.support.test.espresso.intent.Intents;
import android.support.test.runner.MonitoringInstrumentation;

import cucumber.api.android.CucumberInstrumentationCore;
import cucumber.api.java.After;
import cucumber.api.java.Before;

public class Instrumentation extends MonitoringInstrumentation {

    // Suppressed because we cannot obtain references to the data directory directly from the Instrumentation tests
    @SuppressWarnings("SdCardPath")
    public static final String filesDir = "/data/user/0/com.nervousfish.nervousfish/files";
    private final CucumberInstrumentationCore mInstrumentationCore = new CucumberInstrumentationCore(this);

    @Override
    public void onCreate(final Bundle bundle) {
        super.onCreate(bundle);

        this.mInstrumentationCore.create(bundle);
        this.start();
    }

    @Override
    public void onStart() {
        super.onStart();

        this.waitForIdleSync();
        this.mInstrumentationCore.start();
    }

    @Before
    public void setUp() throws Exception {
        Intents.init();
    }

    @After
    public void tearDown() {
        Intents.release();
    }

}
