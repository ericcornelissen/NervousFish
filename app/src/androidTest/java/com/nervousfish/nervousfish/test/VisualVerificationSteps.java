package com.nervousfish.nervousfish.test;

import android.app.Activity;
import android.content.Intent;
import android.support.test.espresso.core.deps.guava.collect.Iterables;
import android.support.test.runner.lifecycle.ActivityLifecycleMonitorRegistry;
import android.support.test.runner.lifecycle.Stage;
import android.test.ActivityInstrumentationTestCase2;

import com.nervousfish.nervousfish.*;
import com.nervousfish.nervousfish.activities.VisualVerificationActivity;
import com.nervousfish.nervousfish.service_locator.EntryActivity;

import cucumber.api.CucumberOptions;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

@CucumberOptions(features = "features")
public class VisualVerificationSteps extends ActivityInstrumentationTestCase2<EntryActivity> {

    public VisualVerificationSteps() {
        super(EntryActivity.class);
    }

    @Given("^I am viewing the visual verification activity$")
    public void iHaveAVisualVerificationActivity() {
        Intent intent = new Intent(getActivity(), VisualVerificationActivity.class);
        getActivity().startActivity(intent);
        assertEquals(getCurrentActivity().getClass(), VisualVerificationActivity.class);
    }

    @When("^I press a button$")
    public void iPressAButton() {
        onView(withId(com.nervousfish.nervousfish.R.id.button00)).perform(click());
    }

    @Then("^I'm happy$")
    public void imHappy() {
        assertTrue(true);
    }

    private Activity getCurrentActivity() {
        getInstrumentation().waitForIdleSync();
        final Activity[] activity = new Activity[1];
        try {
            runTestOnUiThread(new Runnable() {
                @Override
                public void run() {
                    java.util.Collection<Activity> activities = ActivityLifecycleMonitorRegistry.getInstance().getActivitiesInStage(Stage.RESUMED);
                    activity[0] = Iterables.getOnlyElement(activities);
                }
            });
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return activity[0];
    }

}
