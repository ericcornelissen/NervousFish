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
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static junit.framework.Assert.assertEquals;

@CucumberOptions(features = "features")
public class ChangeContactActivitySteps {

    private final IServiceLocator serviceLocator = new TestServiceLocator();
    private final String contactName = "Henk";
    private final IKey key = new SimpleKey("Webserver", "aDsfOIHiow093h0HGIHSDGi03tj");
    private final Contact contact = new Contact(this.contactName, this.key);

    private String newName;

    @Rule
    public ActivityTestRule<ChangeContactActivity> mActivityRule =
            new ActivityTestRule<>(ChangeContactActivity.class, true, false);

    @Given("^I am viewing the change contact activity$")
    public void iAmViewingChangeContactActivity() throws IOException {
        final Intent intent = new Intent();
        intent.putExtra(ConstantKeywords.SERVICE_LOCATOR, this.serviceLocator);
        intent.putExtra(ConstantKeywords.CONTACT, contact);
        mActivityRule.launchActivity(intent);
    }

    @When("^I press the contact name$")
    public void iPressContactName() {
        onView(withId(R.id.edit_contact_name)).perform(click());
    }

    @When("^I type \"(.*?)\"$")
    public void iTypeDifferentName(final String newName) {
        this.newName = newName;
        onView(withId(R.id.edit_contact_name)).perform(replaceText(newName));
    }

    @When("^I press on the save button$")
    public void iPressSave() {
        onView(withId(R.id.saveContactButton)).perform(click());
    }

    @Then("^the contact should be updated$")
    public void theContactShouldBeUpdated() throws IOException {
        Contact updatedContact = new Contact(this.newName, key);
        assertEquals(updatedContact, serviceLocator.getDatabase().getContactWithName(this.newName));
    }

    @When("^I change the name$")
    public void iChangeTheName() {
        onView(withId(R.id.edit_contact_name)).perform(typeText("aabbcc"));
    }

    @When("^I close the keyboard$")
    public void iCloseKeyboard() {
        onView(withId(R.id.edit_contact_name)).perform(closeSoftKeyboard());
    }

    @When("^I press the back button$")
    public void iPressBack() {
        onView(withId(R.id.backButtonChange)).perform(click());
    }

    @When("^I press cancel on the popup$")
    public void iPressCancelPopup() {
        onView(withId(R.id.cancel_button)).perform(click());
    }

    @When("^I press yes go back on the popup$")
    public void iPressYesGoBack() {
        onView(withId(R.id.confirm_button)).perform(click());
    }

    @When("^I press confirm on the popup$")
    public void iPressConfirm() {
        onView(withId(R.id.confirm_button)).perform(click());
    }

    @Then("^I should stay on the page$")
    public void iShouldStayOnPage() {
        intended(hasComponent(ChangeContactActivity.class.getName()));
    }

    @Then("^an error should be raised that the name is invalid$")
    public void raiseErrorInvalidName() {
        // TODO: Add assertion
    }

    @Then("^I should go to the previous activity$")
    public void iShouldGoToThePreviousActivity() {
        intended(hasComponent(ContactActivity.class.getName()));
    }

}
