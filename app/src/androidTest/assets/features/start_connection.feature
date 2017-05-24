Feature: Starting a connection
    As a user I want to access my bluetooth features with one button press from the main activity

    Scenario: Start a Bluetooth connection
        Given I am viewing the main activity
        When I press the connect button
        And I select the Bluetooth as connection method
        Then I go to the BluetoothConnectionActivity
