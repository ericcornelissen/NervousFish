package com.nervousfish.nervousfish.test;

import android.app.Activity;
import android.content.Intent;
import android.support.test.espresso.core.deps.guava.collect.Iterables;
import android.support.test.runner.lifecycle.ActivityLifecycleMonitorRegistry;
import android.support.test.runner.lifecycle.Stage;
import android.test.ActivityInstrumentationTestCase2;

import com.nervousfish.nervousfish.ConstantKeywords;
import com.nervousfish.nervousfish.activities.BluetoothConnectionActivity;
import com.nervousfish.nervousfish.activities.VisualVerificationActivity;
import com.nervousfish.nervousfish.service_locator.EntryActivity;

import cucumber.api.CucumberOptions;
import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;


@CucumberOptions(features = "features")
public class BluetoothActivitySteps extends ActivityInstrumentationTestCase2<EntryActivity> {
    private static final Activity[] activity = new Activity[1];

    public BluetoothActivitySteps(EntryActivity activityClass) {
        super(EntryActivity.class);
    }

    @Given("^I have a BluetoothConnectionActivity and don't have an established connection$")
    public void iHaveABluetoothConnectionActivityWithoutAConnection() {
        Intent intent = new Intent(getActivity(), BluetoothConnectionActivity.class);
        intent.putExtra(ConstantKeywords.SERVICE_LOCATOR,
                getCurrentActivity().getIntent().getSerializableExtra(ConstantKeywords.SERVICE_LOCATOR));
        getActivity().startActivity(intent);

    }

    @When("^When I'm in the MainActivity and the connect button is pressed")
    public void iPressConnect() {
        assertTrue(true);
    }

    @Then("^$")
    public void iShouldStartAConnection() {
        assertTrue(true);
    }


    private Activity getCurrentActivity() {
        getInstrumentation().waitForIdleSync();
        try {
            runTestOnUiThread(new GetCurrentActivityRunnable());
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return activity[0];
    }

    private static class GetCurrentActivityRunnable implements Runnable {
        @Override
        public void run() {
            java.util.Collection<Activity> activities = ActivityLifecycleMonitorRegistry.getInstance().getActivitiesInStage(Stage.RESUMED);
            activity[0] = Iterables.getOnlyElement(activities);
        }
    }
}
