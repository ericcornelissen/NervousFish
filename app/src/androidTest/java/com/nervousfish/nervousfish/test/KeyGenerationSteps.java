package com.nervousfish.nervousfish.test;


import android.test.ActivityInstrumentationTestCase2;
import android.view.View;
import android.widget.EditText;

import com.nervousfish.nervousfish.LoginActivity;
import com.nervousfish.nervousfish.R;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.internal.matchers.TypeSafeMatcher;

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
import static org.hamcrest.Matchers.not;

@SuppressWarnings("PMD")
@CucumberOptions(features = "features")
public class KeyGenerationSteps extends ActivityInstrumentationTestCase2<LoginActivity> {

    public KeyGenerationSteps(LoginActivity activityClass) {
        super(LoginActivity.class);
    }

    @Given("^I have a LoginActivity")
    public void iHaveALoginActivity() {
        assertNotNull(getActivity());
    }

    @When("^I don't have public/private key pair yet")
    public void iDontHaveKeyPairYet() {
    }

    @Then("^the app should generate a public/private key pair")
    public void theAppShouldGenerateKeyPair() {

    }

    private static Matcher<? super View> hasErrorText(String expectedError) {
        return new ErrorTextMatcher(expectedError);
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
            if(!(view instanceof EditText)) {
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
