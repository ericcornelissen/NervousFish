package com.nervousfish.nervousfish.test;

import android.content.Intent;
import android.support.test.rule.ActivityTestRule;

import com.nervousfish.nervousfish.ConstantKeywords;
import com.nervousfish.nervousfish.R;
import com.nervousfish.nervousfish.TestServiceLocator;
import com.nervousfish.nervousfish.activities.MainActivity;
import com.nervousfish.nervousfish.activities.WaitForSlaveActivity;
import com.nervousfish.nervousfish.service_locator.IServiceLocator;

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

@CucumberOptions(features = "features")
public class WaitingForSlaveSteps {

    private final IServiceLocator serviceLocator = new TestServiceLocator();

    @Rule
    public ActivityTestRule<WaitForSlaveActivity> mActivityRule =
            new ActivityTestRule<>(WaitForSlaveActivity.class, true, false);

    @Given("^I am viewing the waiting for slave activity$")
    public void iAmViewingWaitingForSlaveActivity() {
        final Intent intent = new Intent();
        intent.putExtra(ConstantKeywords.SERVICE_LOCATOR, this.serviceLocator);
        mActivityRule.launchActivity(intent);
    }

    @When("^I press the cancel waiting for slave button$")
    public void iPressTheCancelWaitingForSlaveButton() {
        onView(withId(R.id.cancelWaitForSlave)).perform(click());
    }

    @Then("^I should go to the main activity$")
    public void iShouldGoToTheMainActivity() {
        intended(hasComponent(MainActivity.class.getName()));
    }

}
