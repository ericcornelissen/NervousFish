package com.nervousfish.nervousfish.test;

import android.content.Intent;
import android.support.test.rule.ActivityTestRule;

import com.nervousfish.nervousfish.R;
import com.nervousfish.nervousfish.activities.SettingsActivity;

import org.junit.Rule;

import java.io.IOException;

import cucumber.api.CucumberOptions;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static junit.framework.Assert.assertTrue;

@CucumberOptions(features = "features")
public class SettingsSteps {

    @Rule
    public ActivityTestRule<SettingsActivity> mActivityRule =
            new ActivityTestRule<>(SettingsActivity.class, true, false);

    @Given("^I am viewing the settings activity$")
    public void iAmViewingSettingsActivity() throws IOException {
        final Intent intent = new Intent();
        this.mActivityRule.launchActivity(intent);
    }

    @When("^I click the back button in the settings activity to go back to main$")
    public void iPressSettingsBackButton() {
        pressBack();
    }

    @When("^I click on the general settings item$")
    public void iClickOnGeneralSettings() {
        onView(withText(com.nervousfish.nervousfish.R.string.pref_header_general)).perform(click());
    }

    @When("^I click on the personal information item$")
    public void iClickOnPersonalInformationSettings() {
        onView(withText(com.nervousfish.nervousfish.R.string.pref_header_profile)).perform(click());
    }

    @Then("^I should go back to the main activity from settings$")
    public void iShouldGoBackToMainFromSettings() {
        assertTrue(this.mActivityRule.getActivity().isFinishing());
    }

    @Then("^I should go to the general settings fragment$")
    public void iShouldGoToGeneralSettingsFragment() {
        onView(withText(R.string.pref_verification_method_to_use)).check(matches(isDisplayed()));
    }

    @Then("^I should go to the personal information fragment$")
    public void iShouldGoToPersonalSettingsFragment() {
        onView(withText(R.string.display_name)).check(matches(isDisplayed()));
    }

}
