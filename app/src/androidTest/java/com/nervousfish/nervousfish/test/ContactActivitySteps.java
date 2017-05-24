package com.nervousfish.nervousfish.test;

import android.content.Intent;
import android.support.test.rule.ActivityTestRule;

import com.nervousfish.nervousfish.ConstantKeywords;
import com.nervousfish.nervousfish.R;
import com.nervousfish.nervousfish.TestServiceLocator;
import com.nervousfish.nervousfish.activities.ChangeContactActivity;
import com.nervousfish.nervousfish.activities.ContactActivity;
import com.nervousfish.nervousfish.data_objects.Contact;
import com.nervousfish.nervousfish.data_objects.IKey;
import com.nervousfish.nervousfish.data_objects.SimpleKey;
import com.nervousfish.nervousfish.service_locator.IServiceLocator;

import org.junit.Rule;

import java.io.IOException;

import cucumber.api.CucumberOptions;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

@CucumberOptions(features = "features")
public class ContactActivitySteps {

    private final IServiceLocator serviceLocator = new TestServiceLocator();
    private final String contactName = "Henk";
    private final IKey key = new SimpleKey("Webserver", "aDsfOIHiow093h0HGIHSDGi03tj");
    private final Contact contact = new Contact(contactName, key);

    @Rule
    public ActivityTestRule<ContactActivity> mActivityRule = new ActivityTestRule<>(ContactActivity.class, true, false);

    @Given("^I am viewing the contact activity$")
    public void iAmViewingContactActivity() throws IOException {
        final Intent intent = new Intent();
        intent.putExtra(ConstantKeywords.SERVICE_LOCATOR, this.serviceLocator);
        intent.putExtra(ConstantKeywords.CONTACT, contact);
        mActivityRule.launchActivity(intent);
    }

    @When("^I press the back arrow$")
    public void iPressBackArrow() {
        onView(withId(R.id.backButtonChange)).perform(click());
    }

    @When("^I press the menu$")
    public void iPressMenu() {
        onView(withId(R.id.edit_menu_button)).perform(click());
    }

    @When("^I press delete$")
    public void iPressDeleteButton() {
        onView(withText("Delete")).perform(click());
    }

    @When("^I press that I am sure$")
    public void iAmSureToDelete() {
        onView(withId(R.id.confirm_button)).perform(click());
    }

    @When("^I press on the OK button$")
    public void iPressOK() {
        onView(withId(R.id.confirm_button)).perform(click());
    }

    @When("^I press edit$")
    public void iPressEdit() {
        onView(withText("Edit")).perform(click());
    }

    @Then("^I should go to the previous activity I visited$")
    public void iShouldGoToPreviousActivity() {
        intended(hasComponent(ContactActivity.class.getName()));
    }

    @Then("^the current contact should be deleted$")
    public void currentContactIsDeleted() throws IOException {
        // TODO: Add assertion
    }

    @Then("^I should go to the change contact activity$")
    public void iShouldGoToChangeContactActivity() {
        intended(hasComponent(ChangeContactActivity.class.getName()));
    }

}
