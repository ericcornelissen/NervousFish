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
import com.nervousfish.nervousfish.activities.MainActivity;
import com.nervousfish.nervousfish.activities.WaitForSlaveActivity;
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
public class WaitingForSlaveSteps extends ActivityInstrumentationTestCase2<EntryActivity> {
    private static final Activity[] activity = new Activity[1];

    public WaitingForSlaveSteps(EntryActivity activityClass) {
        super(EntryActivity.class);
    }
    public WaitingForSlaveSteps() {
        super(EntryActivity.class);
    }

    @Given("^I am viewing the waitingForSlave activity$")
    public void iAmViewingWaitingForSlaveActivity() {
        assertNotNull(getActivity());

        Intent intent = new Intent(getActivity(), WaitForSlaveActivity.class);
        intent.putExtra(ConstantKeywords.SERVICE_LOCATOR,
                getCurrentActivity().getIntent().getSerializableExtra(ConstantKeywords.SERVICE_LOCATOR));
        getActivity().startActivity(intent);
    }

    @When("^I press the cancel button$")
    public void iPressDeleteButton() {
        onView(withId(R.id.cancelWaitForSlave)).perform(click());
    }

    @Then("^I should go to the MainActivity$")
    public void iShouldGoToMainActivity() {
        assertEquals(MainActivity.class, getCurrentActivity().getClass());
    }


    private Activity getCurrentActivity() {
        getInstrumentation().waitForIdleSync();
        try {
            runTestOnUiThread(new GetCurrentActivityRunnable());
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return activity[0];
    }

    private static class GetCurrentActivityRunnable implements Runnable {
        @Override
        public void run() {
            java.util.Collection<Activity> activities = ActivityLifecycleMonitorRegistry.getInstance().getActivitiesInStage(Stage.RESUMED);
            activity[0] = Iterables.getOnlyElement(activities);
        }
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
