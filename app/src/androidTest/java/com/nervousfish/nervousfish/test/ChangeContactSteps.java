package com.nervousfish.nervousfish.test;

import android.content.Context;
import android.content.Intent;
import android.support.test.rule.ActivityTestRule;
import android.view.inputmethod.InputMethodManager;

import com.nervousfish.nervousfish.ConstantKeywords;
import com.nervousfish.nervousfish.R;
import com.nervousfish.nervousfish.activities.ChangeContactActivity;
import com.nervousfish.nervousfish.data_objects.Contact;
import com.nervousfish.nervousfish.data_objects.Ed25519Key;
import com.nervousfish.nervousfish.data_objects.IKey;
import com.nervousfish.nervousfish.data_objects.KeyPair;
import com.nervousfish.nervousfish.data_objects.Profile;
import com.nervousfish.nervousfish.modules.cryptography.KeyGeneratorAdapter;
import com.nervousfish.nervousfish.modules.database.IDatabase;
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
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.nervousfish.nervousfish.BaseTest.accessConstructor;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

@CucumberOptions(features = "features")
public class ChangeContactSteps {

    private final IServiceLocator serviceLocator = NervousFish.getServiceLocator();
    private final IKey key = new Ed25519Key("FTP", "ajfoJKFoeiSDFLow");
    private final Contact contact = new Contact("Illio", this.key);
    private InputMethodManager inputMethodManager;
    private static final String testpass = "Testpass";
    private String newName;

    @Rule
    public ActivityTestRule<ChangeContactActivity> mActivityRule =
            new ActivityTestRule<>(ChangeContactActivity.class, true, false);

    @Before
    public void createDatabase() throws Exception {
        final IDatabase database = serviceLocator.getDatabase();
        KeyGeneratorAdapter keyGen = (KeyGeneratorAdapter) accessConstructor(KeyGeneratorAdapter.class, serviceLocator);
        KeyPair keyPair = keyGen.generateRSAKeyPair("Test");
        Profile profile = new Profile("name", new ArrayList<KeyPair>());
        profile.addKeyPair(keyPair);
        database.createDatabase(profile, testpass);
        database.loadDatabase(testpass);
    }

    @After
    public void deleteDatabase() {
        final IDatabase database = serviceLocator.getDatabase();
        database.deleteDatabase();
    }

    @Given("^I am viewing the change contact activity$")
    public void iAmViewingChangeContactActivity() throws IOException {
        this.initDatabase();

        final Intent intent = new Intent();
        intent.putExtra(ConstantKeywords.CONTACT, this.contact);
        this.mActivityRule.launchActivity(intent);
        this.inputMethodManager = (InputMethodManager) this.mActivityRule.getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
    }

    @When("^I press the change contact back button$")
    public void iPressTheChangeContactBackButton() {
        onView(withId(R.id.back_button_change)).perform(click());
    }

    @When("^I press the save contact changes button$")
    public void iPressTheSaveContactButton() {
        onView(withId(R.id.save_contact_button)).perform(click());
    }

    @When("^I press OK on the change contact error popup$")
    public void iPressOKOnTheChangeContactErrorPopup() {
        onView(withText(R.string.dialog_ok)).perform(click());
    }

    @When("^I remove all text from the name$")
    public void iRemoveAllTextFromTheName() {
        onView(withId(R.id.edit_contact_name_input)).perform(replaceText(""));
    }

    @When("^I select the contact name$")
    public void iSelectTheContactName() {
        onView(withId(R.id.edit_contact_name_input)).perform(click());
    }

    @When("^I type (.*?) as new name$")
    public void iTypeNewNameAsNewName(final String newName) {
        this.newName = newName;
        onView(withId(R.id.edit_contact_name_input)).perform(replaceText(newName));
    }

    @When("^I select the IBAN field$")
    public void iSelectTheIBANField() {
        inputMethodManager.hideSoftInputFromWindow(mActivityRule.getActivity().getCurrentFocus().getWindowToken(), 0);
        onView(withId(R.id.contact_page_change_iban)).perform(click());
    }

    @When("^I type (.*?) as new IBAN")
    public void iTypeNewIBANAsNewIBAN(final String newIban) {
        onView(withId(R.id.contact_page_change_iban)).perform(replaceText(newIban));
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
        final IDatabase database = serviceLocator.getDatabase();
        assertNotNull(database.getContactWithName(this.newName));
    }

    /**
     * Initialize the database for the ChangeContactSteps.
     */
    private void initDatabase() throws IOException {
        final IDatabase database = serviceLocator.getDatabase();
        for (Contact contact : database.getAllContacts()) {
            database.deleteContact(contact.getName());
        }
        database.addContact(this.contact);
    }

}
