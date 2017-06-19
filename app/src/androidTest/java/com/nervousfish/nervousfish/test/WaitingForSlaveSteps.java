package com.nervousfish.nervousfish.test;

import android.content.Intent;
import android.support.test.rule.ActivityTestRule;

import com.nervousfish.nervousfish.R;
import com.nervousfish.nervousfish.activities.WaitActivity;

import org.junit.Rule;

import cucumber.api.CucumberOptions;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static junit.framework.Assert.assertTrue;

@CucumberOptions(features = "features")
public class WaitingForSlaveSteps {

    @Rule
    public ActivityTestRule<WaitActivity> mActivityRule =
            new ActivityTestRule<>(WaitActivity.class, true, false);

    @Given("^I am viewing the waiting for slave activity$")
    public void iAmViewingWaitingForSlaveActivity() {
        final Intent intent = new Intent();
        this.mActivityRule.launchActivity(intent);
    }

    @When("^I press the cancel waiting for slave button$")
    public void iPressTheCancelWaitingForSlaveButton() {
        onView(withId(R.id.cancel_wait_for_slave)).perform(click());
    }

    @Then("^the wait activity should be finishing$")
    public void waitActivityFinish() {
        assertTrue(this.mActivityRule.getActivity().isFinishing());
    }

}
