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
    public ActivityTestRule<ContactActivity> mActivityRule =
            new ActivityTestRule<>(ContactActivity.class, true, false);

    @Given("^I am viewing the contact activity$")
    public void iAmViewingTheContactActivity() {
        final Intent intent = new Intent();
        intent.putExtra(ConstantKeywords.SERVICE_LOCATOR, this.serviceLocator);
        intent.putExtra(ConstantKeywords.CONTACT, contact);
        mActivityRule.launchActivity(intent);
    }

    @When("^I confirm the contact is deleted$")
    public void iConfirmTheContactIsDeleted() {
        onView(withId(R.id.confirm_button)).perform(click());
    }

    @When("^I open the contact activity menu$")
    public void iOpenTheContactActivityMenu() {
        onView(withId(R.id.edit_menu_button)).perform(click());
    }

    @When("^I press the contact activity back button")
    public void iPressTheContactActivityBackButton() {
        onView(withId(R.id.backButtonChange)).perform(click());
    }

    @When("^I select delete contact$")
    public void iPressDeleteButton() {
        onView(withText("Delete")).perform(click());
    }

    @When("^I select edit contact$")
    public void iSelectEditContact() {
        onView(withText("Edit")).perform(click());
    }

    @When("^I verify that I am sure I want to delete the contact$")
    public void iVerifyThatIAmSureIWantToDeleteTheContact() {
        onView(withId(R.id.confirm_button)).perform(click());
    }

    @Then("^I should go to the change contact activity$")
    public void iShouldGoToChangeContactActivity() {
        intended(hasComponent(ChangeContactActivity.class.getName()));
    }

    @Then("^I should go to the activity I visited before the contact activity$")
    public void iShouldGoToTheActivityIVisitedBeforeTheContactActivity() {
        intended(hasComponent(ContactActivity.class.getName()));
    }

    @Then("^the contact should be deleted$")
    public void theContactShouldBeDeleted() {
        // TODO: Add assertion
    }

}
