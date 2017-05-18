package com.nervousfish.nervousfish.test;

import android.app.Activity;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.annotation.UiThreadTest;
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
import com.nervousfish.nervousfish.modules.database.IDatabase;
import com.nervousfish.nervousfish.service_locator.EntryActivity;
import com.nervousfish.nervousfish.service_locator.IServiceLocator;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.internal.matchers.TypeSafeMatcher;
import org.junit.runner.RunWith;

import java.io.IOException;

import cucumber.api.CucumberOptions;
import cucumber.api.android.CucumberInstrumentation;
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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;

@CucumberOptions(features = "features")
public class LoginActivitySteps extends ActivityInstrumentationTestCase2<LoginActivity> {

    private static final String CORRECT_PASSWORD = "12345";

    public LoginActivitySteps(LoginActivity activityClass) throws IOException {
        super(LoginActivity.class);

        injectInstrumentation(InstrumentationRegistry.getInstrumentation());

        IServiceLocator serviceLocator = mock(IServiceLocator.class, withSettings().serializable());
        IDatabase database = mock(IDatabase.class, withSettings().serializable());
        when(serviceLocator.getDatabase()).thenReturn(database);
        when(database.getUserPassword()).thenReturn(CORRECT_PASSWORD);

        Intent intent = new Intent();
        intent.putExtra(ConstantKeywords.SERVICE_LOCATOR, serviceLocator);
        setActivityIntent(intent);

        getActivity();
    }

    @Given("^I have a LoginActivity$")
    public void iHaveALoginActivity() {
        assertEquals(getCurrentActivity().getClass(), LoginActivity.class);
    }

    @When("^I input password \"(.*?)\"$")
    public void iInputPassword(final String password) {
        onView(withId(R.id.login_password_input)).perform(typeText(password));
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

    /* Helper methods */
    private static Matcher<? super View> hasErrorText(final String expectedError) {
        return new ErrorTextMatcher(expectedError);
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
