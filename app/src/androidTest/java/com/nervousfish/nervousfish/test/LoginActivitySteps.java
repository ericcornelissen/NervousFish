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
import com.nervousfish.nervousfish.activities.LoginActivity;
import com.nervousfish.nervousfish.activities.MainActivity;
import com.nervousfish.nervousfish.service_locator.EntryActivity;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.internal.matchers.TypeSafeMatcher;
import org.junit.runner.RunWith;

import cucumber.api.CucumberOptions;
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

@CucumberOptions(features = "features")
public class LoginActivitySteps extends ActivityInstrumentationTestCase2<EntryActivity> {

    public LoginActivitySteps(EntryActivity activityClass) {
        super(EntryActivity.class);
    }
    public LoginActivitySteps() {
        super(EntryActivity.class);
    }

    private static Matcher<? super View> hasErrorText(final String expectedError) {
        return new ErrorTextMatcher(expectedError);
    }

    @Given("^I have a LoginActivity")
    public void iHaveALoginActivity() {
        assertNotNull(getActivity());

        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.putExtra(ConstantKeywords.SERVICE_LOCATOR,
                getCurrentActivity().getIntent().getSerializableExtra(ConstantKeywords.SERVICE_LOCATOR));
        getActivity().startActivity(intent);
    }

    @When("^I input password \"(.*?)\"$")
    public void iInputPassword(final String password) {
        onView(withId(R.id.password)).perform(typeText(password));
    }

    @When("^I press submit button$")
    public void iPressSubmit() {
        onView(withId(R.id.submit)).perform(scrollTo()).perform(click());
    }

    @Then("^I (true|false) continue to the MainActivity$")
    public void iShouldContinueToNextActivity(boolean continuesToNextActivity) {
        if (continuesToNextActivity) {
            assertEquals(getCurrentActivity().getClass(), MainActivity.class);
        } else {
            assertEquals(getCurrentActivity().getClass(), LoginActivity.class);
        }
    }

    @Then("^I should see an auth error$")
    public void iShouldSeeAuthError() {
        onView(withId(R.id.error)).check(matches(isDisplayed()));
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
