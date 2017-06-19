package com.nervousfish.nervousfish.test;

import android.content.Intent;
import android.support.test.rule.ActivityTestRule;

import com.nervousfish.nervousfish.R;
import com.nervousfish.nervousfish.activities.VisualVerificationActivity;
import com.nervousfish.nervousfish.activities.WaitActivity;

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
public class VisualVerificationSteps {

    private final int[] buttons = {
            R.id.visual_verification_button00,
            R.id.visual_verification_button01,
            R.id.visual_verification_button02,
            R.id.visual_verification_button10,
            R.id.visual_verification_button11,
            R.id.visual_verification_button12,
            R.id.visual_verification_button10,
            R.id.visual_verification_button21,
            R.id.visual_verification_button22,
            R.id.visual_verification_button30,
            R.id.visual_verification_button31,
            R.id.visual_verification_button32,
    };

    @Rule
    public ActivityTestRule<VisualVerificationActivity> mActivityRule =
            new ActivityTestRule<>(VisualVerificationActivity.class, true, false);


    @Given("^I am viewing the visual verification activity$")
    public void iAmViewingTheVisualVerificationActivity() {
        final Intent intent = new Intent();
        this.mActivityRule.launchActivity(intent);
    }

    @When("^I press button (\\d+) in the verification grid$")
    public void iPressButtonXInTheVerificationGrid(final int button) {
        int btn = this.buttons[button];
        onView(withId(btn)).perform(click());
    }

    @Then("^I leave the visual verification activity$")
    public void iLeaveTheVisualVerificationActivity() {
        intended(hasComponent(WaitActivity.class.getName()));
    }

}
