package com.nervousfish.nervousfish.test;

import android.app.Activity;
import android.content.Intent;
import android.support.test.rule.ActivityTestRule;

import com.nervousfish.nervousfish.ConstantKeywords;
import com.nervousfish.nervousfish.R;
import com.nervousfish.nervousfish.activities.LoginActivity;
import com.nervousfish.nervousfish.activities.MainActivity;
import com.nervousfish.nervousfish.modules.database.IDatabase;
import com.nervousfish.nervousfish.service_locator.IServiceLocator;

import org.junit.Rule;

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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;

@CucumberOptions(features = "features")
public class LoginActivitySteps {

    public static final String TAG = LoginActivitySteps.class.getSimpleName();
    private static final String CORRECT_PASSWORD = "12345";

    @Rule
    public ActivityTestRule<LoginActivity> mActivityRule = new ActivityTestRule<>(LoginActivity.class);

    @Before
    public void setUp() throws Exception {
        IServiceLocator serviceLocator = mock(IServiceLocator.class, withSettings().serializable());
        IDatabase database = mock(IDatabase.class, withSettings().serializable());
        when(serviceLocator.getDatabase()).thenReturn(database);
        when(database.getUserPassword()).thenReturn(CORRECT_PASSWORD);

        Intent intent = new Intent();
        intent.putExtra(ConstantKeywords.SERVICE_LOCATOR, serviceLocator);

        mActivityRule.launchActivity(intent);
        mActivityRule.getActivity();
    }

    /**
     * All the clean up of application's data and state after each scenario must happen here
     */
    @After
    public void tearDown() throws Exception {

    }

    @Given("^I have a LoginActivity$")
    public void iHaveALoginActivity() {
        Activity activity = mActivityRule.getActivity();
        assertEquals(activity.getClass(), LoginActivity.class);
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
        Activity activity = mActivityRule.getActivity();
        if (continuesToNextActivity) {
            assertEquals(activity.getClass(), MainActivity.class);
        } else {
            assertEquals(activity.getClass(), LoginActivity.class);
        }
    }

    @Then("^I should see an auth error$")
    public void iShouldSeeAuthError() {
        onView(withId(R.id.error)).check(matches(isDisplayed()));
    }

}
