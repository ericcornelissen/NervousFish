Feature: Bluetooth connection service
  As a user
  I want to be able to pair devices using Bluetooth to exchange data
  So that I can exchange data with someone using my mobile device

  Scenario Outline: Connect with a device from the discovered devices list
    Given I have a BluetoothConnectionActivity and don't have an established connection
    When I press on a device from the paired list
    Then I start the connect process with that device

  Scenario Outline: Start up a connection with the partner device already bonded
    Given I have a BluetoothConnectionActivity
    And don't have an established connection but have made the necessary pairings already
    When I'm in the MainActivity and the connect button is pressed
    Then the app should be able to setup a connection with the already bonded device

  Scenario Outline: Destroy an established connection
    Given I have a BluetoothConnectionActivity with a working connection
    When I'm in the MainActivity and the disconnect button is pressed
    Then the app should be able to destroy the connection and free up unnecessary resources