package com.nervousfish.nervousfish.test;

import android.content.Intent;
import android.support.test.rule.ActivityTestRule;

import com.nervousfish.nervousfish.R;
import com.nervousfish.nervousfish.activities.QRExchangeActivity;

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
public class QRExchangeSteps {

    @Rule
    public ActivityTestRule<QRExchangeActivity> mActivityRule =
            new ActivityTestRule<>(QRExchangeActivity.class, true, false);

    @Given("^I am viewing QRExchange activity$")
    public void iAmViewingQRExchangeActivity() {
        final Intent intent = new Intent();
        this.mActivityRule.launchActivity(intent);
    }

    @When("^I press the back button in QRExchange activity$")
    public void iPressTheBackButton(){
        onView(withId(R.id.back_button_QR_exchange)).perform(click());
    }

    @Then("^I should return from the QRExchangeActivity$")
    public void iShouldReturnFromTheQRExchangeKeyActivity() {
        assertTrue(mActivityRule.getActivity().isFinishing());
    }

}
