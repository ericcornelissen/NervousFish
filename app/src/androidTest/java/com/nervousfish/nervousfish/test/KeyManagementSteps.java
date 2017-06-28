package com.nervousfish.nervousfish.test;

import android.content.Intent;
import android.support.test.rule.ActivityTestRule;

import com.nervousfish.nervousfish.R;
import com.nervousfish.nervousfish.activities.KeyManagementActivity;
import com.nervousfish.nervousfish.data_objects.IKey;
import com.nervousfish.nervousfish.data_objects.KeyPair;
import com.nervousfish.nervousfish.data_objects.RSAKey;
import com.nervousfish.nervousfish.modules.database.IDatabase;
import com.nervousfish.nervousfish.service_locator.NervousFish;

import org.junit.Rule;

import cucumber.api.CucumberOptions;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static junit.framework.Assert.assertTrue;

@CucumberOptions(features = "features")
public class KeyManagementSteps {

    @Rule
    public ActivityTestRule<KeyManagementActivity> mActivityRule =
            new ActivityTestRule<>(KeyManagementActivity.class, true, false);

    @Given("^I have a foo keypair in my profile$")
    public void iHaveAFooKeypairInMyProfile() throws Exception {
        final IDatabase database = NervousFish.getServiceLocator().getDatabase();
        final IKey key = new RSAKey("foo", "bar", "baz");
        final KeyPair keyPair = new KeyPair("foo", key, key);
        database.getProfile().addKeyPair(keyPair);
    }

    @Given("^I am viewing the key management activity$")
    public void iAmViewingKeyManagementActivity() {
        final Intent intent = new Intent();
        this.mActivityRule.launchActivity(intent);
    }

    @When("^I press the key management back button$")
    public void iPressTheBackButton() {
        onView(withId(R.id.back_button_key_management)).perform(click());
    }

    @When("^I click on the foo keypair$")
    public void iClickTheFooKeypair() {
        onView(withText("foo")).perform(click());
    }

    @Then("^I should return from the key management activity$")
    public void iShouldReturnFromTheKeyManagementKeyActivity() {
        assertTrue(mActivityRule.getActivity().isFinishing());
    }

    @Then("^I should see an alert dialog$")
    public void iShouldSeeAnAlertDialog() {
        onView(withText("Test")).check(matches(isDisplayed()));
    }
}
