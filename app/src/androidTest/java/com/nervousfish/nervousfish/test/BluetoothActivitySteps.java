package com.nervousfish.nervousfish.test;

import android.test.ActivityInstrumentationTestCase2;

import com.nervousfish.nervousfish.modules.pairing.BluetoothConnectionService;

import cucumber.api.CucumberOptions;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

/**
 * Created by Kilian on 3/05/2017.
 */

@SuppressWarnings("PMD")
@CucumberOptions(features = "features")
public class BluetoothActivitySteps extends ActivityInstrumentationTestCase2<BluetoothConnectionService> {

    public BluetoothActivitySteps(Class<BluetoothConnectionService> activityClass) {
        super(BluetoothConnectionService.class);
    }

    @Given("^I have a BluetoothConnectionService and don't have an established connection nor any pairing$")
    public void iHaveABluetoothConnectionActivityWithoutAConnection() {
        assertNotNull(getActivity());
    }

    @When("^$")
    public void iPressConnect() {
        assertTrue(true);
    }

    @Then("^$")
    public void iShouldStartAConnection() {
        assertTrue(true);
    }

    }
