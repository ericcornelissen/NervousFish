package com.nervousfish.nervousfish.test;

import android.content.Intent;
import android.os.Build;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.NoMatchingViewException;
import android.support.test.rule.ActivityTestRule;
import android.util.Log;

import com.nervousfish.nervousfish.R;
import com.nervousfish.nervousfish.activities.ContactActivity;
import com.nervousfish.nervousfish.activities.LoginActivity;
import com.nervousfish.nervousfish.activities.MainActivity;
import com.nervousfish.nervousfish.activities.QRExchangeKeyActivity;
import com.nervousfish.nervousfish.activities.SettingsActivity;
import com.nervousfish.nervousfish.activities.WelcomeActivity;
import com.nervousfish.nervousfish.data_objects.Contact;
import com.nervousfish.nervousfish.data_objects.IKey;
import com.nervousfish.nervousfish.data_objects.KeyPair;
import com.nervousfish.nervousfish.data_objects.Profile;
import com.nervousfish.nervousfish.data_objects.RSAKey;
import com.nervousfish.nervousfish.modules.cryptography.KeyGeneratorAdapter;
import com.nervousfish.nervousfish.modules.database.IDatabase;
import com.nervousfish.nervousfish.service_locator.EntryActivity;
import com.nervousfish.nervousfish.service_locator.IServiceLocator;
import com.nervousfish.nervousfish.service_locator.NervousFish;

import org.junit.Rule;

import java.io.IOException;
import java.util.ArrayList;

import cucumber.api.CucumberOptions;
import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.nervousfish.nervousfish.BaseTest.accessConstructor;
import static org.hamcrest.core.AllOf.allOf;
import static org.hamcrest.core.StringEndsWith.endsWith;

@CucumberOptions(features = "features")
public class EntrySteps {


    @Rule
    public ActivityTestRule<EntryActivity> mActivityRule =
            new ActivityTestRule<>(EntryActivity.class, true, false);

    @After
    public void closeActivity() {
        pressBack();
    }

    @Given("^I am using the app for the first time$")
    public void iAmUsingAppFirstTime() {
        final IDatabase database = NervousFish.getServiceLocator().getDatabase();
        database.deleteDatabase();

        final Intent intent = new Intent();
        this.mActivityRule.launchActivity(intent);
    }

    @Given("^I am using the app not for the first time$")
    public void iAmUsingAppNotFirstTime() throws Exception {
        final IDatabase database = NervousFish.getServiceLocator().getDatabase();
        KeyGeneratorAdapter keyGen = (KeyGeneratorAdapter) accessConstructor(KeyGeneratorAdapter.class, NervousFish.getServiceLocator());
        KeyPair keyPair = keyGen.generateRSAKeyPair("Test");
        Profile profile = new Profile("name", new ArrayList<>());
        profile.addKeyPair(keyPair);
        database.createDatabase(profile, "Testpass");
        database.loadDatabase("Testpass");

        final Intent intent = new Intent();
        this.mActivityRule.launchActivity(intent);
    }
    @When("^I wait a very short time$")
    public void iWaitAVeryShortTime() throws Exception {
        Thread.sleep(100);
    }

    @Then("^I go to WelcomeActivity$")
    public void iShouldGoToWelcomeActivity() {
        intended(hasComponent(WelcomeActivity.class.getName()));
    }

    @Then("^I go to LoginActivity$")
    public void iShouldGoToLoginActivity() {
        intended(hasComponent(LoginActivity.class.getName()));
    }
}
