package com.nervousfish.nervousfish.test;

import android.content.Intent;
import android.support.test.rule.ActivityTestRule;

import com.nervousfish.nervousfish.BaseTest;
import com.nervousfish.nervousfish.ConstantKeywords;
import com.nervousfish.nervousfish.R;
import com.nervousfish.nervousfish.activities.RhythmCreateActivity;
import com.nervousfish.nervousfish.activities.WaitActivity;
import com.nervousfish.nervousfish.service_locator.IServiceLocator;
import com.nervousfish.nervousfish.service_locator.ServiceLocatorNoNetwork;

import org.junit.Rule;

import cucumber.api.CucumberOptions;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static junit.framework.Assert.assertTrue;

@CucumberOptions(features = "features")
public class RhythmSteps {

    private final IServiceLocator serviceLocator = (IServiceLocator) BaseTest.accessConstructor(ServiceLocatorNoNetwork.class, Instrumentation.filesDir);

    @Rule
    public ActivityTestRule<RhythmCreateActivity> mActivityRule =
            new ActivityTestRule<>(RhythmCreateActivity.class, true, false);

    @Given("^I am viewing the rhythm activity$")
    public void iAmViewingRhythmCreateActivity() {
        final Intent intent = new Intent();
        intent.putExtra(ConstantKeywords.SERVICE_LOCATOR, this.serviceLocator);
        mActivityRule.launchActivity(intent);
    }

    @When("^I press the start recording button$")
    public void iPressTheStartRecordingButton() {
        onView(withId(R.id.start_recording_button)).perform(click());
    }

    @When("^I press the tap button a couple of times$")
    public void iPressTheTapButton() {
        onView(withId(R.id.tap_screen_rhythm_activity)).perform(click()).perform(click()).perform(click());
    }

    @When("^I press the stop recording button$")
    public void iPressTheStopRecordingButton() {
        onView(withId(R.id.stop_recording_button)).perform(click());
    }

    @When("^I press the done button$")
    public void iPressDoneButton() {
        onView(withId(R.id.done_tapping_button)).perform(click());
    }

    @Then("^I should go to the WaitActivity$")
    public void goToWaitActivity() {
        intended(hasComponent(WaitActivity.class.getName()));
    }

}
