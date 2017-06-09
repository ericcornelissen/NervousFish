package com.nervousfish.nervousfish.test;

import android.content.Intent;
import android.support.test.espresso.NoMatchingViewException;
import android.support.test.rule.ActivityTestRule;

import com.nervousfish.nervousfish.BaseTest;
import com.nervousfish.nervousfish.ConstantKeywords;
import com.nervousfish.nervousfish.R;
import com.nervousfish.nervousfish.activities.ContactActivity;
import com.nervousfish.nervousfish.activities.LoginActivity;
import com.nervousfish.nervousfish.activities.MainActivity;
import com.nervousfish.nervousfish.activities.QRExchangeKeyActivity;
import com.nervousfish.nervousfish.data_objects.Contact;
import com.nervousfish.nervousfish.data_objects.IKey;
import com.nervousfish.nervousfish.data_objects.RSAKey;
import com.nervousfish.nervousfish.modules.database.IDatabase;
import com.nervousfish.nervousfish.service_locator.IServiceLocator;
import com.nervousfish.nervousfish.service_locator.ServiceLocator;

import org.junit.Rule;

import java.io.IOException;

import cucumber.api.CucumberOptions;
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
import static org.hamcrest.core.AllOf.allOf;
import static org.hamcrest.core.StringEndsWith.endsWith;

@CucumberOptions(features = "features")
public class MainSteps {

    private final IServiceLocator serviceLocator = (IServiceLocator) BaseTest.accessConstructor(ServiceLocator.class, Instrumentation.filesDir);

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule =
            new ActivityTestRule<>(MainActivity.class, true, false);

    @Given("^I am viewing the main activity$")
    public void iAmViewingMainActivity() {
        final Intent intent = new Intent();
        intent.putExtra(ConstantKeywords.SERVICE_LOCATOR, this.serviceLocator);
        this.mActivityRule.launchActivity(intent);
      
        try {
            onView(withText(R.string.no)).perform(click());
        } catch (NoMatchingViewException ignore) { }
    }

    @Given("^there is a contact with the name (.*?) in the database$")
    public void thereIsAContactInTheDatabaseWithTheName(final String name) throws IOException {
        final IKey key = new RSAKey("Email", "42", "13");
        final Contact contact = new Contact(name, key);

        final IDatabase database = this.serviceLocator.getDatabase();
        database.addContact(contact);
    }

    @When("^I click the back button in main and go to the LoginActivity$")
    public void iPressTheChangeContactBackButton() {
        pressBack();
    }

    @When("^I verify that I want to log out$")
    public void iVerifyThatIWantToLogOut() {
        onView(withText(R.string.yes)).perform(click());
    }

    @When("^I verify that I do not want to log out$")
    public void iVerifyThatIDoNotWantToLogOut() {
        onView(withText(R.string.no)).perform(click());
    }

    @When("^I click open buttons with the plus$")
    public void clickPlusButton() {
        onView(allOf(withParent(withId(R.id.pairing_button)), withClassName(endsWith("ImageView")), isDisplayed()))
                .perform(click());
    }

    @When("^I click the button with the QR icon$")
    public void iClickQRButton() {
        onView(withId(R.id.pairing_menu_qr)).perform(click());
    }

    @When("^I click on the contact with the name (.*?)$")
    public void iClockOnTheContactWithTheName(final String name) {
        onView(withText(name)).perform(click());
    }

    @When("^I click the button with the QR text label$")
    public void iClickQRLabel() {
        onView(withText(R.string.qr)).perform(click());
    }

    @Then("^I should stay in the main activity after pressing back$")
    public void iShouldStayInTheMainActivity() {
        intended(hasComponent(MainActivity.class.getName()));
    }

    @Then("^I should go to the login activity after pressing back$")
    public void iShouldGoToTheLoginActivity() {
        intended(hasComponent(LoginActivity.class.getName()));
    }

    @Then("^I should go to the QR activity from main$")
    public void iShouldGoToTheQRActivity() {
        intended(hasComponent(QRExchangeKeyActivity.class.getName()));
    }

    @Then("^I should go to the contact activity from main$")
    public void iShouldGoToTheContactActivity() {
        intended(hasComponent(ContactActivity.class.getName()));
    }

}
