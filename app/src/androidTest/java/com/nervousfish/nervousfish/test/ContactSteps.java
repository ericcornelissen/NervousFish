package com.nervousfish.nervousfish.test;

import android.app.Activity;
import android.content.Intent;
import android.support.test.rule.ActivityTestRule;

import com.nervousfish.nervousfish.BaseTest;
import com.nervousfish.nervousfish.ConstantKeywords;
import com.nervousfish.nervousfish.R;
import com.nervousfish.nervousfish.activities.ChangeContactActivity;
import com.nervousfish.nervousfish.activities.ContactActivity;
import com.nervousfish.nervousfish.data_objects.Contact;
import com.nervousfish.nervousfish.data_objects.IKey;
import com.nervousfish.nervousfish.data_objects.KeyPair;
import com.nervousfish.nervousfish.data_objects.Profile;
import com.nervousfish.nervousfish.data_objects.Ed25519Key;
import com.nervousfish.nervousfish.modules.cryptography.KeyGeneratorAdapter;
import com.nervousfish.nervousfish.modules.database.IDatabase;
import com.nervousfish.nervousfish.service_locator.IServiceLocator;
import com.nervousfish.nervousfish.service_locator.NervousFish;
import com.nervousfish.nervousfish.service_locator.ServiceLocatorNoNetwork;

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
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.nervousfish.nervousfish.BaseTest.accessConstructor;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

@CucumberOptions(features = "features")
public class ContactSteps {

    private final IServiceLocator serviceLocator = (IServiceLocator) BaseTest.accessConstructor(ServiceLocatorNoNetwork.class, Instrumentation.filesDir);
    private final IKey key = new Ed25519Key("Webserver", "aDsfOIHiow093h0HGIHSDGi03tj");
    private final Contact contact = new Contact("Yashuo", this.key);

    @Rule
    public ActivityTestRule<ContactActivity> mActivityRule =
            new ActivityTestRule<>(ContactActivity.class, true, false);

    @Before
    public void createDatabase() throws Exception {
        final IDatabase database = serviceLocator.getDatabase();
        KeyGeneratorAdapter keyGen = (KeyGeneratorAdapter) accessConstructor(KeyGeneratorAdapter.class, serviceLocator);
        KeyPair keyPair = keyGen.generateRSAKeyPair("Test");
        Contact contact = new Contact("name", new ArrayList<IKey>());
        Profile profile = new Profile(contact, new ArrayList<KeyPair>());
        profile.addKeyPair(keyPair);
        database.createDatabase(profile, "Testpass");
        database.loadDatabase("Testpass");
    }

    @After
    public void reinitializeDatabase() {
        final IDatabase database = serviceLocator.getDatabase();
        database.deleteDatabase();
    }

    @Given("^I am viewing the contact activity$")
    public void iAmViewingTheContactActivity() throws IOException {
        this.initDatabase();
        final Intent intent = new Intent();
        intent.putExtra(ConstantKeywords.CONTACT, this.contact);
        this.mActivityRule.launchActivity(intent);
    }

    @When("^I confirm the contact is deleted$")
    public void iConfirmTheContactIsDeleted() {
        onView(withId(R.id.confirm_button)).perform(click());
    }

    @When("^I open the contact activity menu$")
    public void iOpenTheContactActivityMenu() {
        onView(withId(R.id.edit_menu_button)).perform(click());
    }

    @When("^I press the back button in the contact activity$")
    public void iPressTheContactActivityBackButton() {
        onView(withId(R.id.back_button_change)).perform(click());
    }

    @When("^I select delete contact$")
    public void iPressDeleteButton() {
        onView(withText(R.string.delete)).perform(click());
    }

    @When("^I select edit contact$")
    public void iSelectEditContact() {
        onView(withText(R.string.edit)).perform(click());
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
        final Activity activity = this.mActivityRule.getActivity();
        assertTrue(activity.isFinishing());
    }

    @Then("^the contact should be deleted$")
    public void theContactShouldBeDeleted() throws IOException {
        IDatabase database = serviceLocator.getDatabase();
        assertFalse(database.contactExists(this.contact.getName()));
        database.deleteDatabase();
    }

    /**
     * Initialize the database for the ContactSteps.
     */
    private void initDatabase() throws IOException {
        final IDatabase database = serviceLocator.getDatabase();

        for (Contact contact : database.getAllContacts()) {
            database.deleteContact(contact.getName());
        }
        database.addContact(this.contact);
    }

}
