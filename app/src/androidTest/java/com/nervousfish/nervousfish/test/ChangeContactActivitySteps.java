package com.nervousfish.nervousfish.test;


import android.app.Activity;
import android.content.Intent;
import android.support.test.espresso.core.deps.guava.collect.Iterables;
import android.support.test.runner.lifecycle.ActivityLifecycleMonitorRegistry;
import android.support.test.runner.lifecycle.Stage;
import android.test.ActivityInstrumentationTestCase2;
import android.view.View;
import android.widget.EditText;

import com.nervousfish.nervousfish.ConstantKeywords;
import com.nervousfish.nervousfish.R;
import com.nervousfish.nervousfish.activities.ChangeContactActivity;
import com.nervousfish.nervousfish.activities.LoginActivity;
import com.nervousfish.nervousfish.data_objects.Contact;
import com.nervousfish.nervousfish.data_objects.IKey;
import com.nervousfish.nervousfish.data_objects.SimpleKey;
import com.nervousfish.nervousfish.service_locator.EntryActivity;
import com.nervousfish.nervousfish.service_locator.IServiceLocator;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.internal.matchers.TypeSafeMatcher;

import java.io.IOException;
import java.util.List;

import cucumber.api.CucumberOptions;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.core.AllOf.allOf;

@CucumberOptions(features = "features")
public class ChangeContactActivitySteps extends ActivityInstrumentationTestCase2<EntryActivity> {

    private IServiceLocator serviceLocator = null;
    private final static String TEST_NAME = "TestPerson";
    private final static IKey TEST_KEY = new SimpleKey("my key", "key");
    private String differentname;
    private Activity previousActivity;

    public ChangeContactActivitySteps(EntryActivity activityClass) {
        super(EntryActivity.class);
    }

    private static Matcher<? super View> hasErrorText(final String expectedError) {
        return new ErrorTextMatcher(expectedError);
    }

    @Given("^I am viewing the change contact activity$")
    public void iAmViewingChangeContactActivity() throws IOException {
        assertNotNull(getActivity());

        final Contact contact = new Contact(TEST_NAME, TEST_KEY);

        serviceLocator = (IServiceLocator) getCurrentActivity().getIntent().getSerializableExtra(ConstantKeywords.SERVICE_LOCATOR);
        List<Contact> contacts = serviceLocator.getDatabase().getAllContacts();
        for (Contact c: contacts) {
            serviceLocator.getDatabase().deleteContact(c.getName());
        }
        serviceLocator.getDatabase().addContact(contact);

        previousActivity = getCurrentActivity();
        Intent intent = new Intent(getActivity(), ChangeContactActivity.class);
        intent.putExtra(ConstantKeywords.CONTACT, contact);
        intent.putExtra(ConstantKeywords.SERVICE_LOCATOR, serviceLocator);
        getActivity().startActivity(intent);
    }

    @When("^I press the contact name$")
    public void iPressContactName() {
        onView(withId(R.id.edit_contact_name)).perform(click());
    }

    @When("^I type a \"(.*?)\"$")
    public void iTypeDifferentName(final String differentname) {
        this.differentname = differentname;
        onView(withId(R.id.edit_contact_name)).perform(replaceText(differentname));
    }

    @When("^I press on the save button$")
    public void iPressSave() {
        onView(withId(R.id.saveContactButton)).perform(click());
    }

    @Then("^the contact should be updated$")
    public void theContactShouldBeUpdated() throws IOException {
        try {
            serviceLocator.getDatabase().getContactWithName(TEST_NAME);

        } catch (IllegalArgumentException e) {
            assertEquals(serviceLocator.getDatabase().getContactWithName(differentname).getKeys().get(0), TEST_KEY);
        }
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
        assertEquals(ChangeContactActivity.class, getCurrentActivity().getClass());
    }

    @Then("^an error should be raised that the name is invalid$")
    public void raiseErrorInvalidName() {
        assertEquals(ChangeContactActivity.class, getCurrentActivity().getClass());
    }

    @Then("^I should go to the previous activity$")
    public void iShouldGoToPrevActivity() {
        assertEquals(previousActivity.getClass(), getCurrentActivity().getClass());
    }

    private Activity getCurrentActivity() {
        getInstrumentation().waitForIdleSync();
        final Activity[] activity = new Activity[1];
        try {
            runTestOnUiThread(new Runnable() {
                @Override
                public void run() {
                    java.util.Collection<Activity> activities = ActivityLifecycleMonitorRegistry.getInstance().getActivitiesInStage(Stage.RESUMED);
                    activity[0] = Iterables.getOnlyElement(activities);
                }
            });
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return activity[0];
    }

    /**
     * Custom matcher to assert equal EditText.setError();
     */
    private static class ErrorTextMatcher extends TypeSafeMatcher<View> {

        private final String mExpectedError;

        private ErrorTextMatcher(String expectedError) {
            mExpectedError = expectedError;
        }

        @Override
        public boolean matchesSafely(View view) {
            if (!(view instanceof EditText)) {
                return false;
            }

            EditText editText = (EditText) view;

            return mExpectedError.equals(editText.getError());
        }

        @Override
        public void describeTo(Description description) {
            description.appendText("with error: " + mExpectedError);
        }
    }
}
