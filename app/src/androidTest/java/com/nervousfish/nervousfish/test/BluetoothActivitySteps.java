package com.nervousfish.nervousfish.test;

import android.test.ActivityInstrumentationTestCase2;

import com.nervousfish.nervousfish.BluetoothConnectionActivity;

import cucumber.api.CucumberOptions;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

/**
 * Created by Kilian on 3/05/2017.
 */

@SuppressWarnings("PMD")
@CucumberOptions(features = "features")
public class BluetoothActivitySteps extends ActivityInstrumentationTestCase2<BluetoothConnectionActivity> {

    public BluetoothActivitySteps(Class<BluetoothConnectionActivity> activityClass) {
        super(BluetoothConnectionActivity.class);
    }

    @Given("^I have a BluetoothConnectionActivity and don't have an established connection nor any pairing$")
    public void iHaveABluetoothConnectionActivityWithoutAConnection() {
        assertNotNull(getActivity());
    }

    @When("^$")
    public void iPressConnect() {

    }

    @Then("^$")
    public void iShouldStartAConnection() {

    }

    }
