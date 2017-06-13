package com.nervousfish.nervousfish.test;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.test.rule.ActivityTestRule;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ImageView;

import com.nervousfish.nervousfish.BaseTest;
import com.nervousfish.nervousfish.R;
import com.nervousfish.nervousfish.activities.QRExchangeKeyActivity;
import com.nervousfish.nervousfish.data_objects.IKey;
import com.nervousfish.nervousfish.modules.qr.QRGenerator;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;

import cucumber.api.CucumberOptions;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static junit.framework.Assert.assertTrue;

@CucumberOptions(features = "features")
public class QRExchangeKeyActivitySteps {

    @Rule
    public ActivityTestRule<QRExchangeKeyActivity> mActivityRule =
            new ActivityTestRule<>(QRExchangeKeyActivity.class, true, false);

    @Given("^I am viewing QRExchange activity$")
    public void iAmViewingQRExchangeActivity() {
        final Intent intent = new Intent();
        this.mActivityRule.launchActivity(intent);
    }

    @When("^I press the back button$")
    public void iPressTheBackButton(){
        onView(withId(R.id.back_button_QR_exchange)).perform(click());
    }

    @Then("^I should return from the QRExchangeKeyActivity$")
    public void iShouldReturnFromTheQRExchangeKeyActivity() {
        assertTrue(mActivityRule.getActivity().isDestroyed());
    }
}
