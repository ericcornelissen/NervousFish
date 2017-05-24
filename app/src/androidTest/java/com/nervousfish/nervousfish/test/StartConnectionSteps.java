package com.nervousfish.nervousfish.test;

import android.content.Intent;
import android.support.test.rule.ActivityTestRule;

import com.nervousfish.nervousfish.ConstantKeywords;
import com.nervousfish.nervousfish.R;
import com.nervousfish.nervousfish.TestServiceLocator;
import com.nervousfish.nervousfish.activities.BluetoothConnectionActivity;
import com.nervousfish.nervousfish.activities.MainActivity;
import com.nervousfish.nervousfish.service_locator.IServiceLocator;

import org.junit.Rule;

import cucumber.api.CucumberOptions;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static junit.framework.Assert.assertTrue;

@CucumberOptions(features = "features")
public class StartConnectionSteps {

    private final IServiceLocator serviceLocator = new TestServiceLocator();

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule =
            new ActivityTestRule<>(MainActivity.class, true, false);

    @Given("^I am viewing the main activity$")
    public void iAmViewingMainActivity() {
        final Intent intent = new Intent();
        intent.putExtra(ConstantKeywords.SERVICE_LOCATOR, this.serviceLocator);
        mActivityRule.launchActivity(intent);
    }

    @When("^I press the connect button")
    public void iPressTheConnectButton() {
        onView(withId(R.id.fab)).perform(click());
    }

    @When("^I select the Bluetooth as connection method$")
    public void iSelectBluetoothAsConnectionMethod() {
        // TODO: update when the fab is updated in #148
    }

    @Then("^I go to the BluetoothConnectionActivity$")
    public void iGoToTheBluetoothConnectionActivity() {
        intended(hasComponent(BluetoothConnectionActivity.class.getName()));
    }

}
