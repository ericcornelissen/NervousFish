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
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.core.AllOf.allOf;

@CucumberOptions(features = "features")
public class ContactActivitySteps extends ActivityInstrumentationTestCase2<EntryActivity> {
    private static final Activity[] activity = new Activity[1];

    private Activity previousActivity = null;
    private Contact contact = null;
    private IServiceLocator serviceLocator = null;
    private final static String TEST_NAME = "TestPerson";
    private final static IKey TEST_KEY = new SimpleKey("my key", "key");

    public ContactActivitySteps() {
        super(EntryActivity.class);
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

        previousActivity = getCurrentActivity();
        Intent intent = new Intent(getActivity(), ContactActivity.class);
        intent.putExtra(ConstantKeywords.CONTACT, contact);
        intent.putExtra(ConstantKeywords.SERVICE_LOCATOR, serviceLocator);
        getCurrentActivity().startActivity(intent);
        assertTrue(getCurrentActivity() instanceof ContactActivity);
    }

    @When("^I press the back arrow$")
    public void iPressBackArrow() {
        onView(withId(R.id.backButtonChange)).perform(click());
    }

    @When("^I press the menu$")
    public void iPressMenu() {
        onView(withId(R.id.edit_menu_button)).perform(click());
    }

    @When("^I press delete$")
    public void iPressDeleteButton() {
        onView(withText("Delete")).perform(click());
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
        assertEquals(previousActivity.getClass(), getCurrentActivity().getClass());
    }

    @Then("^the current contact should be deleted$")
    public void currentContactIsDeleted() throws IOException {
        assertFalse(serviceLocator.getDatabase().getAllContacts().contains(contact));
    }

    @When("^I press edit$")
    public void iPressEdit() {
        onView(withText("Edit")).perform(click());
    }

    @Then("^I should go to the change contact activity$")
    public void iShouldGoToChangeContactActivity() {
        assertEquals(ChangeContactActivity.class, getCurrentActivity().getClass());
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
}
