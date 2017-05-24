package com.nervousfish.nervousfish.test;

import android.app.Activity;
import android.content.Intent;
import android.support.test.espresso.core.deps.guava.collect.Iterables;
import android.support.test.runner.lifecycle.ActivityLifecycleMonitorRegistry;
import android.support.test.runner.lifecycle.Stage;
import android.test.ActivityInstrumentationTestCase2;

import com.nervousfish.nervousfish.ConstantKeywords;
import com.nervousfish.nervousfish.R;
import com.nervousfish.nervousfish.activities.VisualVerificationActivity;
import com.nervousfish.nervousfish.service_locator.EntryActivity;

import cucumber.api.CucumberOptions;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.assertNotEquals;

@CucumberOptions(features = "features")
public class VisualVerificationSteps extends ActivityInstrumentationTestCase2<EntryActivity> {

    private int[] buttons = new int[] {
        R.id.button00,
        R.id.button01,
        R.id.button02,
        R.id.button03,
        R.id.button10,
        R.id.button11,
        R.id.button12,
        R.id.button13,
        R.id.button10,
        R.id.button21,
        R.id.button22,
        R.id.button23,
        R.id.button30,
        R.id.button31,
        R.id.button32,
        R.id.button33
    };

    public VisualVerificationSteps() {
        super(EntryActivity.class);
    }


    @Given("^I am viewing the visual verification activity$")
    public void iHaveAVisualVerificationActivity() {
        Intent intent = new Intent(getActivity(), VisualVerificationActivity.class);
        intent.putExtra(ConstantKeywords.SERVICE_LOCATOR,
                getCurrentActivity().getIntent().getSerializableExtra(ConstantKeywords.SERVICE_LOCATOR));
        getActivity().startActivity(intent);
    }

    @When("^I press button (\\d+)$")
    public void iPressButton(final int button) {
        int btn = this.buttons[button];
        onView(withId(btn)).perform(click());
    }

    @Then("^I leave the visual verification activity$")
    public void iLeaveTheVisualVerificationActivity() {
        assertNotEquals(getCurrentActivity().getClass(), VisualVerificationActivity.class);
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
