Feature: start Bluetooth Connection activity
    As a user I want to access my bluetooth features with one button press from the main activity

    Scenario: Press the connect button and go to the BluetoothConnectionActivity
        Given I am viewing the main activity
        When I press the connect fab
        And I select the Bluetooth option
        Then I go to the BluetoothConnectionActivity
