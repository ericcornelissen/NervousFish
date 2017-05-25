package com.nervousfish.nervousfish.test;

import android.content.Intent;
import android.support.test.rule.ActivityTestRule;

import com.nervousfish.nervousfish.ConstantKeywords;
import com.nervousfish.nervousfish.R;
import com.nervousfish.nervousfish.TestServiceLocator;
import com.nervousfish.nervousfish.activities.ChangeContactActivity;
import com.nervousfish.nervousfish.data_objects.Contact;
import com.nervousfish.nervousfish.data_objects.IKey;
import com.nervousfish.nervousfish.service_locator.IServiceLocator;

import org.junit.Rule;

import java.io.IOException;
import java.util.Collection;

import cucumber.api.CucumberOptions;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static junit.framework.Assert.assertTrue;

@CucumberOptions(features = "features")
public class ChangeContactSteps {

    private final IServiceLocator serviceLocator = new TestServiceLocator();

    private Contact contact;
    private Collection<IKey> keys;
    private String newName;

    @Rule
    public ActivityTestRule<ChangeContactActivity> mActivityRule =
            new ActivityTestRule<>(ChangeContactActivity.class, true, false);

    @Given("^I am viewing the change contact activity$")
    public void iAmViewingChangeContactActivity() throws IOException {
        this.contact = serviceLocator.getDatabase().getAllContacts().get(0);
        this.keys = this.contact.getKeys();

        final Intent intent = new Intent();
        intent.putExtra(ConstantKeywords.SERVICE_LOCATOR, this.serviceLocator);
        intent.putExtra(ConstantKeywords.CONTACT, this.contact);
        mActivityRule.launchActivity(intent);
    }

    @When("^I press the change contact back button$")
    public void iPressTheChangeContactBackButton() {
        onView(withId(R.id.backButtonChange)).perform(click());
    }

    @When("^I press the save contact changes button$")
    public void iPressTheSaveContactButton() {
        onView(withId(R.id.saveContactButton)).perform(click());
    }

    @When("^I press OK on the change contact error popup$")
    public void iPressOKOnTheChangeContactErrorPopup() {
        onView(withText(R.string.dialog_ok)).perform(click());
    }

    @When("^I remove all text from the name$")
    public void iRemoveAllTextFromTheName() {
        onView(withId(R.id.edit_contact_name)).perform(replaceText(""));
    }

    @When("^I select the contact name$")
    public void iSelectTheContactName() {
        onView(withId(R.id.edit_contact_name)).perform(click());
    }

    @When("^I type (.*?) as new name$")
    public void iTypeNewNameAsNewName(final String newName) {
        this.newName = newName;
        onView(withId(R.id.edit_contact_name)).perform(replaceText(newName));
    }

    @When("^I verify that I want to dismiss the contact changes$")
    public void iVerifyThatIWantToDismissTheContactChanges() {
        onView(withText(R.string.yes_go_back)).perform(click());
    }

    @Then("^I should go to the activity I visited before the change contact activity$")
    public void iShouldGoToTheActivityIVisitedBeforeTheChangeContactActivity() {
        assertTrue(mActivityRule.getActivity().isFinishing());
    }

    @Then("^I should stay in the contact activity$")
    public void iShouldStayInTheContactActivity() {
        intended(hasComponent(ChangeContactActivity.class.getName()));
    }

    @Then("^the contact should be updated$")
    public void theContactShouldBeUpdated() throws IOException {
        // TODO: Add assertion
//        Contact updatedContact = new Contact(this.newName, this.keys);
//        assertTrue(serviceLocator.getDatabase().getAllContacts().contains(updatedContact));
    }

}
