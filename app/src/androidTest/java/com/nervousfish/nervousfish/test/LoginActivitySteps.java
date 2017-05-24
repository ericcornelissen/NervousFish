package com.nervousfish.nervousfish.test;

import android.app.Activity;
import android.content.Intent;
import android.support.test.espresso.intent.Intents;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.nervousfish.nervousfish.ConstantKeywords;
import com.nervousfish.nervousfish.R;
import com.nervousfish.nervousfish.activities.LoginActivity;
import com.nervousfish.nervousfish.data_objects.Contact;
import com.nervousfish.nervousfish.data_objects.Profile;
import com.nervousfish.nervousfish.modules.constants.IConstants;
import com.nervousfish.nervousfish.modules.cryptography.IEncryptor;
import com.nervousfish.nervousfish.modules.cryptography.IKeyGenerator;
import com.nervousfish.nervousfish.modules.database.IDatabase;
import com.nervousfish.nervousfish.modules.filesystem.IFileSystem;
import com.nervousfish.nervousfish.modules.pairing.IBluetoothHandler;
import com.nervousfish.nervousfish.modules.pairing.INfcHandler;
import com.nervousfish.nervousfish.modules.pairing.IQRHandler;
import com.nervousfish.nervousfish.service_locator.IServiceLocator;

import org.junit.Rule;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.List;

import cucumber.api.CucumberOptions;
import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;

import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static junit.framework.Assert.assertEquals;

@CucumberOptions(features = "features")
public class LoginActivitySteps {
    private static final Activity[] activity = new Activity[1];

    public static final String CORRECT_PASSWORD = "12345";

    @Rule
    public ActivityTestRule<LoginActivity> mActivityRule = new ActivityTestRule<>(LoginActivity.class, true, false);

    @Before
    public void setUp() throws Exception {
        Intents.init();
    }

    @After
    public void tearDown() {
        Intents.release();
    }

    @Given("^I have a LoginActivity$")
    public void iHaveALoginActivity() throws IOException {
        final IServiceLocator serviceLocator = new ServiceLocatorTest();
        final Intent intent = new Intent();

        intent.putExtra(ConstantKeywords.SERVICE_LOCATOR, serviceLocator);
        mActivityRule.launchActivity(intent);
    }

    @When("^I input password \"(.*?)\"$")
    public void iInputPassword(final String password) {
        onView(withId(R.id.login_password_input)).perform(typeText(password));
    }

    @When("^I press submit button$")
    public void iPressSubmit() {
        onView(withId(R.id.submit)).perform(scrollTo()).perform(click());
    }

    @Then("^I should stay in the LoginActivity$")
    public void iShouldStayInLoginActivity() {
        Activity activity = mActivityRule.getActivity();
        assertEquals(activity.getClass(), LoginActivity.class);
    }

    @Then("^I should see an auth error$")
    public void iShouldSeeAuthError() {
        onView(withId(R.id.error)).check(matches(isDisplayed()));
    }

}


class ServiceLocatorTest implements IServiceLocator {

    @Override
    public String getAndroidFilesDir() {
        return null;
    }

    @Override
    public IDatabase getDatabase() {
        return new IDatabase() {
            @Override
            public void addContact(Contact contact) throws IOException {

            }

            @Override
            public void deleteContact(String contactName) throws IllegalArgumentException, IOException {

            }

            @Override
            public void updateContact(Contact oldContact, Contact newContact) throws IllegalArgumentException, IOException {

            }

            @Override
            public List<Contact> getAllContacts() throws IOException {
                return null;
            }

            @Override
            public Contact getContactWithName(String contactName) throws IOException {
                return null;
            }

            @Override
            public boolean contactExtists(String name) throws IOException {
                return false;
            }

            @Override
            public List<Profile> getProfiles() throws IOException {
                return null;
            }

            @Override
            public void addProfile(Profile profile) throws IOException {

            }

            @Override
            public void deleteProfile(Profile profile) throws IllegalArgumentException, IOException {

            }

            @Override
            public void updateProfile(Profile oldProfile, Profile newProfile) throws IllegalArgumentException, IOException {

            }

            @Override
            public String getUserPassword() throws IOException {
                return LoginActivitySteps.CORRECT_PASSWORD;
            }
        };
    }

    @Override
    public IKeyGenerator getKeyGenerator() {
        return null;
    }

    @Override
    public IEncryptor getEncryptor() {
        return null;
    }

    @Override
    public IFileSystem getFileSystem() {
        return null;
    }

    @Override
    public IConstants getConstants() {
        return null;
    }

    @Override
    public IBluetoothHandler getBluetoothHandler() {
        return null;
    }

    @Override
    public INfcHandler getNFCHandler() {
        return null;
    }

    @Override
    public IQRHandler getQRHandler() {
        return null;
    }

    @Override
    public void registerToEventBus(Object object) {

    }

    @Override
    public void unregisterFromEventBus(Object object) {

    }

    @Override
    public void postOnEventBus(Object message) {

    }

};