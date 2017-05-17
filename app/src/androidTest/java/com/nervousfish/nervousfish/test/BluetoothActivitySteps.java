package com.nervousfish.nervousfish.test;

import android.app.Activity;
import android.test.ActivityInstrumentationTestCase2;

import com.nervousfish.nervousfish.activities.BluetoothConnectionActivity;
import com.nervousfish.nervousfish.service_locator.EntryActivity;

import cucumber.api.CucumberOptions;
import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

/**
 * Created by Kilian on 3/05/2017.
 */

@CucumberOptions(features = "features")
public class BluetoothActivitySteps extends ActivityInstrumentationTestCase2<BluetoothConnectionActivity> {

    private Activity mActivity = null;

    public BluetoothActivitySteps(EntryActivity activityClass) {
        super(BluetoothConnectionActivity.class);
    }

    @Before
    public void setUp() throws Exception {
        super.setUp();
        mActivity = getActivity(); // Start Activity before each test scenario
        assertNotNull(mActivity);
    }

    @After
    public void tearDown() throws Exception {
        getActivity().finish();
        super.tearDown(); // This step scrubs everything in this class so always call it last
    }

    @Given("^I have a BluetoothConnectionActivity and don't have an established connection$")
    public void iHaveABluetoothConnectionActivityWithoutAConnection() {
        assertNotNull(getActivity());

    }

    @When("^When I'm in the MainActivity and the connect button is pressed")
    public void iPressConnect() {
        assertTrue(true);
    }

    @Then("^$")
    public void iShouldStartAConnection() {
        assertTrue(true);
    }

}
