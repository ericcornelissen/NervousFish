package com.nervousfish.nervousfish.test;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.support.test.espresso.intent.Checks;
import android.support.test.espresso.matcher.BoundedMatcher;
import android.support.test.rule.ActivityTestRule;
import android.support.v4.content.res.ResourcesCompat;
import android.view.View;
import android.widget.EditText;

import com.nervousfish.nervousfish.BaseTest;
import com.nervousfish.nervousfish.ConstantKeywords;
import com.nervousfish.nervousfish.R;
import com.nervousfish.nervousfish.activities.CreateProfileActivity;
import com.nervousfish.nervousfish.activities.LoginActivity;
import com.nervousfish.nervousfish.data_objects.Profile;
import com.nervousfish.nervousfish.service_locator.IServiceLocator;
import com.nervousfish.nervousfish.service_locator.ServiceLocator;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Rule;

import java.io.IOException;
import java.util.List;

import cucumber.api.CucumberOptions;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

@CucumberOptions(features = "features")
public class CreateProfileSteps {

    private final IServiceLocator serviceLocator = (IServiceLocator) BaseTest.accessConstructor(ServiceLocator.class, Instrumentation.filesDir);

    @Rule
    public ActivityTestRule<CreateProfileActivity> mActivityRule =
            new ActivityTestRule<>(CreateProfileActivity.class, true, false);

    @Given("^I am viewing the create profile activity$")
    public void iAmViewingTheCreateProfileActivity() throws IOException {
        final Intent intent = new Intent();
        intent.putExtra(ConstantKeywords.SERVICE_LOCATOR, this.serviceLocator);
        mActivityRule.launchActivity(intent);
    }

    @Given("^there are no profiles in the database$")
    public void thereAreNoProfilesInDatabase() throws IOException {
        final List<Profile> profiles = serviceLocator.getDatabase().getProfiles();
        for (final Profile profile : profiles) {
            serviceLocator.getDatabase().deleteProfile(profile);
        }
        assertTrue(serviceLocator.getDatabase().getProfiles().isEmpty());
    }

    @When("^I click on the submit profile button$")
    public void iClickOnSubmitProfile() {
        onView(withId(R.id.submitProfile)).perform(scrollTo()).perform(click());
    }

    @When("^I click ok on the popup$")
    public void iClickOkOnPopup() {
        onView(withId(R.id.confirm_button)).perform(click());
    }

    @When("^I enter a valid (.*?) as name$")
    public void iEnterValidName(final String name) {
        onView(withId(R.id.profileEnterName)).perform(replaceText(name));
    }

    @When("^I enter a valid (.*?) as password$")
    public void iEnterValidPassword(final String password) {
        onView(withId(R.id.profileEnterPassword)).perform(typeText(password));
    }

    @When("^I enter a valid repeat (.*?) as repeat password$")
    public void iEnterValidRepeatPassword(final String password) {
        onView(withId(R.id.profileRepeatPassword)).perform(typeText(password));
    }

    @Then("^I should stay on the create profile activity$")
    public void iShouldGoToCreateProfileActivity() {
        intended(hasComponent(CreateProfileActivity.class.getName()));
    }

    @Then("^I should go to the login activity$")
    public void iShouldGoToLoginActivity() {
        intended(hasComponent(LoginActivity.class.getName()));
    }

    @Then("^the name input field should become red$")
    public void nameInputFieldShouldBeRed() {
        onView(withId(R.id.profileEnterName)).check(matches(withBackgroundColor(ResourcesCompat.getColor(
                mActivityRule.getActivity().getResources(), R.color.red_fail, null))));
    }

    @Then("^the password input field should become red$")
    public void passwordInputFieldShouldBeRed() {
        onView(withId(R.id.profileEnterPassword)).check(matches(withBackgroundColor(ResourcesCompat.getColor(
                mActivityRule.getActivity().getResources(), R.color.red_fail, null))));
    }

    @Then("^the repeat password input field should become red$")
    public void repeatPasswordInputFieldShouldBeRed() {
        onView(withId(R.id.profileRepeatPassword)).check(matches(withBackgroundColor(ResourcesCompat.getColor(
                mActivityRule.getActivity().getResources(), R.color.red_fail, null))));
    }

    @Then("^the profile with (.*?) should be saved in the database$")
    public void profileWithNameShouldBeInDatabase(final String name) throws IOException {
        final List<Profile> profiles = serviceLocator.getDatabase().getProfiles();
        assertEquals(name, profiles.get(0).getName());
    }

    /**
     * Checks if an element has a certain background color.
     *
     * @param color - the color that has to be checked
     * @return a {@link Matcher}
     */
    private static Matcher<View> withBackgroundColor(final int color) {
        Checks.checkNotNull(color);
        return new BoundedMatcher<View, EditText>(EditText.class) {
            @Override
            public boolean matchesSafely(EditText warning) {
                return color == ((ColorDrawable) warning.getBackground()).getColor();
            }
            @Override
            public void describeTo(Description description) {
                description.appendText("with text color: ");
            }
        };
    }

}