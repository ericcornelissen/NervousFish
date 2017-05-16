package com.nervousfish.nervousfish.test;


import android.app.Activity;
import android.content.Intent;
import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.core.deps.guava.collect.Iterables;
import android.support.test.runner.lifecycle.ActivityLifecycleMonitorRegistry;
import android.support.test.runner.lifecycle.Stage;
import android.test.ActivityInstrumentationTestCase2;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.nervousfish.nervousfish.ConstantKeywords;
import com.nervousfish.nervousfish.R;
import com.nervousfish.nervousfish.activities.ContactActivity;
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
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isClickable;
import static android.support.test.espresso.matcher.ViewMatchers.isEnabled;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.core.AllOf.allOf;

@CucumberOptions(features = "features")
public class ContactActivitySteps extends ActivityInstrumentationTestCase2<EntryActivity> {

    private Contact contact = null;
    private IServiceLocator serviceLocator = null;
    private final static String TEST_NAME = "TestPerson";
    private final static IKey TEST_KEY = new SimpleKey("my key", "key");
    private final static String DIFFERENT_NAME = "DifferentName";

    public ContactActivitySteps(EntryActivity activityClass) {
        super(EntryActivity.class);
    }

    private static Matcher<? super View> hasErrorText(final String expectedError) {
        return new ErrorTextMatcher(expectedError);
    }

    @Given("^I am viewing the contact activity$")
    public void iAmViewingContactActivity() throws IOException {
        assertNotNull(getActivity());

        contact = new Contact(TEST_NAME, TEST_KEY);

        serviceLocator = (IServiceLocator) getCurrentActivity().getIntent().getSerializableExtra(ConstantKeywords.SERVICE_LOCATOR);
        List<Contact> contacts = serviceLocator.getDatabase().getAllContacts();
        for (Contact contact: contacts) {
            serviceLocator.getDatabase().deleteContact(contact.getName());
        }
        serviceLocator.getDatabase().addContact(contact);

        Intent intent = new Intent(getActivity(), ContactActivity.class);
        intent.putExtra(ConstantKeywords.CONTACT, contact);
        intent.putExtra(ConstantKeywords.SERVICE_LOCATOR, serviceLocator);
        getActivity().startActivity(intent);
    }

    @When("^I press the back arrow$")
    public void iPressBackArrow() {
        onView(withId(R.id.backButton)).perform(click());
    }

    @When("^I press the delete button$")
    public void iPressDeleteButton() {
        onView(withId(R.id.deleteButton)).perform(click());
    }

    @When("^I press that I am sure$")
    public void iAmSureToDelete() {
        onView(withId(R.id.confirm_button)).perform(click());
    }

    @When("^I press on the OK button$")
    public void iPressOK() {
        onView(withId(R.id.confirm_button)).perform(click());
    }

    @Then("^I should go to the previous activity I visited$")
    public void iShouldGoToPreviousActivity() {
        assertEquals(LoginActivity.class, getCurrentActivity().getClass());
    }

    @Then("^the current contact should be deleted$")
    public void currentContactIsDeleted() throws IOException {
        assertFalse(serviceLocator.getDatabase().getAllContacts().contains(contact));
    }

    @When("^I press the contact name$")
    public void iPressContactName() {
        onView(withId(R.id.contact_name)).perform(click());
    }

    @When("^I type a different name$")
    public void iTypeDifferentName() {
        onView(withId(R.id.edit_contact_name)).perform(replaceText(DIFFERENT_NAME));
    }

    @When("^I press on the save button$")
    public void iPressSave() {
        onView(withId(R.id.save_button)).check(matches(allOf( isEnabled(), isClickable()))).perform(
                new ViewAction() {
                    @Override
                    public Matcher<View> getConstraints() {
                        return isEnabled(); // no constraints, they are checked above
                    }

                    @Override
                    public String getDescription() {
                        return "click plus button";
                    }

                    @Override
                    public void perform(UiController uiController, View view) {
                        view.performClick();
                    }
                }
        );
    }

    @Then("^the contact should be updated$")
    public void theContactShouldBeUpdated() throws IOException {
        assertEquals(serviceLocator.getDatabase().getContactWithName(TEST_NAME), null);
        assertEquals(serviceLocator.getDatabase().getContactWithName(DIFFERENT_NAME).getKeys().get(0), TEST_KEY);
    }

    @Then("^I should see the new contact name$")
    public void iShouldSeeNewContactName() {
        assertEquals(
                ((TextView)getCurrentActivity().findViewById(R.id.contact_name)).getText().toString(),
                DIFFERENT_NAME);
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
