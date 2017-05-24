package com.nervousfish.nervousfish.test;

import android.content.Intent;
import android.support.test.rule.ActivityTestRule;

import com.nervousfish.nervousfish.ConstantKeywords;
import com.nervousfish.nervousfish.R;
import com.nervousfish.nervousfish.TestServiceLocator;
import com.nervousfish.nervousfish.activities.LoginActivity;
import com.nervousfish.nervousfish.service_locator.IServiceLocator;

import org.junit.Rule;

import cucumber.api.CucumberOptions;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;

import static android.support.test.espresso.matcher.ViewMatchers.withId;

@CucumberOptions(features = "features")
public class LoginSteps {

    public static final String CORRECT_PASSWORD = "12345";
    private final IServiceLocator serviceLocator = new TestServiceLocator();

    @Rule
    public ActivityTestRule<LoginActivity> mActivityRule =
            new ActivityTestRule<>(LoginActivity.class, true, false);

    @Given("^I am viewing the login activity$")
    public void iAmViewingTheLoginActivity() {
        final Intent intent = new Intent();
        intent.putExtra(ConstantKeywords.SERVICE_LOCATOR, this.serviceLocator);
        mActivityRule.launchActivity(intent);
    }

    @When("^I type (.*?) as password$")
    public void iTypeSomethingAsPassword(final String password) {
        onView(withId(R.id.login_password_input)).perform(typeText(password));
    }

    @When("^I press the login button$")
    public void iPressTheLoginButton() {
        onView(withId(R.id.submit)).perform(scrollTo()).perform(click());
    }

    @Then("^I should stay in the LoginActivity$")
    public void iShouldStayInLoginActivity() {
        // TODO: add assertion
    }

    @Then("^I should see an authentication error$")
    public void iShouldSeeAnAuthenticationError() {
        onView(withId(R.id.error)).check(matches(isDisplayed()));
    }

}