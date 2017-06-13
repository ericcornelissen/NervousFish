package com.nervousfish.nervousfish.test;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.support.test.espresso.intent.Checks;
import android.support.test.espresso.matcher.BoundedMatcher;
import android.support.test.rule.ActivityTestRule;
import android.support.v4.content.res.ResourcesCompat;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.nervousfish.nervousfish.BaseTest;
import com.nervousfish.nervousfish.R;
import com.nervousfish.nervousfish.activities.CreateProfileActivity;
import com.nervousfish.nervousfish.activities.MainActivity;
import com.nervousfish.nervousfish.data_objects.Contact;
import com.nervousfish.nervousfish.data_objects.IKey;
import com.nervousfish.nervousfish.data_objects.KeyPair;
import com.nervousfish.nervousfish.data_objects.Profile;
import com.nervousfish.nervousfish.modules.cryptography.KeyGeneratorAdapter;
import com.nervousfish.nervousfish.modules.database.IDatabase;
import com.nervousfish.nervousfish.service_locator.IServiceLocator;
import com.nervousfish.nervousfish.service_locator.NervousFish;
import com.nervousfish.nervousfish.service_locator.ServiceLocator;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Rule;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cucumber.api.CucumberOptions;
import cucumber.api.java.After;
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

import static com.nervousfish.nervousfish.BaseTest.accessConstructor;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

@CucumberOptions(features = "features")
public class CreateProfileSteps {


    @Rule
    public ActivityTestRule<CreateProfileActivity> mActivityRule =
            new ActivityTestRule<>(CreateProfileActivity.class, true, false);

    @Before
    public void createDatabase() throws Exception {
        final IDatabase database =  NervousFish.getServiceLocator().getDatabase();
        KeyGeneratorAdapter keyGen = (KeyGeneratorAdapter) accessConstructor(KeyGeneratorAdapter.class,  NervousFish.getServiceLocator());
        KeyPair keyPair = keyGen.generateRSAKeyPair("Test");
        Contact contactu = new Contact("name", new ArrayList<IKey>());
        Profile profile = new Profile(contactu, new ArrayList<KeyPair>());
        profile.addKeyPair(keyPair);
        database.createDatabase(profile, "Testpass");
        database.loadDatabase("Testpass");
    }

    @After
    public void deleteDatabase() {
        final IDatabase database =  NervousFish.getServiceLocator().getDatabase();

        database.deleteDatabase();
    }

    @Given("^I am viewing the create profile activity$")
    public void iAmViewingTheCreateProfileActivity() throws IOException {
        final Intent intent = new Intent();
        this.mActivityRule.launchActivity(intent);
    }

    @When("^I click on the submit profile button$")
    public void iClickOnSubmitProfile() {
        InputMethodManager imm = (InputMethodManager)mActivityRule.getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mActivityRule.getActivity().getCurrentFocus().getWindowToken(), 0);
        onView(withId(R.id.submitProfile)).perform(scrollTo()).perform(click());
    }

    @When("^I click ok on the popup with a warning about creating an profile$")
    public void iClickOkOnPopup() {
        onView(withId(R.id.confirm_button)).perform(click());
    }

    @When("^I enter a valid (.*?) as name$")
    public void iEnterValidName(final String name) {
        InputMethodManager imm = (InputMethodManager)mActivityRule.getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mActivityRule.getActivity().getCurrentFocus().getWindowToken(), 0);
        onView(withId(R.id.profile_enter_name)).perform(scrollTo()).perform(replaceText(name));
    }

    @When("^I enter a valid (.*?) as password$")
    public void iEnterValidPassword(final String password) {
        InputMethodManager imm = (InputMethodManager)mActivityRule.getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mActivityRule.getActivity().getCurrentFocus().getWindowToken(), 0);
        onView(withId(R.id.profile_enter_password)).perform(scrollTo()).perform(typeText(password));
    }

    @When("^I enter a valid repeat (.*?) as repeat password$")
    public void iEnterValidRepeatPassword(final String password) {
        InputMethodManager imm = (InputMethodManager)mActivityRule.getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mActivityRule.getActivity().getCurrentFocus().getWindowToken(), 0);
        onView(withId(R.id.profile_repeat_password)).perform(scrollTo()).perform(typeText(password));
    }

    @When("^I enter a (.*?) with a length smaller than 6 characters$")
    public void enterPasswordSmallerThanSix(final String password) {
        InputMethodManager imm = (InputMethodManager)mActivityRule.getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mActivityRule.getActivity().getCurrentFocus().getWindowToken(), 0);
        onView(withId(R.id.profile_enter_password)).perform(typeText(password));
    }

    @When("^I enter a different (.*?) than the password field$")
    public void iEnterDifferentRepeatPassword(final String password) {
        InputMethodManager imm = (InputMethodManager)mActivityRule.getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mActivityRule.getActivity().getCurrentFocus().getWindowToken(), 0);
        onView(withId(R.id.profile_repeat_password)).perform(typeText(password));
    }

    @Then("^I should stay on the create profile activity$")
    public void iShouldGoToCreateProfileActivity() {
        intended(hasComponent(CreateProfileActivity.class.getName()));
    }

    @Then("^I should progress directly to the main activity$")
    public void iShouldProgressDirectlyToTheMainActivity() {
        intended(hasComponent(MainActivity.class.getName()));
    }

    @Then("^the name input field should become red$")
    public void nameInputFieldShouldBeRed() {
        onView(withId(R.id.profile_enter_name)).check(matches(withBackgroundColor(ResourcesCompat.getColor(
                mActivityRule.getActivity().getResources(), R.color.red_fail, null))));
    }

    @Then("^the password input field should become red$")
    public void passwordInputFieldShouldBeRed() {
        onView(withId(R.id.profile_enter_password)).check(matches(withBackgroundColor(ResourcesCompat.getColor(
                mActivityRule.getActivity().getResources(), R.color.red_fail, null))));
    }

    @Then("^the repeat password input field should become red$")
    public void repeatPasswordInputFieldShouldBeRed() {
        onView(withId(R.id.profile_repeat_password)).check(matches(withBackgroundColor(ResourcesCompat.getColor(
                mActivityRule.getActivity().getResources(), R.color.red_fail, null))));
    }

    @Then("^the profile with (.*?) should be saved in the database$")
    public void profileWithNameShouldBeInDatabase(final String name) throws IOException {
        final Profile profile = NervousFish.getServiceLocator().getDatabase().getProfile();
        assertEquals(name, profile.getName());
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