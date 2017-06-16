package com.nervousfish.nervousfish.test;

import android.content.Intent;
import android.support.test.rule.ActivityTestRule;

import com.nervousfish.nervousfish.R;
import com.nervousfish.nervousfish.activities.CreateProfileActivity;
import com.nervousfish.nervousfish.activities.WelcomeActivity;
import com.nervousfish.nervousfish.data_objects.Contact;
import com.nervousfish.nervousfish.data_objects.IKey;
import com.nervousfish.nervousfish.data_objects.KeyPair;
import com.nervousfish.nervousfish.data_objects.Profile;
import com.nervousfish.nervousfish.modules.cryptography.KeyGeneratorAdapter;
import com.nervousfish.nervousfish.modules.database.IDatabase;
import com.nervousfish.nervousfish.service_locator.NervousFish;

import org.junit.Before;
import org.junit.Rule;

import java.io.IOException;
import java.util.ArrayList;

import cucumber.api.CucumberOptions;
import cucumber.api.java.After;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.nervousfish.nervousfish.BaseTest.accessConstructor;

@CucumberOptions(features = "features")
public class WelcomeSteps {


    @Rule
    public ActivityTestRule<WelcomeActivity> mActivityRule =
            new ActivityTestRule<>(WelcomeActivity.class, true, false);

    @Before
    public void createDatabase() throws Exception {
        final IDatabase database = NervousFish.getServiceLocator().getDatabase();
        KeyGeneratorAdapter keyGen = (KeyGeneratorAdapter) accessConstructor(KeyGeneratorAdapter.class, NervousFish.getServiceLocator());
        KeyPair keyPair = keyGen.generateRSAKeyPair("Test");
        Profile profile = new Profile("name", new ArrayList<KeyPair>());
        profile.addKeyPair(keyPair);
        database.createDatabase(profile, "Testpass");
        database.loadDatabase("Testpass");
    }

    @After
    public void deleteDatabase() {
        final IDatabase database = NervousFish.getServiceLocator().getDatabase();

        database.deleteDatabase();
    }

    @Given("^I am viewing welcome activity$")
    public void iAmViewingTheFirstUseActivity() throws IOException {
        final Intent intent = new Intent();
        this.mActivityRule.launchActivity(intent);
    }

    @When("^I click on the get started button$")
    public void iClickGetStarted() {
        onView(withId(R.id.get_started)).perform(click());
    }

    @Then("^I should go to the CreateProfileActivity$")
    public void iShouldGoToCreateProfileActivity() {
        intended(hasComponent(CreateProfileActivity.class.getName()));
    }

}