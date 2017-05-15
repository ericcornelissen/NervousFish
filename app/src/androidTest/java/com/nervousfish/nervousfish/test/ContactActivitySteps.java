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
import com.nervousfish.nervousfish.activities.ContactActivity;
import com.nervousfish.nervousfish.activities.LoginActivity;
import com.nervousfish.nervousfish.data_objects.Contact;
import com.nervousfish.nervousfish.data_objects.IKey;
import com.nervousfish.nervousfish.data_objects.SimpleKey;
import com.nervousfish.nervousfish.service_locator.EntryActivity;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.internal.matchers.TypeSafeMatcher;

import cucumber.api.CucumberOptions;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

@CucumberOptions(features = "features")
public class ContactActivitySteps extends ActivityInstrumentationTestCase2<EntryActivity> {

    private Activity parent = null;

    public ContactActivitySteps(EntryActivity activityClass) {
        super(EntryActivity.class);
    }

    private static Matcher<? super View> hasErrorText(final String expectedError) {
        return new ErrorTextMatcher(expectedError);
    }

    @Given("^I am viewing the contact activity$")
    public void iAmViewingContactActivity() {
        assertNotNull(getActivity());

        IKey key = new SimpleKey("my key", "key");
        Contact contact = new Contact("Cornel",key );

        Intent intent = new Intent(getActivity(), ContactActivity.class);
        intent.putExtra(ConstantKeywords.CONTACT, contact);
        intent.putExtra(ConstantKeywords.SERVICE_LOCATOR,
                getCurrentActivity().getIntent().getSerializableExtra(ConstantKeywords.SERVICE_LOCATOR));
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

    @Then("^I should go to the previous activity I visited$")
    public void iShouldGoToPreviousActivity() {
        assertEquals(LoginActivity.class, getCurrentActivity().getClass());
    }

    @Then("^I should get a popup asking if I am sure to delete the contact$")
    public void iShouldGetPopup() {
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
