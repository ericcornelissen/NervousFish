Feature: Bluetooth connection service
  As a user
  I want to be able to pair devices using Bluetooth to exchange data
  So that I can exchange data with someone using my mobile device

  Scenario Outline: Start up a connection without any bonds
    Given I have a BluetoothConnectionActivity
    When I don't have an established connection and any bonded pairs yet
    Then the app should be able to pair with other devices over Bluetooth and setup a connection


  Scenario Outline: Start up a connection with the partner device already bonded
    Given I have a BluetoothConnectionActivity
    When I don't have an established connection yet but already have the necessary bonding
    Then the app should be able to setup a connection with the already bonded device

    Scenario Outline: Destroy an established connection
        Given I have a BluetoothConnectionActivity
        When I have a working connection and want to end the connection
        Then the app should be able to destroy the connection and free up unnecessary resources