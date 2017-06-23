package com.nervousfish.nervousfish.test;

import android.content.Context;
import android.content.Intent;
import android.support.test.espresso.action.ViewActions;
import android.support.test.rule.ActivityTestRule;
import android.view.inputmethod.InputMethodManager;

import com.nervousfish.nervousfish.R;
import com.nervousfish.nervousfish.activities.LoginActivity;

import org.junit.Rule;

import java.io.IOException;

import cucumber.api.CucumberOptions;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

import static android.support.test.espresso.Espresso.closeSoftKeyboard;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;

import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static junit.framework.Assert.assertFalse;

@CucumberOptions(features = "features")
public class LoginSteps {
    private static boolean firstRun = true;
    private static LoginActivity activity;

    @Rule
    public ActivityTestRule<LoginActivity> mActivityRule =
            new ActivityTestRule<>(LoginActivity.class, true, false);

    @Given("^I am viewing the login activity$")
    public void iAmViewingTheLoginActivity() throws IOException {
        if (firstRun) {
            this.mActivityRule.launchActivity(null);
            activity = this.mActivityRule.getActivity();
            firstRun = false;
        }
    }

    @When("^I type (.*?) as password$")
    public void iTypeSomethingAsPassword(final String password) {
        onView(withId(R.id.login_password_input)).perform(typeText(password), ViewActions.closeSoftKeyboard());
    }

    @When("^I press the login button$")
    public void iPressTheLoginButton() {
        pressBack();
        onView(withId(R.id.submit)).perform(click());
    }

    @Then("^I should stay in the LoginActivity$")
    public void iShouldStayInLoginActivity() {
        assertFalse(activity.isDestroyed());
    }

    @Then("^I should see an authentication error$")
    public void iShouldSeeAnAuthenticationError() {
        onView(withId(R.id.error_message_login)).check(matches(isDisplayed()));
    }

}